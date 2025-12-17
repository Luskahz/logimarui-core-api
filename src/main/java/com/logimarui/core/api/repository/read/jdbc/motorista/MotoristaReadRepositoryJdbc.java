package com.logimarui.core.api.repository.read.jdbc.motorista;

import com.logimarui.core.api.domain.read.Motorista;
import com.logimarui.core.api.repository.read.MotoristaReadRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

public class MotoristaReadRepositoryJdbc implements MotoristaReadRepository {

    private final JdbcTemplate jdbcTemplate;
    public MotoristaReadRepositoryJdbc(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Optional<Motorista> buscar(Long codigo) {
        String sql = """
            SELECT
                mt.cod_motorista as codigo,
                mat.matricula as matricula,
                mt.nome_motorista as nome,
                mt.cpf as cpf,
                mt.cluster as cluster
            FROM diretorio.motoristas mt
            LEFT JOIN diretorio.matricula_motoristas mat
                ON mt.cod_motorista = mat.cod_motorista; 
        """;
        return jdbcTemplate.query(
                sql,
                new MotoristaRowMapper(),
                codigo
        ).stream().findFirst();
    }
}
