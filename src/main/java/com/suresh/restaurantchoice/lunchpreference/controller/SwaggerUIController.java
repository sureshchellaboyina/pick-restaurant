package com.suresh.restaurantchoice.lunchpreference.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerUIController {
    @GetMapping("/swagger-ui")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html";
    }
}

