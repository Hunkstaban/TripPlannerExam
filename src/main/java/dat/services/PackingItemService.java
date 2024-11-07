package dat.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.controllers.TripController;
import dat.dtos.PackingItemDTO;
import dat.entities.Category;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PackingItemService {

    public List<PackingItemDTO> fetchPackingItems(Category category) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://packingapi.cphbusinessapps.dk/packinglist/" + category.toString().toLowerCase()))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                PackingItemService.PackingItemResponse packingItemResponse = objectMapper.readValue(response.body(), PackingItemService.PackingItemResponse.class);
                return packingItemResponse.getItems();
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    // Private inner class to handle the external API response
    private static class PackingItemResponse {
        @JsonProperty("items")
        private List<PackingItemDTO> items;

        public List<PackingItemDTO> getItems() {
            return items;
        }
    }
}
