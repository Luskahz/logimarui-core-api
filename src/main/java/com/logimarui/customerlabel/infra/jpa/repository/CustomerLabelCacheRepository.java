package com.logimarui.customerlabel.infra.jpa.repository;

import com.logimarui.customerlabel.infra.jpa.entity.CustomerLabelCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerLabelCacheRepository extends JpaRepository<CustomerLabelCacheEntity, Long> {
    Optional<CustomerLabelCacheEntity> findFirstByOrderByGeneratedAtDesc();
}
