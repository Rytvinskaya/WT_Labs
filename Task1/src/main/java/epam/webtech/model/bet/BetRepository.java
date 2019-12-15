package epam.webtech.model.bet;

import epam.webtech.model.CrudRepository;
import epam.webtech.model.user.User;

import java.util.List;

public interface BetRepository extends CrudRepository<Bet> {

    List<Bet> findByUser(User user);

}
