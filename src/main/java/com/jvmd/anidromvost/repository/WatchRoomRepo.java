package com.jvmd.anidromvost.repository;

import com.jvmd.anidromvost.model.WatchRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchRoomRepo extends JpaRepository<WatchRoom, Long> {
    Optional<WatchRoom> findByCodeAndActiveTrue(String code);
}
