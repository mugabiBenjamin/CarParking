package infrastructure.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {
    private static final int DEFAULT_PARKING_LOT_SIZE = 10;
    private static final String DEFAULT_DATA_DIRECTORY = "data";
    private static final String DEFAULT_PARKING_LOT_FILE_NAME = "parking_lot.txt";
    private static final String DEFAULT_REPORT_FILE_NAME = "parking_lot_report.csv";
    private static final String DEFAULT_APP_TITLE = "Car Parking System";

    private final int parkingLotSize;
    private final Path dataDirectory;
    private final String parkingLotFileName;
    private final String reportFileName;
    private final String appTitle;

    public AppConfig(
            int parkingLotSize,
            Path dataDirectory,
            String parkingLotFileName,
            String reportFileName,
            String appTitle
    ) {
        this.parkingLotSize = parkingLotSize <= 0 ? DEFAULT_PARKING_LOT_SIZE : parkingLotSize;
        this.dataDirectory = dataDirectory == null ? Paths.get(DEFAULT_DATA_DIRECTORY) : dataDirectory;
        this.parkingLotFileName = normalize(parkingLotFileName, DEFAULT_PARKING_LOT_FILE_NAME);
        this.reportFileName = normalize(reportFileName, DEFAULT_REPORT_FILE_NAME);
        this.appTitle = normalize(appTitle, DEFAULT_APP_TITLE);
    }

    public static AppConfig defaults() {
        return new AppConfig(
                DEFAULT_PARKING_LOT_SIZE,
                Paths.get(DEFAULT_DATA_DIRECTORY),
                DEFAULT_PARKING_LOT_FILE_NAME,
                DEFAULT_REPORT_FILE_NAME,
                DEFAULT_APP_TITLE
        );
    }

    public int getParkingLotSize() {
        return parkingLotSize;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public String getParkingLotFileName() {
        return parkingLotFileName;
    }

    public String getReportFileName() {
        return reportFileName;
    }

    public String getAppTitle() {
        return appTitle;
    }

    private static String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }
}