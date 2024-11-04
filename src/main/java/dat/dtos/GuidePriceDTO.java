package dat.dtos;

import lombok.Data;

@Data
public class GuidePriceDTO {
    private Integer guideId;
    private Double totalPrice;

    public GuidePriceDTO(Integer guideId, Double totalPrice) {
        this.guideId = guideId;
        this.totalPrice = totalPrice;
    }
}
