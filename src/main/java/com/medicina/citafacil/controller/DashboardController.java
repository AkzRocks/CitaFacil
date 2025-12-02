package com.medicina.citafacil.controller;

import com.medicina.citafacil.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        if (request.isUserInRole("ADMIN")) {
            model.addAttribute("stats", dashboardService.getAdminStats());
            return "dashboard_admin";
        } else if (request.isUserInRole("DOCTOR")) {
            return "dashboard_doctor";
        } else if (request.isUserInRole("PATIENT")) {
            return "dashboard_patient";
        }
        return "redirect:/login";
    }
}
