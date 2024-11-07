package dat.entities;

import dat.dtos.TripDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime starttime;
    private LocalDateTime endtime;
    private String startposition;
    private String name;
    private Double price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;

    public Trip(LocalDateTime starttime, LocalDateTime endtime, String startposition, String name, Double price, Category category) {
        this.starttime = starttime;
        this.endtime = endtime;
        this.startposition = startposition;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Trip(TripDTO tripDTO) {
        this.starttime = tripDTO.getStarttime();
        this.endtime = tripDTO.getEndtime();
        this.startposition = tripDTO.getStartposition();
        this.name = tripDTO.getName();
        this.price = tripDTO.getPrice();
        this.category = tripDTO.getCategory();
    }
}