package epam.webtech.application;

import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.NotEnoughMoneyException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.exceptions.WrongPasswordException;
import epam.webtech.model.bet.Bet;
import epam.webtech.model.bet.BetRepository;
import epam.webtech.model.enums.Command;
import epam.webtech.model.enums.RaceStatus;
import epam.webtech.model.horse.Horse;
import epam.webtech.model.horse.HorseRepository;
import epam.webtech.model.race.Race;
import epam.webtech.model.race.RaceRepository;
import epam.webtech.model.race.RaceService;
import epam.webtech.model.user.User;
import epam.webtech.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Component
public class CommandHandler {

    private boolean isRunning = true;
    private Scanner scanner = new Scanner(System.in);
    private User currentUser;
    private int authorityLvl = 0;

    @Autowired
    private UserService userService;

    @Autowired
    private RaceRepository raceRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private RaceService raceService;

    public void listenCommands() {
        String input;
        while (isRunning) {
            System.out.println("Enter command:");
            input = scanner.next();
            input = input.trim().toLowerCase();
            switch (input) {
                case "/register":
                    handleRegisterCommand();
                    break;
                case "/login":
                    handleLoginCommand();
                    break;
                case "/exit":
                case "exit":
                    handleExitCommand();
                    break;
                case "/races":
                    if (authorityLvl >= Command.RACES.getAuthorityLvl())
                        handleRacesCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/bets":
                    if (authorityLvl >= Command.BETS.getAuthorityLvl())
                        handleBetsCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/addrace":
                    if (authorityLvl >= Command.ADD_RACE.getAuthorityLvl())
                        handleAddRaceCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/addhorse":
                    if (authorityLvl >= Command.ADD_HORSE.getAuthorityLvl())
                        handleAddHorseCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/finishrace":
                    if (authorityLvl >= Command.FINISH_RACE.getAuthorityLvl())
                        handleFinishRaceCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/makebet":
                    if (authorityLvl >= Command.MAKE_BET.getAuthorityLvl())
                        handleMakeBetCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/me":
                    if (authorityLvl >= Command.ME.getAuthorityLvl())
                        handleMeCommand();
                    else
                        handleWrongCommand();
                    break;
                case "/help":
                    handleHelpCommand();
                    break;
                default:
                    handleWrongCommand();
                    break;
            }
        }
    }

    private void handleRegisterCommand() {
        if (currentUser != null)
            System.out.println("Please log out before register new user");
        else {
            boolean isError = true;
            String name, password;
            while (isError) {
                System.out.println("Enter name (3-16 characters)");
                name = scanner.next();
                name = name.trim();
                if (name.equals("/exit"))
                    return;
                if ((name.length() < 3) || (name.length() > 16))
                    continue;
                System.out.println("Enter your password (6-16 characters)");
                password = scanner.next();
                password = password.trim();
                if (password.equals("/exit"))
                    return;
                if ((password.length() < 6) || (password.length() > 16))
                    continue;
                try {
                    currentUser = userService.registerNewUser(name, password);
                    authorityLvl = currentUser.getAuthorityLvl();
                    System.out.println("Registration successful\nCurrent user: " + currentUser.getName());
                    isError = false;
                } catch (IOException e) {
                    System.out.println("Database error");
                    //TODO log
                } catch (AlreadyExistsException e) {
                    System.out.println("User " + name + " already exists");
                }
            }
        }
    }

    private void handleLoginCommand() {
        if (currentUser != null)
            System.out.println("Please log out before log in");
        else {
            boolean isError = true;
            String name, password;
            while (isError) {
                System.out.println("Enter name (3-16 characters)");
                name = scanner.next();
                name = name.trim();
                if (name.equals("/exit"))
                    return;
                if ((name.length() < 3) || (name.length() > 16))
                    continue;
                System.out.println("Enter your password (6-16 characters)");
                password = scanner.next();
                password = password.trim();
                if (password.equals("/exit"))
                    return;
                if ((password.length() < 6) || (password.length() > 16))
                    continue;
                try {
                    currentUser = userService.logIn(name, password);
                    authorityLvl = currentUser.getAuthorityLvl();
                    System.out.println("LogIn successful\nCurrent user: " + currentUser.getName());
                    isError = false;
                } catch (NotFoundException e) {
                    System.out.println("User " + name + " doesn't exist");
                } catch (WrongPasswordException e) {
                    System.out.println("Wrong password");
                }
            }

        }
    }

    private void handleAddRaceCommand() {
        int horseCount = 0;
        boolean isError = true;
        String input;
        while (isError) {
            System.out.println("Enter number of horses (2-6)");
            input = scanner.next();
            if (input.equals("/exit"))
                return;
            try {
                horseCount = Integer.parseInt(input);
                isError = false;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input");
            }
        }
        isError = true;
        String[] horseNames = new String[horseCount];
        while (isError) {
            for (int i = 0; i < horseCount; i++) {
                System.out.println("Enter the name of horse number " + (i + 1));
                input = scanner.next();
                if (input.equals("/exit"))
                    return;
                try {
                    horseRepository.getByName(input);
                    horseNames[i] = input;
                } catch (NotFoundException e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
            isError = false;
        }
        isError = true;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        while (isError) {
            System.out.println("Enter date of the race (example: 31/08/1999 12:00)");
            scanner.nextLine();
            input = scanner.nextLine();
            if (input.equals("/exit"))
                return;
            try {
                date = format.parse(input);
                if (date.compareTo(new Date()) <= 0) {
                    System.out.println("Wrong date");
                    continue;
                }
                isError = false;
            } catch (ParseException e) {
                System.out.println("Wrong format");
            }
        }
        try {
            raceRepository.add(raceService.createRace(horseNames, date));
        } catch (IOException e) {
            System.out.println("Database error");
            //TODO log
        } catch (AlreadyExistsException e) {
            //TODO log
        }
        System.out.println("Race successfully added");
        System.out.println("---------------------------------------------------------------");
    }

    private void handleAddHorseCommand() {
        System.out.println("---------------------------------------------------------------");
        boolean isError = true;
        String input;
        while (isError) {
            System.out.println("Enter horse's name");
            input = scanner.next();
            if (input.equals("/exit"))
                return;
            Horse horse = new Horse();
            horse.setName(input);
            try {
                horseRepository.add(horse);
                isError = false;
                System.out.println("Horse successfully added");
            } catch (IOException e) {
                System.out.println("Database error");
                //TODO log
            } catch (AlreadyExistsException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("---------------------------------------------------------------");
    }

    private void handleFinishRaceCommand() {
        System.out.println("---------------------------------------------------------------");
        boolean isError = true;
        String input;
        int raceId;
        while (isError) {
            System.out.println("Enter ID of the race");
            input = scanner.next();
            if (input.equals("/exit"))
                return;
            try {
                raceId = Integer.parseInt(input);
                Race race = raceRepository.getByID(raceId);
                System.out.println("Enter the winner horse name");
                input = scanner.next();
                if (input.equals("/exit"))
                    return;
                Horse winner = horseRepository.getByName(input);
                winner.setWinsCounter(winner.getWinsCounter() + 1);
                race.setStatus(RaceStatus.FINISHED);
                race.setWinnerHorseName(input);
                System.out.println("Race " + race.getId() + " finished\n Race bank: " + race.getBank());
                raceService.checkBets(race);
                isError = false;
            } catch (NumberFormatException e) {
                System.out.println("ID must be number");
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("---------------------------------------------------------------");
    }

    private void handleExitCommand() {
        if (currentUser != null) {
            currentUser = null;
            System.out.println("Log out");
        } else {
            isRunning = false;
        }
    }

    private void handleRacesCommand() {
        List<Race> races = raceRepository.findAll();
        System.out.println("\n-----------------######      RACES      ######-----------------");
        for (Race race : races) {
            System.out.println("---------------------------------------------------------------");
            printRace(race);

        }
        System.out.println("---------------------------------------------------------------");
    }

    private void printRace(Race race) {
        System.out.println("Status: " + race.getStatus().toString() + "\nID: " + race.getId() + ". Date" + race.getDate().toString());
        System.out.println("Horses: ");
        String[] horses = race.getHorsesNames();
        float[] odds = race.getOdds();
        StringBuilder sb;
        for (int i = 0; i < horses.length; i++) {
            sb = new StringBuilder("   ");
            sb.append(horses[i]);
            for (int j = 20 - horses[i].length(); j > 0; j--) {
                sb.append(" ");
            }
            sb.append(odds[i]);
            System.out.println(sb.toString());
        }
    }

    private void handleBetsCommand() {
        List<Bet> bets = betRepository.findByUser(currentUser);
        System.out.println("\n-----------------######   YOUR BETS   ######-----------------");
        if (bets.isEmpty())
            System.out.println("You haven't active bets");
        else {
            for (Bet bet : bets) {
                System.out.println("---------------------------------------------------------------");
                System.out.println("RACE: ");
                try {
                    printRace(raceRepository.getByID(bet.getRaceId()));
                } catch (NotFoundException e) {
                    System.out.println("Race not found");
                    //TODO log
                }
                System.out.println("\nYour bet:\n   Horse: " + bet.getHorseName() + "\nAmount: " + bet.getAmount() + "\n");
            }
        }
        System.out.println("---------------------------------------------------------------");
    }

    private void handleMakeBetCommand() {
        System.out.println("\n-----------------######   ADD NEW BET   ######-----------------");

        boolean isError = true;
        int raceId, amount;
        String horseName;
        while (isError)
            try {
                System.out.println("Enter the race ID");
                String input = scanner.next();
                if (input.equals("/exit"))
                    return;
                raceId = Integer.parseInt(input);
                Race race = raceRepository.getByID(raceId);
                System.out.println("Enter the horse name");
                input = scanner.next();
                if (input.equals("/exit"))
                    return;
                horseName = input;
                Horse horse = horseRepository.getByName(horseName);
                System.out.println("Enter the amount of money");
                input = scanner.next();
                if (input.equals("/exit"))
                    return;
                amount = Integer.parseInt(input);
                raceService.addBet(currentUser, race, horse, amount);
                isError = false;
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input. Please enter the number");
            } catch (NotFoundException | AlreadyExistsException | NotEnoughMoneyException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Database error");
                //TODO log
            }
        System.out.println("Bet added successfully");
        System.out.println("---------------------------------------------------------------");
    }

    private void handleMeCommand() {

        System.out.println("\n-----------------######   YOUR PROFILE   ######-----------------");
        System.out.println("Name: " + currentUser.getName() + "\nTotal money: " + currentUser.getBank());
        System.out.println("---------------------------------------------------------------");
    }

    private void handleHelpCommand() {
        int authorityLvl = currentUser == null ? 0 : currentUser.getAuthorityLvl();
        System.out.println("---------------------------------------------------------------");
        for (Command command : Command.values()) {
            if (command.getAuthorityLvl() <= authorityLvl)
                System.out.println(command.getName() + "---->" + command.getDescription());
        }
        System.out.println("---------------------------------------------------------------");
    }

    private void handleWrongCommand() {
        System.out.println("Unknown command\n");
    }

}
