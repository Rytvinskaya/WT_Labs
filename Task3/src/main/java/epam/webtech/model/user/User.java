package epam.webtech.model.user;

import epam.webtech.model.Entity;
import lombok.Data;

@Data
public class User extends Entity implements Comparable<User> {

    private int bank;

    private String name;

    private String passwordHash;

    private int authorityLvl;

    @Override
    public int compareTo(User o) {
        return name.compareTo(o.name);
    }
}
