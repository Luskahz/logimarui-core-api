package com.logimarui.platform.openapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomSwaggerUiController {

    @GetMapping("/docs")
    public String docs() {
        return "forward:/openapi-custom/index.html";
    }
}