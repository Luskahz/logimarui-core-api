package com.logimarui.auth.api.controller;

import com.logimarui.auth.core.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class EmployeeController {

    AuthService authService;

    @GetMapping("/employees/{employeeId}/name")
    public String employeeName(
            @PathVariable Long employeeId
    ){

        return authService.nameFromEmployee(employeeId);
    }
}
