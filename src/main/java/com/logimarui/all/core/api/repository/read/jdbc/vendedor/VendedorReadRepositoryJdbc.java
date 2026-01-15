package com.logimarui.all.core.api.repository.read.jdbc.vendedor;

import com.logimarui.all.core.api.domain.read.Vendedor;
import com.logimarui.all.core.api.repository.read.VendedorReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class VendedorReadRepositoryJdbc implements VendedorReadRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Vendedor> buscar(Long codigo){
        String sql = """
            SELECT
                cod as codigo,
                nome as nome,
                supervisor as area
                FROM diretorio.vendedores
                WHERE cod = ?;
        """;
        return jdbcTemplate.query(
                sql,
                new VendedorRowMapper(),
                codigo
        ).stream().findFirst();

    }
}
