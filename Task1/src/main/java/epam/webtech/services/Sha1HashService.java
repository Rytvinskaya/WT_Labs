package epam.webtech.services;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class Sha1HashService implements HashService {

    private static final String HASH_ALGORITHM = "SHA-1";

    private MessageDigest messageDigest;

    {
        try {
            messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            //TODO log
        }
    }

    @Override
    public String getHash(String data) {
        byte[] hash = messageDigest.digest(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < hash.length; j++) {
            String s = Integer.toHexString(0xff & hash[j]);
            s = (s.length() == 1) ? "0" + s : s;
            sb.append(s);
        }
        return sb.toString();
    }
}
