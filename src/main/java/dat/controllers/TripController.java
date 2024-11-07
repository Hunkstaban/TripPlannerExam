package dat.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.config.HibernateConfig;
import dat.config.Populator;
import dat.daos.TripDAO;
import dat.dtos.*;
import dat.entities.Category;
import dat.entities.Trip;
import dat.exceptions.ErrorResponse;
import dat.services.PackingItemService;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.EntityManagerFactory;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class TripController {

    private final TripDAO tripDAO;
    private final Populator populator;
    private static PackingItemService packingItemService = new PackingItemService();

    public TripController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
        this.tripDAO = TripDAO.getInstance(emf);
        this.populator = Populator.getInstance(emf);
    }

    public void createTrip(Context ctx) {
        try {
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);
            TripDTO createdTrip = tripDAO.create(tripDTO);
            ctx.status(201).json(createdTrip);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to create trip"));
        }
    }

    public void getAllTrips(Context ctx) {
        try {
            List<TripDTO> trips = tripDAO.getAll();
            ctx.json(trips);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to retrieve trips"));
        }
    }

    public void getTripById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = tripDAO.getById(id);

            if (trip != null) {
                GuideDTO guide = trip.getGuide();
                List<PackingItemDTO> packingItems = packingItemService.fetchPackingItems(trip.getCategory());
                TripInfoDTO tripInfo = new TripInfoDTO(trip, guide, packingItems);
                ctx.json(tripInfo);
            } else {
                ctx.status(404).json(new ErrorResponse(404, "Trip not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to retrieve trip"));
        }
    }

    public void updateTrip(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO updatedTrip = ctx.bodyAsClass(TripDTO.class);
            TripDTO tripDTO = tripDAO.update(id, updatedTrip);
            ctx.status(200).json(tripDTO);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to update trip"));
        }
    }

    public void deleteTrip(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean isDeleted = tripDAO.delete(id);
            if (isDeleted) {
                ctx.status(200).result("Trip with id " + id + " deleted");
            } else {
                ctx.status(404).json(new ErrorResponse(404, "Trip not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to delete trip"));
        }
    }

    public void addGuideToTrip(Context ctx) {
        try {
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            tripDAO.addGuideToTrip(tripId, guideId);
            ctx.status(200).result("Guide with ID " + guideId + " has been added to Trip with ID " + tripId);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to add guide to trip"));
        }
    }

    public void populateDatabase(Context ctx) {
        try {
            populator.populate();
            ctx.status(201).result("Database populated with sample data.");
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to populate database"));
        }
    }

    public void filterTripsByCategory(Context ctx) {
        try {
            String categoryParam = ctx.pathParam("category").toUpperCase();
            Category category = Category.valueOf(categoryParam.toUpperCase());

            List<TripDTO> trips = tripDAO.getTripsByCategory(category);
            ctx.json(trips);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponse(400, "Invalid category specified"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to filter trips by category"));
        }
    }

    public void getTotalPackingWeight(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = tripDAO.getById(id);

            if (trip != null) {
                List<PackingItemDTO> packingItems = packingItemService.fetchPackingItems(trip.getCategory());
                int totalWeight = packingItems.stream().mapToInt(PackingItemDTO::getWeightInGrams).sum();
                ctx.json(Map.of("tripId", id, "totalPackingWeightInGrams", totalWeight));
            } else {
                ctx.status(404).json(new ErrorResponse(404, "Trip not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to retrieve total packing weight"));
        }
    }
}