package com.logimarui.authentication.infra.persistence.jpa;

import com.logimarui.authentication.infra.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long> {
    boolean existsByEmployeeIdAndAuthorizationTrue(Long employeeId);
    @Query("""
        select e.name
        from EmployeeEntity e
        where e.employeeId = :employeeId
    """)
    Optional<String> findNameByEmployeeId(@Param("employeeId") Long employeeId);
}

