package dat.config;

import dat.entities.Category;
import dat.entities.Guide;
import dat.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;

public class Populator {
    private static EntityManagerFactory emf;
    private static Populator instance;

    public static Populator getInstance(EntityManagerFactory emf_) {
        if (instance == null) {
            emf = emf_;
            instance = new Populator();
        }
        return instance;
    }

    public static void populate() {

        // Create guides
        Guide guide1 = new Guide("John", "Doe", "john.doe@example.com", "123456789", 10);
        Guide guide2 = new Guide("Jane", "Smith", "jane.smith@example.com", "987654321", 8);

        // Create trips
        Trip trip1 = new Trip(LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 17, 0), "Mountain Base", "Mountain Adventure", 150.0, Category.FOREST);

        Trip trip2 = new Trip(LocalDateTime.of(2024, 7, 10, 10, 0),
                LocalDateTime.of(2024, 7, 10, 16, 0), "City Center", "City Exploration", 100.0, Category.CITY);

        Trip trip3 = new Trip(LocalDateTime.of(2024, 8, 15, 8, 0),
                LocalDateTime.of(2024, 8, 15, 14, 0), "Forest Entrance", "Lake Excursion", 120.0, Category.LAKE);

        Trip trip4 = new Trip(LocalDateTime.of(2024, 9, 5, 11, 0),
                LocalDateTime.of(2024, 9, 5, 18, 0), "Sunny Beach", "Beach Relaxation", 80.0, Category.BEACH);

        // Associate trips with guides
        trip1.setGuide(guide1);
        trip3.setGuide(guide1);
        trip2.setGuide(guide2);
        trip4.setGuide(guide2);

        guide1.getTrips().add(trip1);
        guide1.getTrips().add(trip3);
        guide2.getTrips().add(trip2);
        guide2.getTrips().add(trip4);

        // Persist guides and associated trips in the database
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(guide1);
            em.persist(guide2);
            em.getTransaction().commit();
        }
    }
}

