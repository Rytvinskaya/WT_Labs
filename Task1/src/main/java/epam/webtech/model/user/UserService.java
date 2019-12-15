package epam.webtech.model.user;

import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.exceptions.WrongPasswordException;
import epam.webtech.services.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for working with the {@link User}
 *
 * @author NegaTiV444
 * @version 1.0
 */

@Service
public class UserService {

    @Autowired
    private XmlUserRepository userRepository;

    @Autowired
    private HashService hashService;

    /**
     * Verifies the received password and the password stored in the database for a user with the received name
     *
     * @param name     - Username
     * @param password - password
     * @return User from database if the password is correct
     * @throws NotFoundException      if there is no User with such name in database
     * @throws WrongPasswordException if password-hash aren't equal
     * @see User
     */
    public User logIn(String name, String password) throws NotFoundException, WrongPasswordException {
        User user = userRepository.getByName(name);
        if (user.getPasswordHash().equals(hashService.getHash(password)))
            return user;
        else
            throw new WrongPasswordException("Wrong password");
    }

    /**
     * Create new User and save to database
     *
     * @param name     - Username
     * @param password - password
     * @return User if there is no User with such name in database
     * @throws AlreadyExistsException if there is User with such name in database
     * @throws IOException            if there are problems with database-connection
     * @see User
     */

    public User registerNewUser(String name, String password) throws AlreadyExistsException, IOException {
        User newUser = new User();
        newUser.setName(name);
        newUser.setAuthorityLvl(1);
        newUser.setPasswordHash(hashService.getHash(password));
        newUser.setBank(10000);
        userRepository.add(newUser);
        return newUser;
    }
}
