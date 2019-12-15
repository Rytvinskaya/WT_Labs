package epam.webtech.services;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import epam.webtech.exceptions.ValidationException;
import epam.webtech.model.HorseRacingData;
import epam.webtech.model.XmlRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HorseRacingMigrationService implements XmlRepository {

    private static final String XSD_FILE_NAME = "src/main/resources/horseRacingData.xsd";

    private final XsdValidationService validationService = XsdValidationService.getInstance();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final Logger logger = LogManager.getLogger(HorseRacingMigrationService.class);

    private HorseRacingMigrationService() {
    }

    private static class SingletonHandler {
        static final HorseRacingMigrationService INSTANCE = new HorseRacingMigrationService();
    }

    public static HorseRacingMigrationService getInstance() {
        return HorseRacingMigrationService.SingletonHandler.INSTANCE;
    }

    public HorseRacingData loadHorseRacingData(File xmlDataFile) throws ValidationException {
        File xsdFile = new File(XSD_FILE_NAME);
        validationService.validate(xmlDataFile, xsdFile);
        logger.info(xmlDataFile.getName() + " validated successful. Used scheme: " + xsdFile.getName());
        HorseRacingData horseRacingData;
        try {
            String xml = inputStreamToString(new FileInputStream(xmlDataFile));
            horseRacingData = xmlMapper.readValue(xml, HorseRacingData.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ValidationException("Error " + e.getMessage());
        }
        return horseRacingData;
    }

}
