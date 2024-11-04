package dat.daos;

import dat.dtos.TripDTO;

import java.util.Set;

public interface ITripGuideDAO {
    void addGuideToTrip(int tripId, int guideId);
    Set<TripDTO> getTripsByGuide(int guideId);
}
