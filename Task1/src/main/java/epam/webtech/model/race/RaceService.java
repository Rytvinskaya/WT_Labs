package epam.webtech.model.race;

import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.NotEnoughMoneyException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.bet.Bet;
import epam.webtech.model.bet.BetRepository;
import epam.webtech.model.enums.RaceStatus;
import epam.webtech.model.horse.Horse;
import epam.webtech.model.user.User;
import epam.webtech.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RaceService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RaceRepository raceRepository;

    public Race createRace(String[] horsesNames, Date date) {
        Race newRace = new Race();
        newRace.setHorsesNames(horsesNames);
        newRace.setDate(date);
        newRace.setStatus(RaceStatus.WAITING);
        float[] odds = new float[horsesNames.length];
        Arrays.fill(odds, 1 + 1 / odds.length);
        newRace.setOdds(odds);
        newRace.setBetsId(new ArrayList<>());
        return newRace;
    }

    //TODO fix ID generation
    public void addBet(User user, Race race, Horse horse, int amount) throws NotEnoughMoneyException, AlreadyExistsException, IOException {
        if (race.getStatus().equals(RaceStatus.WAITING)) {
            if (user.getBank() >= amount) {
                List<Bet> bets = betRepository.findAll();
                boolean isError = bets.stream()
                        .anyMatch(bet -> (bet.getUserName().equals(user.getName()) && (bet.getRaceId() == race.getId())));
                if (isError)
                    throw new AlreadyExistsException("Your already have bet on this race");
                user.setBank(user.getBank() - amount);
                Bet newBet = new Bet();
                newBet.setUserName(user.getName());
                newBet.setAmount(amount);
                newBet.setRaceId(race.getId());
                newBet.setHorseName(horse.getName());
                updateOdds(race, newBet);
                race.setBank(race.getBank() + amount);
                betRepository.add(newBet);
            } else
                throw new NotEnoughMoneyException("Sorry, your don't have enough money");
        }
    }

    private void updateOdds(Race race, Bet bet) throws AlreadyExistsException, IOException {
        if (race.getBetsId() == null)
            race.setBetsId(new ArrayList<>());
        race.getBetsId().add(bet.getId());
        String[] horsesNames = race.getHorsesNames();
        float[] odds = race.getOdds();
        int totalBets = race.getBetsId().size();
        for (int i = 0; i < horsesNames.length; i++) {
            long counter;
            int finalI = i;
            counter = race.getBetsId().stream()
                    .filter(x -> {
                        try {
                            return betRepository.getByID(x).getHorseName().equals(horsesNames[finalI]);
                        } catch (NotFoundException e) {
                            return false;
                        }
                    })
                    .count();
            odds[i] = totalBets / (counter + 1);
        }
    }

    public void checkBets(Race race) {
        if (race.getStatus().equals(RaceStatus.FINISHED)) {
            String winnerHorseName = race.getWinnerHorseName();
            int winnerIndex = 0;
            for (int i = 0; i < race.getOdds().length; i++) {
                if (race.getHorsesNames()[i].equals(winnerHorseName)) {
                    winnerIndex = i;
                    break;
                }
            }
            for (int id : race.getBetsId()) {
                Bet bet = null;
                try {
                    bet = betRepository.getByID(id);
                    if (bet.getHorseName().equals(winnerHorseName)) {
                        int prize = (int) Math.floor(race.getOdds()[winnerIndex] * bet.getAmount());
                        try {
                            User user = userRepository.getByName(bet.getUserName());
                            user.setBank(user.getBank() + prize);
                            race.setBank(race.getBank() - prize);
                        } catch (NotFoundException e) {
                            //TODO log
                        }
                    }
                    betRepository.delete(bet);
                } catch (NotFoundException | IOException e) {
                    //TODO log
                }
            }
            try {
                raceRepository.delete(race);
            } catch (NotFoundException | IOException e) {
                //TODO log
            }
        }
    }

}
