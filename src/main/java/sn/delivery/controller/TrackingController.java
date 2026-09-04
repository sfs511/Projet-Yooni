package sn.delivery.controller;

import sn.delivery.dto.TrackingDTO;
import sn.delivery.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/{livreurId}")
    public ResponseEntity<TrackingDTO> updatePosition(@PathVariable Long livreurId, @RequestBody Map<String, Double> position) {
        return ResponseEntity.ok(trackingService.updatePosition(
                livreurId,
                position.get("latitude"),
                position.get("longitude"),
                position.getOrDefault("vitesse", 0.0),
                position.getOrDefault("direction", 0.0)
        ));
    }

    @GetMapping("/{livreurId}")
    public ResponseEntity<TrackingDTO> getDernierePosition(@PathVariable Long livreurId) {
        return trackingService.getDernierePosition(livreurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}