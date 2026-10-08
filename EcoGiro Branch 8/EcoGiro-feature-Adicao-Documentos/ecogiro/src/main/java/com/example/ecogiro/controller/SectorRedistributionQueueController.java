package com.example.ecogiro.controller;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.Role;
import com.example.ecogiro.model.SectorRedistributionProposal;
import com.example.ecogiro.repository.AppUserRepository;
import com.example.ecogiro.repository.SectorRedistributionProposalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/redistribution-proposals")
public class SectorRedistributionQueueController {
    private final AppUserRepository users;
    private final SectorRedistributionProposalRepository proposals;

    public SectorRedistributionQueueController(
            AppUserRepository users, SectorRedistributionProposalRepository proposals) {
        this.users = users;
        this.proposals = proposals;
    }

    public record ProposalView(Long id, Long sectorId, String sectorName,
                               Long fromAdministratorId, Long toAdministratorId,
                               Long requestedById, String reason, String status,
                               LocalDateTime createdAt) {}

    @GetMapping("/pending")
    @Transactional(readOnly = true)
    public List<ProposalView> pending(Authentication authentication) {
        if (authentication == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        AppUser actor = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!actor.isActive() || actor.getRole() != Role.ADMIN_GERAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return proposals.findByStatusOrderByCreatedAtDesc(
                    SectorRedistributionProposal.Status.PENDING).stream()
                .map(p -> new ProposalView(p.getId(), p.getSector().getId(),
                        p.getSector().getName(),
                        p.getFromAdministrator() == null ? null : p.getFromAdministrator().getId(),
                        p.getToAdministrator().getId(), p.getRequestedBy().getId(),
                        p.getReason(), p.getStatus().name(), p.getCreatedAt()))
                .toList();
    }
}
