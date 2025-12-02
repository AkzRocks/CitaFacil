package com.medicina.citafacil.controller;

import com.medicina.citafacil.model.Doctor;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.model.Role;
import com.medicina.citafacil.repository.DoctorRepository;
import com.medicina.citafacil.repository.PatientRepository;
import com.medicina.citafacil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===== Doctores =====

    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        return "admin/doctors";
    }

    @GetMapping("/doctors/new")
    public String newDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "admin/doctor_form";
    }

    @PostMapping("/doctors")
    public String createDoctor(@ModelAttribute Doctor doctor, @RequestParam("rawPassword") String rawPassword) {
        doctor.setRole(Role.DOCTOR);
        doctor.setPassword(passwordEncoder.encode(rawPassword));
        doctorRepository.save(doctor);
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors/{id}/edit")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        if (doctorOpt.isEmpty()) {
            return "redirect:/admin/doctors";
        }
        model.addAttribute("doctor", doctorOpt.get());
        return "admin/doctor_form";
    }

    @PostMapping("/doctors/{id}")
    public String updateDoctor(@PathVariable Long id,
                               @ModelAttribute Doctor doctor,
                               @RequestParam(value = "rawPassword", required = false) String rawPassword) {
        doctor.setId(id);
        doctor.setRole(Role.DOCTOR);
        if (rawPassword != null && !rawPassword.isBlank()) {
            doctor.setPassword(passwordEncoder.encode(rawPassword));
        }
        doctorRepository.save(doctor);
        return "redirect:/admin/doctors";
    }

    @PostMapping("/doctors/{id}/delete")
    public String deleteDoctor(@PathVariable Long id) {
        doctorRepository.deleteById(id);
        return "redirect:/admin/doctors";
    }

    // ===== Pacientes =====

    @GetMapping("/patients")
    public String listPatients(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        return "admin/patients";
    }

    @GetMapping("/patients/new")
    public String newPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "admin/patient_form";
    }

    @PostMapping("/patients")
    public String createPatient(@ModelAttribute Patient patient,
                                @RequestParam("rawPassword") String rawPassword) {
        // Reutilizamos la lógica de UserService para validaciones (edad, parentDni, etc.)
        userService.registerPatient(patient, rawPassword);
        return "redirect:/admin/patients";
    }

    @GetMapping("/patients/{id}/edit")
    public String editPatientForm(@PathVariable Long id, Model model) {
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isEmpty()) {
            return "redirect:/admin/patients";
        }
        model.addAttribute("patient", patientOpt.get());
        return "admin/patient_form";
    }

    @PostMapping("/patients/{id}")
    public String updatePatient(@PathVariable Long id,
                                @ModelAttribute Patient patient,
                                @RequestParam(value = "rawPassword", required = false) String rawPassword) {
        patient.setId(id);
        if (rawPassword != null && !rawPassword.isBlank()) {
            patient.setPassword(passwordEncoder.encode(rawPassword));
        }
        patient.setRole(Role.PATIENT);
        patientRepository.save(patient);
        return "redirect:/admin/patients";
    }

    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
        return "redirect:/admin/patients";
    }
}
