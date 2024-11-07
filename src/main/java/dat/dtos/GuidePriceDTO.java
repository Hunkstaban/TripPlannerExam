package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuidePriceDTO {

    @JsonProperty("guide_id")
    private Integer guideId;

    @JsonProperty("total_price")
    private Double totalPrice;

    public GuidePriceDTO(Integer guideId, Double totalPrice) {
        this.guideId = guideId;
        this.totalPrice = totalPrice;
    }
}
