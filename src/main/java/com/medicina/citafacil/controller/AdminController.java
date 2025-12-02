package com.medicina.citafacil.controller;

import com.medicina.citafacil.model.Doctor;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.model.Role;
import com.medicina.citafacil.repository.AppointmentRepository;
import com.medicina.citafacil.repository.DoctorRepository;
import com.medicina.citafacil.repository.PatientRepository;
import com.medicina.citafacil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private AppointmentRepository appointmentRepository;

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
    public String createDoctor(@ModelAttribute("doctor") Doctor doctor,
                               BindingResult bindingResult,
                               @RequestParam("rawPassword") String rawPassword,
                               Model model) {

        validateDoctorForm(doctor, rawPassword, null, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/doctor_form";
        }

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
                               @ModelAttribute("doctor") Doctor doctor,
                               BindingResult bindingResult,
                               @RequestParam(value = "rawPassword", required = false) String rawPassword,
                               Model model) {
        // Cargar el doctor existente para conservar campos no editados (como password si no se cambia)
        Optional<Doctor> existingOpt = doctorRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return "redirect:/admin/doctors";
        }

        Doctor existing = existingOpt.get();

        validateDoctorForm(doctor, rawPassword, id, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", doctor);
            return "admin/doctor_form";
        }
        existing.setDni(doctor.getDni());
        existing.setFullName(doctor.getFullName());
        existing.setUsername(doctor.getUsername());
        existing.setSpecialty(doctor.getSpecialty());
        existing.setCmp(doctor.getCmp());
        existing.setRole(Role.DOCTOR);

        if (rawPassword != null && !rawPassword.isBlank()) {
            existing.setPassword(passwordEncoder.encode(rawPassword));
        }

        doctorRepository.save(existing);
        return "redirect:/admin/doctors";
    }

    private void validateDoctorForm(Doctor doctor,
                                    String rawPassword,
                                    Long currentDoctorId,
                                    BindingResult bindingResult) {

        // Contraseña requerida al crear
        if (currentDoctorId == null && (rawPassword == null || rawPassword.isBlank())) {
            bindingResult.rejectValue("password", "password.required", "La contraseña es requerida para crear un doctor");
        }

        String cmp = doctor.getCmp();
        if (cmp == null || !cmp.matches("\\d{9}")) {
            bindingResult.rejectValue("cmp", "cmp.invalid", "El CMP debe tener exactamente 9 dígitos numéricos");
        } else {
            boolean exists = doctorRepository.existsByCmp(cmp);
            if (exists) {
                if (currentDoctorId != null) {
                    var current = doctorRepository.findById(currentDoctorId);
                    if (current.isPresent() && cmp.equals(current.get().getCmp())) {
                        return; // mismo CMP del mismo médico
                    }
                }
                bindingResult.rejectValue("cmp", "cmp.duplicate", "Ya existe un médico con el mismo CMP");
            }
        }
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

    // ===== Citas =====

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepository.findAll());
        return "admin/appointments";
    }
}
