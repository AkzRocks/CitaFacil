package com.medicina.citafacil.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @GetMapping("/patients")
    public String patients() {
        return "doctor/patients"; // Pendiente de crear vista
    }

    @GetMapping("/appointments")
    public String appointments() {
        return "doctor/appointments"; // Pendiente de crear vista
    }
}
