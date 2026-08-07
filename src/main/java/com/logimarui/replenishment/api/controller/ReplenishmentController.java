package com.logimarui.replenishment.api.controller;


import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/replenishments")
@AllArgsConstructor
@Validated
public class ReplenishmentController {

    @GetMapping
    public void findReplenishmentsByFilter(
            @NotNull Authentication authentication
    ){}

    @GetMapping("/{id}/ticket")
    public void findReplenishmentTicket(
            @NotNull Authentication authentication
    ){}

    @GetMapping("/me")
    public void findActorData(
            @NotNull Authentication authentication
    ){}

    @GetMapping("/line/{deliver-router-id}/lookup")
    public void loockupDeliverRouterData(
            @NotNull Authentication authentication
    ){}

    @PostMapping
    public void initReplenishment(//recebe os dados iniciais da replenishment e 1 linha de produto
            @NotNull Authentication authentication
    ){}

    @PostMapping("/line")
    public void commitLineReplenishment(
            // Commita uma nova linha na reposição criada
            // Anteriormente vai requerir o ID pela DTO
            // atualiza atributos de updatedAt
            @NotNull Authentication authentication
    ){}
    @PostMapping("/{id}/conclude")//esse conclude n ficou legal
    public void finishLineReplenishment(
            @NotNull Authentication authentication
    ){}

    @PatchMapping
    public void cancelReplenishment(
            @NotNull Authentication authentication
    ){}


    @PatchMapping("/line")
    public void updateReplenishmentLine(
            @NotNull Authentication authentication
    ){}

    @GetMapping("/history-pos")
    public void getHistporyReplenishmentFromPos(
            @NotNull Authentication authentication
    ){}






}
