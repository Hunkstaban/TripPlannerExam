package dat.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TripInfoDTO {
    TripDTO trip;
    GuideDTO guide;

    @JsonProperty("packing_items")
    List<PackingItemDTO> packingItems;

    public TripInfoDTO(TripDTO trip, GuideDTO guide, List<PackingItemDTO> packingItems) {
        this.trip = trip;
        this.guide = guide;
        this.packingItems = packingItems;
    }
}
