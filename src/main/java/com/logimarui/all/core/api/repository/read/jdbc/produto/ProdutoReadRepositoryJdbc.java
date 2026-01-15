package com.logimarui.all.core.api.repository.read.jdbc.produto;

import com.logimarui.all.core.api.domain.read.Produto;
import com.logimarui.all.core.api.repository.read.ProdutoReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
@AllArgsConstructor
public class ProdutoReadRepositoryJdbc implements ProdutoReadRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Produto> buscar(Long codigo){
        String sql = """
            SELECT
                codigo,
                descricao,
                embalagem,
                peso_bruto_kg as peso
            FROM `diretorio`.`01_11`
            WHERE codigo = ?;
        """;
        return jdbcTemplate.query(
                sql,
                new ProdutoRowMapper(),
                codigo
        ).stream().findFirst();

    }
}
