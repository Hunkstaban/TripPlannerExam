package dat.daos;

import dat.dtos.GuidePriceDTO;
import dat.dtos.TripDTO;

import dat.entities.Category;
import dat.entities.Guide;
import dat.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TripDAO implements IDAO<TripDTO>, ITripGuideDAO {

    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public TripDTO create(TripDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = new Trip(dto);
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public List<TripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t", Trip.class);
            return query.getResultList().stream().map(TripDTO::new).toList();
        }
    }

    @Override
    public TripDTO getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new EntityNotFoundException("Trip with id " + id + " not found");
            }
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new EntityNotFoundException("Trip with id " + id + " not found");
            }
            trip.setStarttime(dto.getStarttime());
            trip.setEndtime(dto.getEndtime());
            trip.setStartposition(dto.getStartposition());
            trip.setName(dto.getName());
            trip.setPrice(dto.getPrice());
            trip.setCategory(dto.getCategory());

            em.merge(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                return false;
            }
            em.getTransaction().begin();
            em.remove(trip);
            em.getTransaction().commit();
            return true;
        }
    }

    @Override
    public void addGuideToTrip(int tripId, int guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);

            if (trip == null) {
                throw new EntityNotFoundException("Trip with id " + tripId + " not found");
            }
            if (guide == null) {
                throw new EntityNotFoundException("Guide with id " + guideId + " not found");
            }

            guide.getTrips().add(trip);
            trip.setGuide(guide);

            em.getTransaction().commit();
        }
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, guideId);
            if (guide != null) {
                Set<TripDTO> tripDTOs = new HashSet<>();
                for (Trip trip : guide.getTrips()) {
                    tripDTOs.add(new TripDTO(trip)); // Convert each Trip to TripDTO
                }
                return tripDTOs;
            }
            return Set.of();
        }
    }

    public List<TripDTO> getTripsByCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.category = :category", Trip.class);
            query.setParameter("category", category);
            return query.getResultList().stream().map(TripDTO::new).toList();
        }
    }
}



