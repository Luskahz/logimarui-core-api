package com.logimarui.core.api.repository.read.jdbc.supervisor;

import com.logimarui.core.api.domain.read.Supervisor;
import com.logimarui.core.api.repository.read.SupervisorReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class SupervisorReadRepositoryJdbc implements SupervisorReadRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Supervisor> buscar(Long codigo) {
        String sql = """
            SELECT DISTINCT
                superv_rota as codigo,
                nome_superv_rota as nome,
                CASE superv_rota
                    WHEN 1 THEN CAST('09:00:00' AS TIME)
                    WHEN 2 THEN CAST('07:00:00' AS TIME)
                    WHEN 3 THEN CAST('07:30:00' AS TIME)
                    ELSE NULL
                END as matinal
            FROM
                `diretorio`.`03_11_29`
            WHERE superv_rota = ?;
        """;

        return jdbcTemplate.query(
            sql,
            new SupervisorRowMapper(),
            codigo
        ).stream().findFirst();
    }
}
