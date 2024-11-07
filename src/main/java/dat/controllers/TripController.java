package dat.controllers;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import dat.config.HibernateConfig;
import dat.config.Populator;
import dat.daos.TripDAO;
import dat.dtos.*;
import dat.entities.Category;
import dat.exceptions.ErrorResponse;
import dat.services.PackingItemService;
import io.javalin.http.Context;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

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

    private void respondWithError(Context ctx, int status, String message) {
        ctx.status(status).json(new ErrorResponse(status, message));
    }

    public void createTrip(Context ctx) {
        try {
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);
            TripDTO createdTrip = tripDAO.create(tripDTO);
            ctx.status(201).json(createdTrip);
        }  catch (Exception e) {
            respondWithError(ctx, 500, "Failed to create trip: " + e.getMessage());
        }
    }

    public void getAllTrips(Context ctx) {
        try {
            List<TripDTO> trips = tripDAO.getAll();
            ctx.json(trips);
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to retrieve trips: " + e.getMessage());
        }
    }

    public void getTripById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = tripDAO.getById(id);

            GuideDTO guide = trip.getGuide();
            List<PackingItemDTO> packingItems = packingItemService.fetchPackingItems(trip.getCategory());
            TripInfoDTO tripInfo = new TripInfoDTO(trip, guide, packingItems);
            ctx.json(tripInfo);

        } catch (NumberFormatException e) {
            respondWithError(ctx, 400, "Invalid trip ID format");
        } catch (EntityNotFoundException e) {
            respondWithError(ctx, 404, e.getMessage());
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to retrieve trip: " + e.getMessage());
        }
    }

    public void updateTrip(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO updatedTrip = ctx.bodyAsClass(TripDTO.class);
            TripDTO tripDTO = tripDAO.update(id, updatedTrip);
            ctx.status(200).json(tripDTO);
        } catch (NumberFormatException e) {
            respondWithError(ctx, 400, "Invalid trip ID");
        } catch (EntityNotFoundException e) {
            respondWithError(ctx, 404, e.getMessage());
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to update trip: " + e.getMessage());
        }
    }

    public void deleteTrip(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean isDeleted = tripDAO.delete(id);
            if (isDeleted) {
                ctx.status(200).result("Trip with id " + id + " deleted");
            } else {
                respondWithError(ctx, 404, "Trip not found");
            }
        } catch (NumberFormatException e) {
            respondWithError(ctx, 400, "Invalid trip ID");
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to delete trip: " + e.getMessage());
        }
    }

    public void addGuideToTrip(Context ctx) {
        try {
            int tripId = Integer.parseInt(ctx.pathParam("tripId"));
            int guideId = Integer.parseInt(ctx.pathParam("guideId"));
            tripDAO.addGuideToTrip(tripId, guideId);
            ctx.status(200).result("Guide with ID " + guideId + " has been added to Trip with ID " + tripId);
        } catch (NumberFormatException e) {
            respondWithError(ctx, 400, "Invalid ID format for trip or guide");
        } catch (EntityNotFoundException e) {
            respondWithError(ctx, 404, e.getMessage());
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to add guide to trip: " + e.getMessage());
        }
    }

    public void populateDatabase(Context ctx) {
        try {
            populator.populate();
            ctx.status(201).result("Database populated with sample data.");
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to populate database: " + e.getMessage());
        }
    }

    public void filterTripsByCategory(Context ctx) {
        try {
            String categoryParam = ctx.pathParam("category").toUpperCase();
            Category category = Category.valueOf(categoryParam);

            List<TripDTO> trips = tripDAO.getTripsByCategory(category);
            ctx.json(trips);
        } catch (IllegalArgumentException e) {
            respondWithError(ctx, 400, "Invalid category specified");
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to filter trips by category: " + e.getMessage());
        }
    }

    public void getTotalPackingWeight(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TripDTO trip = tripDAO.getById(id); // Will throw EntityNotFoundException if not found

            List<PackingItemDTO> packingItems = packingItemService.fetchPackingItems(trip.getCategory());
            int totalWeight = packingItems.stream().mapToInt(PackingItemDTO::getWeightInGrams).sum();
            ctx.json(Map.of("tripId", id, "totalPackingWeightInGrams", totalWeight));

        } catch (NumberFormatException e) {
            respondWithError(ctx, 400, "Invalid trip ID");
        } catch (EntityNotFoundException e) {
            respondWithError(ctx, 404, e.getMessage()); // Handles case where trip is not found
        } catch (Exception e) {
            respondWithError(ctx, 500, "Failed to retrieve total packing weight: " + e.getMessage());
        }
    }
}