package epam.webtech.model.user;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class UserSaxParser extends DefaultHandler {

    enum UserTag {
        USERS, USER, USER_NAME, USER_BANK, USER_PASSWORD_HASH, USER_AUTHORITY_LVL;
    }

    private List<User> users;
    private User user;
    private StringBuilder input;

    public List<User> getUsers() {
        return users;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        input = new StringBuilder();
        if (qName.toUpperCase().equals(UserTag.USERS.toString())) {
            users = new ArrayList<User>();
        }
        if ((users != null) && (qName.toUpperCase().equals(UserTag.USER.toString()))) {
            user = new User();
            user.setId(Integer.parseInt(attributes.getValue("id")));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        input.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            UserTag tagName = UserTag.valueOf(qName.toUpperCase().replace("-", "_"));
            switch (tagName) {
                case USER_BANK:
                    user.setBank(Integer.parseInt(input.toString()));
                    break;
                case USER_PASSWORD_HASH:
                    user.setPasswordHash(input.toString());
                    break;
                case USER_AUTHORITY_LVL:
                    user.setAuthorityLvl(Integer.parseInt(input.toString()));
                    break;
                case USER_NAME:
                    user.setName(input.toString());
                    break;
                case USER:
                    users.add(user);
                    user = null;
                    break;
            }
        } catch (Exception e) {
        }

    }
}
