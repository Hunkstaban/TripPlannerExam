package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Category;
import dat.entities.Trip;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripDTO {
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime starttime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endtime;
    private String startposition;
    private String name;
    private Double price;
    private Category category;

    @JsonIgnore
    private GuideDTO guide;

    public TripDTO (Trip trip) {
        this.id = trip.getId();
        this.starttime = trip.getStarttime();
        this.endtime = trip.getEndtime();
        this.startposition = trip.getStartposition();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();

        // Map Guide entity to GuideDTO
        if (trip.getGuide() != null) {
            this.guide = new GuideDTO(trip.getGuide());
        }
    }

    public TripDTO(LocalDateTime starttime, LocalDateTime endtime, String startposition, String name, Double price, Category category) {
        this.starttime = starttime;
        this.endtime = endtime;
        this.startposition = startposition;
        this.name = name;
        this.price = price;
        this.category = category;
    }
}
