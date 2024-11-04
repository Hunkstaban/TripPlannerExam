package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dat.entities.Guide;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GuideDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private int yearsOfExperience;

    @JsonIgnore
    private List<TripDTO> trips;

    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.firstname = guide.getFirstname();
        this.lastname = guide.getLastname();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
    }
}
