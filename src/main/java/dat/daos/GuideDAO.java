package dat.daos;

import dat.dtos.GuideDTO;

import java.util.List;

public class GuideDAO implements IDAO<GuideDTO> {

    @Override
    public GuideDTO create(GuideDTO dto) {
        return null;
    }

    @Override
    public List<GuideDTO> getAll() {
        return List.of();
    }

    @Override
    public GuideDTO getById(Integer id) {
        return null;
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO dto) {
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }
}
