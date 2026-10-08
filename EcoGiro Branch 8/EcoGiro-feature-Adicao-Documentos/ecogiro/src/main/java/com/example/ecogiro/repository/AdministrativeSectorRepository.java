package com.example.ecogiro.repository;

import com.example.ecogiro.model.AdministrativeSector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrativeSectorRepository extends JpaRepository<AdministrativeSector, Long> {
    boolean existsByCode(String code);
}
