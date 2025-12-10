package com.medicina.citafacil.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medicina.citafacil.model.Appointment;
import com.medicina.citafacil.model.MedicalRecord;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.repository.AppointmentRepository;
import com.medicina.citafacil.repository.DoctorRepository;
import com.medicina.citafacil.repository.MedicalRecordRepository;
import com.medicina.citafacil.service.UserService;

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
    public String newAppointment(Authentication authentication, Model model) {
        Patient patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now());

        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorRepository.findAll());
        return "patient/appointment_form";
    }

    @PostMapping("/appointments")
    public String createAppointment(@ModelAttribute Appointment appointment,
                                    Authentication authentication,
                                    Model model) {
        Patient patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
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
}