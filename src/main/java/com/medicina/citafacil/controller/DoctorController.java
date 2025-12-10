package com.medicina.citafacil.controller;

import com.medicina.citafacil.model.Appointment;
import com.medicina.citafacil.model.Appointment.AppointmentStatus;
import com.medicina.citafacil.model.Doctor;
import com.medicina.citafacil.model.MedicalRecord;
import com.medicina.citafacil.repository.AppointmentRepository;
import com.medicina.citafacil.repository.MedicalRecordRepository;
import com.medicina.citafacil.repository.PatientRepository;
import com.medicina.citafacil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping("/patients")
    public String patients(Authentication authentication,
                           @org.springframework.web.bind.annotation.RequestParam(value = "q", required = false) String query,
                           Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        // Obtener todas las citas del doctor y derivar los pacientes únicos
        java.util.List<Appointment> doctorAppointments = appointmentRepository.findByDoctor(doctor);
        java.util.LinkedHashSet<com.medicina.citafacil.model.Patient> patients = new java.util.LinkedHashSet<>();
        for (Appointment a : doctorAppointments) {
            if (a.getPatient() != null) {
                patients.add(a.getPatient());
            }
        }

        // Filtro por nombre o DNI si se proporciona 'q'
        java.util.List<com.medicina.citafacil.model.Patient> filtered = new java.util.ArrayList<>();
        String q = query != null ? query.trim().toLowerCase() : null;
        for (com.medicina.citafacil.model.Patient p : patients) {
            if (q == null || q.isEmpty()) {
                filtered.add(p);
            } else {
                String name = p.getFullName() != null ? p.getFullName().toLowerCase() : "";
                String dni = p.getDni() != null ? p.getDni().toLowerCase() : "";
                if (name.contains(q) || dni.contains(q)) {
                    filtered.add(p);
                }
            }
        }

        model.addAttribute("patients", filtered);
        model.addAttribute("q", query);
        return "doctor/patients";
    }

    // Ver historial médico de un paciente atendido por el doctor
    @GetMapping("/patients/{patientId}/history")
    public String patientHistory(@PathVariable Long patientId,
                                 Authentication authentication,
                                 Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        var patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            return "redirect:/doctor/patients";
        }
        var patient = patientOpt.get();

        // Opcional: podríamos validar que el doctor tenga al menos una cita con este paciente
        java.util.List<MedicalRecord> records = medicalRecordRepository.findByPatientOrderByDateDesc(patient);

        model.addAttribute("patient", patient);
        model.addAttribute("records", records);
        return "doctor/patient_history";
    }

    // Listar citas futuras del doctor autenticado
    @GetMapping("/appointments")
    public String appointments(Authentication authentication, Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        List<Appointment> futureAppointments = appointmentRepository.findByDoctor(doctor).stream()
                .filter(a -> !a.getDate().isBefore(LocalDate.now()))
                .toList();

        model.addAttribute("appointments", futureAppointments);
        return "doctor/appointments";
    }

    // Formulario para crear una nueva cita futura
    @GetMapping("/appointments/new")
    public String newAppointment(Authentication authentication,
                                @org.springframework.web.bind.annotation.RequestParam(value = "patientId", required = false) Long patientId,
                                Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Appointment appointment = new Appointment();
        // Por defecto, sugerir mañana como fecha de cita
        appointment.setDate(LocalDate.now().plusDays(1));
        appointment.setTime(LocalTime.of(9, 0));

        if (patientId != null) {
            patientRepository.findById(patientId).ifPresent(appointment::setPatient);
        }

        model.addAttribute("appointment", appointment);
        // Pacientes disponibles; en un escenario real se podrían filtrar solo "sus" pacientes
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("timeSlots", getDefaultTimeSlots());
        return "doctor/appointment_form";
    }

    // Guardar nueva cita futura
    @PostMapping("/appointments")
    public String createAppointment(@ModelAttribute Appointment appointment,
                                    Authentication authentication,
                                    Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        // Validar que la fecha sea posterior a hoy
        LocalDate today = LocalDate.now();
        if (appointment.getDate() == null || !appointment.getDate().isAfter(today)) {
            appointment.setDoctor(doctor);
            model.addAttribute("appointment", appointment);
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("timeSlots", getDefaultTimeSlots());
            model.addAttribute("errorMessage", "Solo puedes crear citas en fechas posteriores a hoy.");
            return "doctor/appointment_form";
        }

        // Validar disponibilidad del doctor en fecha/hora
        boolean busy = appointmentRepository.existsByDoctorAndDateAndTimeAndStatusNot(
                doctor,
                appointment.getDate(),
                appointment.getTime(),
                AppointmentStatus.CANCELLED
        );

        if (busy) {
            appointment.setDoctor(doctor);
            model.addAttribute("appointment", appointment);
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("timeSlots", getDefaultTimeSlots());
            model.addAttribute("errorMessage", "Ya existe una cita en esa fecha y hora para este doctor. Elige otro horario.");
            return "doctor/appointment_form";
        }

        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
        return "redirect:/doctor/appointments";
    }

    // Marcar cita como completada y mostrar formulario de historial médico
    @GetMapping("/appointments/{id}/complete")
    public String completeAppointmentForm(@PathVariable Long id,
                                          Authentication authentication,
                                          Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }

        // Si ya existe un registro médico para esta cita, lo reutilizamos para edición
        java.util.Optional<MedicalRecord> existingOpt = medicalRecordRepository.findByAppointment(appointment);
        MedicalRecord record;

        java.util.List<MedicalRecord> history = medicalRecordRepository
                .findByPatientOrderByDateDesc(appointment.getPatient());

        if (existingOpt.isPresent()) {
            record = existingOpt.get();
        } else {
            // Crear un nuevo registro precargando métricas desde el último historial del paciente (si existe)
            record = new MedicalRecord();
            record.setDate(java.time.LocalDateTime.now());
            record.setDoctor(doctor);
            record.setPatient(appointment.getPatient());
            record.setAppointment(appointment);

            if (!history.isEmpty()) {
                MedicalRecord last = history.get(0);
                record.setWeight(last.getWeight());
                record.setBloodSugar(last.getBloodSugar());
                record.setHemoglobin(last.getHemoglobin());
                record.setTriglycerides(last.getTriglycerides());
                record.setHeight(last.getHeight());
                record.setCholesterol(last.getCholesterol());
                record.setBmi(last.getBmi());
            }
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("record", record);
        model.addAttribute("lastRecord", history.isEmpty() ? null : history.get(0));
        return "doctor/appointment_record_form";
    }

    @PostMapping("/appointments/{id}/complete")
    public String saveCompletedAppointment(@PathVariable Long id,
                                           @ModelAttribute("record") MedicalRecord record,
                                           Authentication authentication) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/appointments";
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // Cargar registro existente (si lo hay) y hacer actualización parcial
        MedicalRecord existing = medicalRecordRepository.findByAppointment(appointment)
                .orElseGet(() -> {
                    MedicalRecord r = new MedicalRecord();
                    r.setAppointment(appointment);
                    r.setDoctor(doctor);
                    r.setPatient(appointment.getPatient());
                    return r;
                });

        // Campos de texto: solo reemplazar si se envía algo
        if (record.getDiagnosis() != null && !record.getDiagnosis().isBlank()) {
            existing.setDiagnosis(record.getDiagnosis());
        }
        if (record.getTreatment() != null && !record.getTreatment().isBlank()) {
            existing.setTreatment(record.getTreatment());
        }
        if (record.getNotes() != null && !record.getNotes().isBlank()) {
            existing.setNotes(record.getNotes());
        }

        // Métricas: solo reemplazar si no son null
        if (record.getWeight() != null) {
            existing.setWeight(record.getWeight());
        }
        if (record.getBloodSugar() != null) {
            existing.setBloodSugar(record.getBloodSugar());
        }
        if (record.getHemoglobin() != null) {
            existing.setHemoglobin(record.getHemoglobin());
        }
        if (record.getTriglycerides() != null) {
            existing.setTriglycerides(record.getTriglycerides());
        }
        if (record.getHeight() != null) {
            existing.setHeight(record.getHeight());
        }
        if (record.getCholesterol() != null) {
            existing.setCholesterol(record.getCholesterol());
        }
        if (record.getBmi() != null) {
            existing.setBmi(record.getBmi());
        }

        if (record.getDate() != null) {
            existing.setDate(record.getDate());
        } else if (existing.getDate() == null) {
            existing.setDate(java.time.LocalDateTime.now());
        }

        medicalRecordRepository.save(existing);

        return "redirect:/doctor/appointments";
    }

    // Marcar cita como no asistida
    @PostMapping("/appointments/{id}/no-show")
    public String markNoShow(@PathVariable Long id,
                             Authentication authentication) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment != null && appointment.getDoctor().getId().equals(doctor.getId())) {
            appointment.setStatus(AppointmentStatus.NO_SHOW);
            appointmentRepository.save(appointment);
        }
        return "redirect:/doctor/appointments";
    }

    // Reprogramación deshabilitada por ahora; se mantiene solo crear, completar y marcar no-show

    private Doctor getCurrentDoctor(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        String username = authentication.getName();
        var user = userService.findByUsername(username);
        if (user instanceof Doctor) {
            return (Doctor) user;
        }
        return null;
    }

    // Horario base simple: slots cada 30 minutos de 09:00 a 17:00
    private List<LocalTime> getDefaultTimeSlots() {
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);
        java.util.ArrayList<LocalTime> slots = new java.util.ArrayList<>();
        LocalTime t = start;
        while (!t.isAfter(end.minusMinutes(30))) {
            slots.add(t);
            t = t.plusMinutes(30);
        }
        return slots;
    }
}
