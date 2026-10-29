package com.example.ecogiro.controller;

import com.example.ecogiro.model.PlanRequest;
import com.example.ecogiro.repository.PlanRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/** Consulta pública de protocolo com validação do CPF do titular. */
@RestController
@RequestMapping("/api/requests")
public class PublicRequestLookupController {
    private final PlanRequestRepository requests;

    public PublicRequestLookupController(PlanRequestRepository requests) {
        this.requests = requests;
    }

    @PostMapping("/lookup")
    public ResponseEntity<?> lookup(@RequestBody LookupInput input) {
        if (input == null || input.protocol() == null || input.cpf() == null) return notFound();
        String protocol = input.protocol().trim();
        String cpf = digits(input.cpf());
        if (protocol.isBlank() || protocol.length() > 64 || cpf.length() != 11) return notFound();

        PlanRequest request = requests.findByProtocolIgnoreCase(protocol).orElse(null);
        // Mesmo retorno para protocolo inexistente e CPF incorreto, evitando enumeração.
        if (request == null || !digits(request.getUser().getCpf()).equals(cpf)) return notFound();

        return ResponseEntity.ok(new LookupResult(
            request.getProtocol(), request.getPlan().getName(), request.getStatus().name(),
            request.getRequestedAt(), request.getRespondedAt(), request.getAdminResponse(),
            request.getVehicle() == null ? null : request.getVehicle().getModel()
        ));
    }

    private static ResponseEntity<Map<String, String>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Não foi possível localizar uma solicitação com esses dados."));
    }

    private static String digits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    public record LookupInput(String protocol, String cpf) {}
    public record LookupResult(String protocol, String planName, String status,
        LocalDateTime requestedAt, LocalDateTime respondedAt, String response, String vehicleModel) {}
}
