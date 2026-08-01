package infrastructure.config;

import infrastructure.logging.AppLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigLoader {
    private static final String DEFAULT_CONFIG_FILE = "config.properties";

    private final AppLogger logger;

    public ConfigLoader(AppLogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.logger = logger;
    }

    public AppConfig load() {
        return load(Paths.get(DEFAULT_CONFIG_FILE));
    }

    public AppConfig load(Path configFile) {
        if (configFile == null) {
            logger.warn("Config file path was null. Using default configuration.");
            return AppConfig.defaults();
        }

        if (!Files.exists(configFile)) {
            logger.info("Config file not found at " + configFile + ". Using default configuration.");
            return AppConfig.defaults();
        }

        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(configFile)) {
            properties.load(inputStream);

            return new AppConfig(
                    parsePositiveInteger(properties.getProperty("parking.lot.size"), AppConfig.defaults().getParkingLotSize()),
                    Paths.get(normalize(properties.getProperty("data.directory"), AppConfig.defaults().getDataDirectory().toString())),
                    normalize(properties.getProperty("parking.lot.file"), AppConfig.defaults().getParkingLotFileName()),
                    normalize(properties.getProperty("report.file"), AppConfig.defaults().getReportFileName()),
                    normalize(properties.getProperty("app.title"), AppConfig.defaults().getAppTitle())
            );
        } catch (IOException exception) {
            logger.error("Failed to read config file. Using default configuration.", exception);
            return AppConfig.defaults();
        } catch (Exception exception) {
            logger.error("Unexpected config loading error. Using default configuration.", exception);
            return AppConfig.defaults();
        }
    }

    private int parsePositiveInteger(String value, int fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        try {
            int parsedValue = Integer.parseInt(value.trim());

            if (parsedValue <= 0) {
                logger.warn("Config value must be greater than zero: " + value + ". Using fallback.");
                return fallback;
            }

            return parsedValue;
        } catch (NumberFormatException exception) {
            logger.warn("Invalid numeric config value: " + value + ". Using fallback.");
            return fallback;
        }
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }
}