package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
   private Long codigo;
   private String descricao;
   private String embalagem;
   private Float peso;
}
