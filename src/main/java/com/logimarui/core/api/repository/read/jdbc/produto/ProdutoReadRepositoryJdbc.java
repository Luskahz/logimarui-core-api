package com.logimarui.core.api.repository.read.jdbc.produto;

import com.logimarui.core.api.domain.read.Produto;
import com.logimarui.core.api.repository.read.ProdutoReadRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoReadRepositoryJdbc implements ProdutoReadRepository {
    private final JdbcTemplate jdbcTemplate;
    public ProdutoReadRepositoryJdbc(JdbcTemplate jdbcTemplate){this.jdbcTemplate = jdbcTemplate; }

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
        List<Produto> resultado = jdbcTemplate.query(
                sql,
                new ProdutoRowMapper(),
                codigo
        );
        return resultado.stream().findFirst();


    }
}
