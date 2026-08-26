package com.logimarui.occurrence.infra.jpa.adapter;

import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.core.domain.model.OccurrenceSearchCriteria;
import com.logimarui.occurrence.core.port.repository.OccurrenceRepository;
import com.logimarui.occurrence.infra.jpa.entity.OccurrenceEntity;
import com.logimarui.occurrence.infra.jpa.mapper.OccurrencePersistenceMapper;
import com.logimarui.occurrence.infra.jpa.repository.OccurrenceJpaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaOccurrenceRepositoryAdapter implements OccurrenceRepository {
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "customerId", "invoiceNumber", "type", "status",
            "problemResolved", "createdAt", "updatedAt"
    );

    private final OccurrenceJpaRepository jpaRepository;
    private final OccurrencePersistenceMapper mapper;

    @Override
    public Occurrence save(Occurrence occurrence) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(occurrence)));
    }

    @Override
    public Optional<Occurrence> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Occurrence> search(OccurrenceSearchCriteria criteria, Pageable pageable) {
        validateSort(pageable);
        Page<OccurrenceEntity> result = jpaRepository.findAll(
                specification(criteria),
                pageable
        );
        return result.map(mapper::toDomain);
    }

    private Specification<OccurrenceEntity> specification(OccurrenceSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.customerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), criteria.customerId()));
            }
            if (criteria.invoiceNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("invoiceNumber"), criteria.invoiceNumber()));
            }
            if (criteria.type() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), criteria.type()));
            }
            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.problemResolved() != null) {
                predicates.add(criteriaBuilder.equal(root.get("problemResolved"), criteria.problemResolved()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException(
                        "Unsupported occurrence sort field: " + order.getProperty()
                );
            }
        });
    }
}
