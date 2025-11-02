package RegisResQ.persistence;
import java.util.*;

public interface Dao<T> {

    List<T> getAll();

    Boolean add(T item);

    Boolean update(T item);

    Boolean delete(T item);
}
