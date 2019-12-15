package epam.webtech;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.ValidationException;
import epam.webtech.model.HorseRacingData;
import epam.webtech.services.HorseRacingMigrationService;
import epam.webtech.model.bet.BetMigrationService;
import epam.webtech.model.horse.HorseMigrationService;
import epam.webtech.model.race.RaceMigrationService;
import epam.webtech.model.user.UserMigrationService;
import epam.webtech.services.FileService;
import epam.webtech.services.JdbcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/epam?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123321";

    private static final String XML_DATA_FILE = "src/main/resources/HorseRacingData.xml";

    private static final Logger logger = LogManager.getLogger(Application.class);

    private static JdbcService jdbcService = JdbcService.getInstance();
    private static HorseMigrationService horseMigrationService = HorseMigrationService.getInstance();
    private static BetMigrationService betMigrationService = BetMigrationService.getInstance();
    private static RaceMigrationService raceMigrationService = RaceMigrationService.getInstance();
    private static UserMigrationService userMigrationService = UserMigrationService.getInstance();
    private static HorseRacingMigrationService horseRacingMigrationService = HorseRacingMigrationService.getInstance();
    private static FileService fileService = FileService.getInstance();



    public static void main(String[] args) {
        System.out.println("XML to MySQL migration tool (using XSD-validation)");
        logger.debug("Application started");
        try {
            jdbcService.init(DB_URL, DB_USER, DB_PASSWORD);
            logger.debug("JdbcService: initialization successful");
            HorseRacingData data = horseRacingMigrationService.loadHorseRacingData(fileService.checkFile(XML_DATA_FILE));
            userMigrationService.migrate(data.getUsers());
            horseMigrationService.migrate(data.getHorses());
            raceMigrationService.migrate(data.getRaces());
            betMigrationService.migrate(data.getBets());
        } catch (ValidationException e) {
            System.out.println("Migration failed " + e);
            logger.debug(e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database error " + e);
            logger.fatal(e.getMessage());
        }
        logger.debug("Application finished");
    }

}
