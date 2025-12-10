package com.medicina.citafacil.controller;

import com.medicina.citafacil.model.MedicalRecord;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.service.DashboardService;
import com.medicina.citafacil.service.UserService;
import com.medicina.citafacil.repository.MedicalRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        if (request.isUserInRole("ADMIN")) {
            model.addAttribute("stats", dashboardService.getAdminStats());
            return "dashboard_admin";
        } else if (request.isUserInRole("DOCTOR")) {
            return "dashboard_doctor";
        } else if (request.isUserInRole("PATIENT")) {
            // Cargar paciente actual y su evolución clínica
            String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
            if (username != null) {
                var user = userService.findByUsername(username);
                if (user instanceof Patient patient) {
                    List<MedicalRecord> records = medicalRecordRepository.findByPatientOrderByDateDesc(patient);

                    // Tomar los últimos 6 registros y ordenarlos de antiguo a reciente para la gráfica
                    List<MedicalRecord> lastRecords = records.stream()
                            .limit(6)
                            .collect(Collectors.toList());
                    Collections.reverse(lastRecords);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                    List<String> labels = lastRecords.stream()
                            .map(r -> r.getDate().format(formatter))
                            .collect(Collectors.toList());
                    List<Double> weights = lastRecords.stream()
                            .map(MedicalRecord::getWeight)
                            .collect(Collectors.toList());
                    List<Double> hemoglobins = lastRecords.stream()
                            .map(MedicalRecord::getHemoglobin)
                            .collect(Collectors.toList());
                    List<Double> triglycerides = lastRecords.stream()
                            .map(MedicalRecord::getTriglycerides)
                            .collect(Collectors.toList());
                    List<Double> bloodSugars = lastRecords.stream()
                            .map(MedicalRecord::getBloodSugar)
                            .collect(Collectors.toList());

                    model.addAttribute("labels", labels);
                    model.addAttribute("weights", weights);
                    model.addAttribute("hemoglobins", hemoglobins);
                    model.addAttribute("triglycerides", triglycerides);
                    model.addAttribute("bloodSugars", bloodSugars);
                }
            }
            return "dashboard_patient";
        }
        return "redirect:/login";
    }
}
