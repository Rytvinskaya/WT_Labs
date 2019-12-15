package epam.webtech.services;

import epam.webtech.exceptions.ValidationException;

import java.io.File;

public class FileService {

    private FileService() {
    }

    private static class SingletonHandler {
        static final FileService INSTANCE = new FileService();
    }

    public static FileService getInstance() {
        return SingletonHandler.INSTANCE;
    }

    public File checkFile(String path) throws ValidationException {
        File file = new File(path);
        if ((file.exists()) && (file.isFile()) && (file.canRead()) && (file.length() > 0))
            return file;
        else
            throw new ValidationException("File " + path + " cannot be read");
    }
}
