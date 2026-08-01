package infrastructure.file;

import infrastructure.config.AppConfig;

import java.nio.file.Path;

public final class FilePaths {
    private final Path dataDirectory;
    private final Path parkingLotFile;
    private final Path reportFile;

    public FilePaths(AppConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Application config cannot be null");
        }

        this.dataDirectory = config.getDataDirectory();
        this.parkingLotFile = dataDirectory.resolve(config.getParkingLotFileName());
        this.reportFile = dataDirectory.resolve(config.getReportFileName());
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public Path getParkingLotFile() {
        return parkingLotFile;
    }

    public Path getReportFile() {
        return reportFile;
    }
}