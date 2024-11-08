package dat.Utils;

import dat.entities.Category;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.security.dtos.UserDTO;
import dat.security.entities.Role;
import dat.security.entities.User;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;

public class Populator {

    public static UserDTO[] populateUsers(EntityManagerFactory emf) {
        User user, admin;
        Role userRole, adminRole;

        user = new User("usertest", "user123");
        admin = new User("admintest", "admin123");
        userRole = new Role("USER");
        adminRole = new Role("ADMIN");
        user.addRole(userRole);
        admin.addRole(adminRole);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }
        UserDTO userDTO = new UserDTO(user.getUsername(), "user123");
        UserDTO adminDTO = new UserDTO(admin.getUsername(), "admin123");
        return new UserDTO[]{userDTO, adminDTO};
    }

    public static Guide[] populateGuides(EntityManagerFactory emf) {
        Guide guide1 = new Guide("John", "Doe", "john.doe@example.com", "123456789", 10);
        Guide guide2 = new Guide("Jane", "Smith", "jane.smith@example.com", "987654321", 8);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(guide1);
            em.persist(guide2);
            em.getTransaction().commit();
        }
        return new Guide[]{guide1, guide2};
    }

    public static Trip[] populateTrips(EntityManagerFactory emf, Guide[] guides) {
        Trip trip1 = new Trip(LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 17, 0), "Mountain Base", "Mountain Adventure", 150.0, Category.FOREST);
        trip1.setGuide(guides[0]);

        Trip trip2 = new Trip(LocalDateTime.of(2024, 7, 10, 10, 0),
                LocalDateTime.of(2024, 7, 10, 16, 0), "City Center", "City Exploration", 100.0, Category.CITY);
        trip2.setGuide(guides[1]);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(trip1);
            em.persist(trip2);
            em.getTransaction().commit();
        }
        return new Trip[]{trip1, trip2};
    }
}
