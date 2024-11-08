package dat.controllers;

import dat.Utils.Populator;
import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.dtos.TripDTO;
import dat.dtos.TripInfoDTO;
import dat.entities.Guide;
import dat.entities.Trip;
import dat.entities.Category;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;
import dat.security.dtos.UserDTO;
import dat.security.exceptions.ValidationException;
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
    private static Trip[] trips;
    private static Guide[] guides;
    private static String userToken, adminToken;
    private static final String BASE_URL = "http://localhost:7070/api/v1";
    private static SecurityController securityController;
    private static SecurityDAO securityDAO;

    @BeforeAll
    static void setUpAll() {
        RestAssured.baseURI = BASE_URL;
        emf = HibernateConfig.getEntityManagerFactory(true);
        app = ApplicationConfig.startServer(7070);

        securityController = SecurityController.getInstance();
        securityDAO = new SecurityDAO(emf);

        // Populate users for authorization and get tokens
        UserDTO[] users = Populator.populateUsers(emf);
        UserDTO userDTO = users[0];
        UserDTO adminDTO = users[1];

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminDTO.getPassword());
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        // Populate the database with guides and trips
        guides = Populator.populateGuides(emf);
        trips = Populator.populateTrips(emf, guides);
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
    void testServerIsUp() {
        given()
                .header("Authorization", userToken)
                .when()
                .get("/trips")
                .then()
                .statusCode(200);
    }

    @Test
    void getAllTrips() {
        List<TripDTO> trips = given()
                .header("Authorization", userToken)
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
        TripInfoDTO tripInfo = given()
                .header("Authorization", userToken)
                .when()
                .get("/trips/" + trips[0].getId())
                .then()
                .statusCode(200)
                .extract()
                .as(TripInfoDTO.class);

        assertThat(tripInfo.getTrip().getId(), is(trips[0].getId()));
        assertThat(tripInfo.getTrip().getName(), is("Mountain Adventure"));
        assertThat(tripInfo.getPackingItems(), is(notNullValue()));
    }

    @Test
    void getTripById_NotFound() {
        given()
                .header("Authorization", userToken)
                .when()
                .get("/trips/999")
                .then()
                .statusCode(404)
                .body("message", containsString("Trip with id 999 not found"));
    }

    @Test
    void addTrip() {
        TripDTO newTrip = new TripDTO(LocalDateTime.of(2024, 8, 20, 10, 0),
                LocalDateTime.of(2024, 8, 20, 18, 0), "Lake Shore", "Lake Excursion", 120.0, Category.LAKE);

        TripDTO createdTrip = given()
                .header("Authorization", userToken)
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
    void addTrip_Unauthorized() {
        TripDTO newTrip = new TripDTO(LocalDateTime.of(2024, 8, 20, 10, 0),
                LocalDateTime.of(2024, 8, 20, 18, 0), "Lake Shore", "Lake Excursion", 120.0, Category.LAKE);

        given()
                .body(newTrip)
                .contentType("application/json")
                .when()
                .post("/trips")
                .then()
                .statusCode(401);
    }

    @Test
    void updateTrip() {
        TripDTO updatedTrip = new TripDTO(LocalDateTime.of(2024, 9, 15, 8, 0),
                LocalDateTime.of(2024, 9, 15, 18, 0), "Beach Side", "Beach Adventure", 200.0, Category.BEACH);

        TripDTO responseTrip = given()
                .header("Authorization", adminToken)
                .body(updatedTrip)
                .contentType("application/json")
                .when()
                .put("/trips/" + trips[0].getId())
                .then()
                .statusCode(200)
                .extract()
                .as(TripDTO.class);

        assertThat(responseTrip.getName(), is("Beach Adventure"));
        assertThat(responseTrip.getCategory(), is(Category.BEACH));
    }

    @Test
    void addGuideToTrip() {
        given()
                .header("Authorization", adminToken)
                .when()
                .put("/trips/" + trips[0].getId() + "/guides/" + guides[1].getId())
                .then()
                .statusCode(200)
                .body(containsString("Guide with ID " + guides[1].getId() + " has been added to Trip with ID " + trips[0].getId()));
    }

    @Test
    void filterTripsByCategory() {
        List<TripDTO> filteredTrips = given()
                .header("Authorization", userToken)
                .when()
                .get("/trips/category/" + Category.FOREST.name())
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<TripDTO>>() {});

        assertThat(filteredTrips, is(not(empty())));
        assertThat(filteredTrips.get(0).getCategory(), is(Category.FOREST));
    }

    @Test
    void getTotalPackingWeight() {
        int totalWeight = given()
                .header("Authorization", userToken)
                .when()
                .get("/trips/" + trips[0].getId() + "/packweight")
                .then()
                .statusCode(200)
                .extract()
                .path("totalPackingWeightInGrams");

        assertThat(totalWeight, is(greaterThan(0))); // Assuming weight is greater than 0
    }

    @Test
    void deleteTrip() {
        given()
                .header("Authorization", adminToken)
                .when()
                .delete("/trips/" + trips[0].getId())
                .then()
                .statusCode(200)
                .body(containsString("Trip with id " + trips[0].getId() + " deleted"));
    }

    @Test
    void deleteTrip_NotFound() {
        given()
                .header("Authorization", adminToken)
                .when()
                .delete("/trips/999")
                .then()
                .statusCode(404)
                .body("message", containsString("Trip not found"));
    }
}