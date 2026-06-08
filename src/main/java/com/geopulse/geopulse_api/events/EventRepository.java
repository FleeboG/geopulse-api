package com.geopulse.geopulse_api.events;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    List<EventEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    List<EventEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    Optional<EventEntity> findFirstByUserEmailOrderByCreatedAtDesc(String userEmail);
}