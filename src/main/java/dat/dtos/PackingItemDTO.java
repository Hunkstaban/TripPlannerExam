package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class PackingItemDTO {
    private String name;
    private int weightInGrams;
    private int quantity;
    private String description;
    private String category;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @JsonProperty("buyingOptions")
    private List<BuyingOptionDTO> buyingOptions;

    @Data
    public static class BuyingOptionDTO {
        private String shopName;
        private String shopUrl;
        private double price;
    }
}