package epam.webtech.utils;

import epam.webtech.exceptions.HashServiceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class HashService {

    private static boolean isInitialized = false;
    private MessageDigest messageDigest = null;

    private void init() throws HashServiceException {
        try (InputStream input = new FileInputStream("conf/conf.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            messageDigest = MessageDigest.getInstance(prop.getProperty("hash.algorithm"));
            isInitialized = true;
        } catch (NoSuchAlgorithmException e) {
            throw new HashServiceException("Failed to initialize HashService. Algorithm not recognized.");
        } catch (IOException e) {
            throw new HashServiceException("Failed to initialize HashService. Configuration file is unavailable.");
        }
    }

    private HashService() {
    }

    public static HashService getInstance() {
        return SingletonHandler.instance;
    }

    public String getHashAsString(String text) throws HashServiceException {
        if (!isInitialized) {
            init();
        }
        byte[] mdbytes = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < mdbytes.length; j++) {
            String s = Integer.toHexString(0xff & mdbytes[j]);
            s = (s.length() == 1) ? "0" + s : s;
            sb.append(s);
        }
        return sb.toString();
    }

    private static class SingletonHandler {
        static final HashService instance = new HashService();
    }
}
