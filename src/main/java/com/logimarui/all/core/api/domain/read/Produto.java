package com.logimarui.all.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
   private Long codigo;
   private String descricao;
   private String embalagem;
   private BigDecimal peso;
}
