package com.example.ecogiro.repository;

import com.example.ecogiro.model.EcoGiroStore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EcoGiroStoreRepository extends JpaRepository<EcoGiroStore, Long> {
    List<EcoGiroStore> findBySectorId(Long sectorId);
}
