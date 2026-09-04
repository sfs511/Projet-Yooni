@RestController
@RequestMapping("/livraisons")
@CrossOrigin(origins = "*")
public class LivraisonController {
    
    @Autowired
    private LivraisonService livraisonService;
    
    @Autowired
    private LivreurService livreurService;
    
    /**
     * Client crée une nouvelle livraison
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> creerLivraison(
        @RequestBody LivraisonDTO dto,
        @AuthenticationPrincipal UserDetails userDetails) {
        
        Client client = (Client) userDetails;
        Livraison livraison = livraisonService.creerLivraison(dto, client);
        
        // Trouver livreurs disponibles et les notifier
        List<Livreur> livreurs = livraisonService.trouverLiveursDisponibles(
            dto.getLatDepart(), dto.getLonDepart(), 5.0 // rayon 5km
        );
        
        return ResponseEntity.ok(livraison);
    }
    
    /**
     * Livreur accepte une livraison
     */
    @PostMapping("/{id}/accepter")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<?> accepterLivraison(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails) {
        
        Livraison livraison = livraisonService.getLivraisonById(id);
        Livreur livreur = (Livreur) userDetails;
        
        livraisonService.accepterLivraison(livraison, livreur);
        
        return ResponseEntity.ok("Livraison acceptée");
    }
    
    /**
     * Obtenir détails livraison + tracking
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLivraison(@PathVariable Long id) {
        Livraison livraison = livraisonService.getLivraisonById(id);
        return ResponseEntity.ok(livraison);
    }
}