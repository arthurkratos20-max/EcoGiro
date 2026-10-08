package com.example.ecogiro.repository;

import com.example.ecogiro.model.SectorRedistributionProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectorRedistributionProposalRepository
        extends JpaRepository<SectorRedistributionProposal, Long> {
    List<SectorRedistributionProposal> findByStatusOrderByCreatedAtDesc(
            SectorRedistributionProposal.Status status);
}
