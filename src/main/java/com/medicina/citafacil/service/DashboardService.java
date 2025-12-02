package com.medicina.citafacil.service;

import com.medicina.citafacil.model.Appointment;
import com.medicina.citafacil.repository.AppointmentRepository;
import com.medicina.citafacil.repository.DoctorRepository;
import com.medicina.citafacil.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDoctors", doctorRepository.count());
        stats.put("totalPatients", patientRepository.count());
        stats.put("totalAppointments", appointmentRepository.count());
        // Citas por mes (formato yyyy-MM)
        List<Appointment> allAppointments = appointmentRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> appointmentsPerMonth = allAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDate().format(formatter),
                        Collectors.counting()
                ));
        stats.put("appointmentsPerMonth", appointmentsPerMonth);
        return stats;
    }
}
