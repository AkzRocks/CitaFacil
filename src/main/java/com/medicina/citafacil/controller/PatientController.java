package com.medicina.citafacil.controller;

import com.medicina.citafacil.model.Appointment;
import com.medicina.citafacil.model.MedicalRecord;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.model.Doctor;
import com.medicina.citafacil.repository.AppointmentRepository;
import com.medicina.citafacil.repository.MedicalRecordRepository;
import com.medicina.citafacil.repository.DoctorRepository;
import com.medicina.citafacil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/history")
    public String history(Authentication authentication, Model model) {
        Patient patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        List<MedicalRecord> records = medicalRecordRepository.findByPatientOrderByDateDesc(patient);
        model.addAttribute("records", records);
        return "patient/history";
    }

    @GetMapping("/appointments")
    public String appointments(Authentication authentication, Model model) {
        Patient patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        List<Appointment> upcoming = appointmentRepository.findByPatient(patient).stream()
                .filter(a -> !a.getDate().isBefore(LocalDate.now()))
                .toList();

        model.addAttribute("appointments", upcoming);
        return "patient/appointments";
    }

    @GetMapping("/appointments/new")
    public String newAppointment(Authentication authentication,
                                 @RequestParam(value = "doctorId", required = false) Long doctorId,
                                 @RequestParam(value = "date", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Model model) {
        Patient patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();
        // Solo permitir reservar a partir de mañana (fecha > hoy)
        if (date == null || !date.isAfter(today)) {
            date = today.plusDays(1);
            model.addAttribute("errorMessage", "Solo puedes reservar citas a partir de mañana.");
        }

        Appointment appointment = new Appointment();
        appointment.setDate(date);

        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("searchDate", date);
        model.addAttribute("searchDoctorId", doctorId);

        if (doctorId != null) {
            Doctor selectedDoctor = doctors.stream()
                    .filter(d -> d.getId().equals(doctorId))
                    .findFirst()
                    .orElse(null);
            if (selectedDoctor != null) {
                appointment.setDoctor(selectedDoctor);
                List<LocalTime> availableSlots = getAvailableSlotsForDoctorAndDate(selectedDoctor, date);
                model.addAttribute("timeSlots", availableSlots);
            }
        }

        model.addAttribute("appointment", appointment);
        return "patient/appointment_form";
    }

    @PostMapping("/appointments")
    public String createAppointment(@ModelAttribute Appointment appointment,
                                    @RequestParam("doctorId") Long doctorId,
                                    Authentication authentication,
                                    Model model) {
        Patient patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        // Cargar el doctor seleccionado a partir del doctorId enviado por el formulario
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        appointment.setDoctor(doctor);

        // Validación defensiva: asegurarnos de que el doctor no sea nulo
        if (appointment.getDoctor() == null) {
            model.addAttribute("appointment", appointment);
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("searchDoctorId", null);
            model.addAttribute("searchDate", LocalDate.now());
            model.addAttribute("errorMessage", "Debes seleccionar un doctor válido.");
            return "patient/appointment_form";
        }

        // Validar que el doctor esté libre en esa fecha y hora (ignorando citas canceladas)
        boolean busy = appointmentRepository.existsByDoctorAndDateAndTimeAndStatusNot(
                appointment.getDoctor(),
                appointment.getDate(),
                appointment.getTime(),
                Appointment.AppointmentStatus.CANCELLED
        );

        if (busy) {
            model.addAttribute("appointment", appointment);
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("searchDoctorId", appointment.getDoctor() != null ? appointment.getDoctor().getId() : null);
            model.addAttribute("searchDate", appointment.getDate());
            model.addAttribute("timeSlots", getAvailableSlotsForDoctorAndDate(appointment.getDoctor(), appointment.getDate()));
            model.addAttribute("errorMessage", "El doctor ya tiene una cita en esa fecha y hora. Por favor elige otro horario.");
            return "patient/appointment_form";
        }

        appointment.setPatient(patient);
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
        return "redirect:/patient/appointments";
    }

    private Patient getCurrentPatient(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        String username = authentication.getName();
        var user = userService.findByUsername(username);
        if (user instanceof Patient) {
            return (Patient) user;
        }
        return null;
    }

    private List<LocalTime> getAvailableSlotsForDoctorAndDate(Doctor doctor, LocalDate date) {
        if (doctor == null || date == null) {
            return List.of();
        }

        var dayOfWeek = date.getDayOfWeek();
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
        java.util.ArrayList<LocalTime> result = new java.util.ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            LocalTime start = schedule.getStartTime();
            LocalTime end = schedule.getEndTime();
            int slotMinutes = schedule.getSlotMinutes();

            LocalTime t = start;
            while (!t.isAfter(end.minusMinutes(slotMinutes))) {
                // No incluir horarios ya pasados en el día actual
                if (date.equals(LocalDate.now()) && t.isBefore(LocalTime.now())) {
                    t = t.plusMinutes(slotMinutes);
                    continue;
                }
                boolean occupied = appointmentRepository.existsByDoctorAndDateAndTimeAndStatusNot(
                        doctor,
                        date,
                        t,
                        Appointment.AppointmentStatus.CANCELLED
                );
                if (!occupied) {
                    result.add(t);
                }
                t = t.plusMinutes(slotMinutes);
            }
        }
        return result;
    }
}