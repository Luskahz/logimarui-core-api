package com.logimarui.core.api.repository.read.jdbc.cliente;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.repository.read.ClienteReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ClienteReadRepositoryJdbc implements ClienteReadRepository {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Optional<Cliente> buscar(Long codigo){
        String sql = """
            SELECT
                cl.codigo_cliente AS codigo,
                CASE
                    WHEN diretorio.valida_cpf(RIGHT(cl.cnpj, 11)) THEN RIGHT(cl.cnpj, 11)
                    ELSE NULL
                END AS cpf,
                CASE
                    WHEN diretorio.valida_cnpj(RIGHT(cl.cnpj, 14)) THEN RIGHT(cl.cnpj, 14)
                    ELSE NULL
                END AS cnpj,
                cl.nome_fantasia AS fantasia,
                cl.razao_social,
                cr.latitude,
                cr.longitude
            FROM diretorio.01_20_11 cl
            JOIN diretorio.clientes_roteirizador cr 
                on cl.codigo_cliente = cr.codigo_cliente
            WHERE 
                cl.codigo_cliente = ?;       
        """;

            return jdbcTemplate.query(
                    sql,
                    new ClienteRowMapper(),
                    codigo
            ).stream().findFirst();
    }

}
