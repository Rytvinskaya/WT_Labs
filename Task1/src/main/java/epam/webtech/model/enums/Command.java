package epam.webtech.model.enums;

public enum Command {

    REGISTER("/register", "Register new User", 0),
    LOGIN("/login", "Log in to the system", 0),
    EXIT("/exit", "Log out of the system / stop application", 0),
    RACES("/races", "Show all races", 0),
    ADD_RACE("/addrace", "Add new race (only for administrators)", 2),
    ADD_HORSE("/addhorse", "Add new horse (only for administrators)", 2),
    FINISH_RACE("/finishrace", "Finish active race (only for administrators)", 2),
    BETS("/bets", "Show your bets", 1),
    MAKE_BET("/makebet", "Make a new bet", 1),
    ME("/me", "Show your account's information", 1),
    HELP("/help", "Show available command", 0);

    Command(String name, String description, int authorityLvl) {
        this.name = name;
        this.description = description;
        this.authorityLvl = authorityLvl;
    }

    private String name;
    private String description;
    private int authorityLvl;

    public int getAuthorityLvl() {
        return authorityLvl;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

}
