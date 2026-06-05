package com.geopulse.geopulse_api.zones;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZoneRepository extends JpaRepository<ZoneEntity, UUID> {
    List<ZoneEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    Optional<ZoneEntity> findByIdAndUserEmail(UUID id, String userEmail);

    boolean existsByIdAndUserEmail(UUID id, String userEmail);
}