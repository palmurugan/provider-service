package com.serviq.provider.repository;

import com.serviq.provider.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findByIsActiveTrue();

    List<Location> findByCityAndIsActiveTrue(String city);

    List<Location> findByStateAndIsActiveTrue(String state);

    Optional<Location> findByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

    boolean existsByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

}
