package epam.webtech.model.user;

import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.CrudRepository;

/**
 * Interface of repository for {@link User}
 * Extends {@link CrudRepository}
 *
 * @author NegaTiV444
 * @version 1.0
 */
public interface UserRepository extends CrudRepository<User> {

    User getByName(String name) throws NotFoundException;
}
