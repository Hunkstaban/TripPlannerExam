package dat.daos;

import java.util.List;


public interface IDAO<T> {

    T create(T dto);

    List<T> getAll();

    T getById(Integer id);

    T update(Integer id, T dto);

    boolean delete(Integer id);
}
