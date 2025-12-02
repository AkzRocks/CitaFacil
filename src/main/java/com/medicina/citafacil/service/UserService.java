package com.medicina.citafacil.service;

import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.model.Role;
import com.medicina.citafacil.model.User;
import com.medicina.citafacil.repository.PatientRepository;
import com.medicina.citafacil.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerPatient(Patient patient, String rawPassword) {
        if (userRepository.existsByDni(patient.getDni())) {
            throw new RuntimeException("DNI already exists");
        }

        // Validar menor de edad
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        if (age < 18 && (patient.getParentDni() == null || patient.getParentDni().isEmpty())) {
            throw new RuntimeException("Parent DNI is required for minors");
        }

        patient.setPassword(passwordEncoder.encode(rawPassword));
        patient.setRole(Role.PATIENT);
        patientRepository.save(patient);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
