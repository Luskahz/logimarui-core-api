package com.logimarui.core.api.domain.read.entrega;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entrega {
    private Long codigoCliente;
    private List<Long> notasFiscais;
}
