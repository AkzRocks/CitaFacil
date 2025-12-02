package com.medicina.citafacil.controller;

import com.medicina.citafacil.model.Appointment;
import com.medicina.citafacil.model.Appointment.AppointmentStatus;
import com.medicina.citafacil.model.Doctor;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.repository.AppointmentRepository;
import com.medicina.citafacil.repository.PatientRepository;
import com.medicina.citafacil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
                                    Authentication authentication) {
        Doctor doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.PENDING);
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
