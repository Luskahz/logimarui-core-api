package com.logimarui.occurrence.infra.jpa.repository;

import com.logimarui.occurrence.infra.jpa.entity.OccurrenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OccurrenceJpaRepository extends
        JpaRepository<OccurrenceEntity, Long>,
        JpaSpecificationExecutor<OccurrenceEntity> {
}
