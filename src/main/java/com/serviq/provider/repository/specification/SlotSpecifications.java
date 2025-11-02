package com.serviq.provider.repository.specification;

import com.serviq.provider.dto.request.SlotSearchCriteria;
import com.serviq.provider.entity.AvailableSlot;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SlotSpecifications {

    public static Specification<AvailableSlot> withCriteria(SlotSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getOrgId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orgId"), criteria.getOrgId()));
            }

            if (criteria.getProviderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("providerId"), criteria.getProviderId()));
            }

            if (criteria.getServiceId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("serviceId"), criteria.getServiceId()));
            }

            if (criteria.getConfigId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("configId"), criteria.getConfigId()));
            }

            if (criteria.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("slotDate"), criteria.getStartDate()
                ));
            }

            if (criteria.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("slotDate"), criteria.getEndDate()
                ));
            }

            if (criteria.getIsBooked() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isBooked"), criteria.getIsBooked()));
            }

            if (Boolean.TRUE.equals(criteria.getAvailableOnly())) {
                predicates.add(criteriaBuilder.equal(root.get("isBooked"), false));
                predicates.add(criteriaBuilder.greaterThan(root.get("availableCapacity"), 0));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<AvailableSlot> isAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("isBooked"), false),
                        criteriaBuilder.greaterThan(root.get("availableCapacity"), 0)
                );
    }

    public static Specification<AvailableSlot> byProvider(UUID providerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("providerId"), providerId);
    }

    public static Specification<AvailableSlot> byService(UUID serviceId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("serviceId"), serviceId);
    }

    public static Specification<AvailableSlot> byOrganization(UUID orgId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("orgId"), orgId);
    }

    public static Specification<AvailableSlot> inDateRange(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("slotDate"), startDate, endDate);
    }
}
