package dat.routes;

import dat.controllers.GuideController;
import dat.controllers.TripController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController = new TripController();
    private final GuideController guideController = new GuideController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", tripController::getAllTrips, Role.ANYONE); // Get all trips
            get("/{id}", tripController::getTripById, Role.USER, Role.ADMIN); // Get a trip by ID
            post("/", tripController::createTrip, Role.USER, Role.ADMIN); // Create a new trip
            put("/{id}", tripController::updateTrip, Role.ADMIN); // Update a trip by ID
            delete("/{id}", tripController::deleteTrip, Role.ADMIN); // Delete a trip by ID
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip, Role.ADMIN); // Add an existing guide to an existing trip
            post("/populate", tripController::populateDatabase, Role.ANYONE); // Populate the database with trips and guides
            get("/category/{category}", tripController::filterTripsByCategory, Role.USER, Role.ADMIN); // Filter trips by category
            get("/guides/overview", guideController::getGuidePriceOverview, Role.USER, Role.ADMIN); // Guide trip overview
            get("/{id}/packweight", tripController::getTotalPackingWeight, Role.USER, Role.ADMIN); // Total packing weight endpoint
        };
    }

    /*// To test without roles:
    public EndpointGroup getRoutes() {
        return () -> {
            get("/", tripController::getAllTrips); // Get all trips
            get("/{id}", tripController::getTripById); // Get a trip by ID
            post("/", tripController::createTrip); // Create a new trip
            put("/{id}", tripController::updateTrip); // Update a trip by ID
            delete("/{id}", tripController::deleteTrip); // Delete a trip by ID
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip); // Add an existing guide to an existing trip
            post("/populate", tripController::populateDatabase); // Populate the database with trips and guides
            get("/category/{category}", tripController::filterTripsByCategory); // Filter trips by category
            get("/guides/overview", guideController::getGuidePriceOverview); // Guide trip overview
            get("/{id}/packweight", tripController::getTotalPackingWeight); // Total packing weight endpoint
        };
    }*/
}