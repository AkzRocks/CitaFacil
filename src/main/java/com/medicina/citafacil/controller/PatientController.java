package com.medicina.citafacil.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @GetMapping("/history")
    public String history() {
        return "patient/history"; // Pendiente de crear vista
    }

    @GetMapping("/appointments")
    public String appointments() {
        return "patient/appointments"; // Pendiente de crear vista
    }
}
