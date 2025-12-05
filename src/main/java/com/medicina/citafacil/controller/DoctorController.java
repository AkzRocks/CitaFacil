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
    public String patients() {
        return "doctor/patients"; // Pendiente de crear vista detallada
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
    public String newAppointment(Authentication authentication, Model model) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(9, 0));

        model.addAttribute("appointment", appointment);
        // Pacientes disponibles; en un escenario real se podrían filtrar solo "sus" pacientes
        model.addAttribute("patients", patientRepository.findAll());
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

        MedicalRecord record = new MedicalRecord();
        record.setDate(java.time.LocalDateTime.now());
        record.setDoctor(doctor);
        record.setPatient(appointment.getPatient());
        record.setAppointment(appointment);

        model.addAttribute("appointment", appointment);
        model.addAttribute("record", record);
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

        record.setAppointment(appointment);
        record.setDoctor(doctor);
        record.setPatient(appointment.getPatient());
        if (record.getDate() == null) {
            record.setDate(java.time.LocalDateTime.now());
        }
        medicalRecordRepository.save(record);

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

    // Reprogramar cita
    @GetMapping("/appointments/{id}/reschedule")
    public String rescheduleForm(@PathVariable Long id,
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

        model.addAttribute("appointment", appointment);
        return "doctor/appointment_reschedule_form";
    }

    @PostMapping("/appointments/{id}/reschedule")
    public String reschedule(@PathVariable Long id,
                             @ModelAttribute Appointment form,
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

        boolean busy = appointmentRepository.existsByDoctorAndDateAndTimeAndStatusNot(
                doctor,
                form.getRescheduledDate(),
                form.getRescheduledTime(),
                AppointmentStatus.CANCELLED
        );

        if (busy) {
            model.addAttribute("appointment", appointment);
            model.addAttribute("errorMessage", "Ya existe una cita en esa nueva fecha y hora. Elige otro horario.");
            return "doctor/appointment_reschedule_form";
        }

        appointment.setRescheduledDate(form.getRescheduledDate());
        appointment.setRescheduledTime(form.getRescheduledTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointmentRepository.save(appointment);
        return "redirect:/doctor/appointments";
    }

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
}
