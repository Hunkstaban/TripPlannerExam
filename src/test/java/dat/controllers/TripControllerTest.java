package dat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.daos.TripDAO;
import dat.dtos.TripDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.entities.Category;
import dat.routes.TripRoutes;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripControllerTest {

    private static EntityManagerFactory emf;
    private static Javalin app;
    private static Trip trip1, trip2;
    private static Guide guide1, guide2;

    @BeforeAll
    static void setUpAll() {
        RestAssured.baseURI = "http://localhost:7070/api/v1";

        // Test database and EntityManagerFactory
        emf = HibernateConfig.getEntityManagerFactory(true);

        // Initializing Javalin server
        app = ApplicationConfig.startServer(7070);
    }

    @BeforeEach
    void setUp() {
        // Create test data: guides and trips
        guide1 = new Guide("John", "Doe", "john.doe@example.com", "123456789", 10);
        guide2 = new Guide("Jane", "Smith", "jane.smith@example.com", "987654321", 8);

        trip1 = new Trip(LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 17, 0), "Mountain Base", "Mountain Adventure", 150.0, Category.FOREST);

        trip2 = new Trip(LocalDateTime.of(2024, 7, 10, 10, 0),
                LocalDateTime.of(2024, 7, 10, 16, 0), "City Center", "City Exploration", 100.0, Category.CITY);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(guide1);
            em.persist(guide2);
            em.persist(trip1);
            em.persist(trip2);
            em.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDownAll() {
        app.stop();
        emf.close();
    }

    @Test
    @DisplayName("Test server is up")
    void testServerIsUp() {
        given().when().get("/trips").then().statusCode(200);
    }

    @Test
    void getAllTrips() {
        List<TripDTO> trips = given()
                .when()
                .get("/trips")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<TripDTO>>() {});

        assertThat(trips.size(), is(2));
        assertThat(trips.get(0).getName(), is("Mountain Adventure"));
    }

    @Test
    void getTripById() {
        TripDTO trip = given()
                .when()
                .get("/trips/" + trip1.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(TripDTO.class);

        assertThat(trip.getId(), is(trip1.getId()));
        assertThat(trip.getName(), is("Mountain Adventure"));
        assertThat(trip.getPackingItems(), is(notNullValue())); // Verifying packing items are returned
    }

    @Test
    void getTripById_NotFound() {
        given()
                .when()
                .get("/trips/999")
                .then()
                .statusCode(404)
                .body("message", containsString("Trip not found"));
    }

    @Test
    void addTrip() {
        TripDTO newTrip = new TripDTO(LocalDateTime.of(2024, 8, 20, 10, 0),
                LocalDateTime.of(2024, 8, 20, 18, 0), "Lake Shore", "Lake Excursion", 120.0, Category.LAKE);

        TripDTO createdTrip = given()
                .body(newTrip)
                .contentType("application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(201)
                .extract()
                .as(TripDTO.class);

        assertThat(createdTrip.getName(), is("Lake Excursion"));
    }

    @Test
    void getTotalPackingWeight() {
        int totalWeight = given()
                .when()
                .get("/trips/" + trip1.getId() + "/packweight")
                .then()
                .statusCode(200)
                .extract()
                .path("totalPackingWeightInGrams");

        assertThat(totalWeight, is(greaterThan(0))); // Assuming weight is greater than 0
    }

    @Test
    void deleteTrip() {
        given()
                .when()
                .delete("/trips/" + trip1.getId())
                .then()
                .statusCode(200)
                .body(containsString("Trip with id " + trip1.getId() + " deleted"));
    }

    @Test
    void deleteTrip_NotFound() {
        given()
                .when()
                .delete("/trips/999")
                .then()
                .statusCode(404)
                .body("message", containsString("Trip not found"));
    }
}