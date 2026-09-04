package sn.delivery.service;

import org.springframework.stereotype.Service;

@Service
public class OptimisationService {

    public double calculerPrix(double poids, double distanceKm) {
        double prixBase = 500;
        double prixParKg = 100;
        double prixParKm = 50;
        return prixBase + (poids * prixParKg) + (distanceKm * prixParKm);
    }

    public boolean verifierDisponibilite(String zone) {
        return zone != null && !zone.isEmpty();
    }
}