package com.medicina.citafacil.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DoctorsController {

    @GetMapping("/doctors")
    public String doctors() {
        return "doctors";
    }
}