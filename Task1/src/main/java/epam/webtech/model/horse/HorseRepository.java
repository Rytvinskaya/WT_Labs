package epam.webtech.model.horse;

import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.CrudRepository;

public interface HorseRepository extends CrudRepository<Horse> {

    Horse getByName(String name) throws NotFoundException;

}
