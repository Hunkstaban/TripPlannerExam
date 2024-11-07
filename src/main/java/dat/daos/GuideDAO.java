package dat.daos;

import dat.dtos.GuideDTO;
import dat.dtos.GuidePriceDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuideDAO implements IDAO<GuideDTO> {

    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory emf_) {
        if (instance == null) {
            emf = emf_;
            instance = new GuideDAO();
        }
        return instance;
    }

    @Override
    public GuideDTO create(GuideDTO dto) {
        return null;
    }

    @Override
    public List<GuideDTO> getAll() {
        return List.of();
    }

    @Override
    public GuideDTO getById(Integer id) {
        try (var em = emf.createEntityManager()) {

            Guide guide = em.find(Guide.class, id);

            if (guide == null) {
                return null;
            }
            return new GuideDTO(guide);
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO dto) {
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    public List<GuidePriceDTO> getGuidePriceOverview() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Trip> allTrips = em.createQuery("SELECT t FROM Trip t", Trip.class).getResultList();

            // Group trips by guide and calculate total price per guide
            Map<Integer, Double> guideTotalPrices = allTrips.stream()
                    .filter(trip -> trip.getGuide() != null) // Ensure trip has an associated guide
                    .collect(Collectors.groupingBy(
                            trip -> trip.getGuide().getId(),
                            Collectors.summingDouble(Trip::getPrice)
                    ));

            // Convert to GuideTripOverviewDTO list
            return guideTotalPrices.entrySet().stream()
                    .map(entry -> new GuidePriceDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }
    }
}
