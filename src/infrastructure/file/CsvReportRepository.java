package infrastructure.file;

import application.repositories.ReportRepository;
import domain.entities.ParkingLot;
import infrastructure.logging.AppLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvReportRepository implements ReportRepository {
    private final FilePaths filePaths;
    private final ReportFileMapper mapper;
    private final AppLogger logger;

    public CsvReportRepository(
            FilePaths filePaths,
            ReportFileMapper mapper,
            AppLogger logger
    ) {
        if (filePaths == null) {
            throw new IllegalArgumentException("File paths cannot be null");
        }

        if (mapper == null) {
            throw new IllegalArgumentException("Report file mapper cannot be null");
        }

        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.filePaths = filePaths;
        this.mapper = mapper;
        this.logger = logger;
    }

    @Override
    public String generate(ParkingLot parkingLot) {
        if (parkingLot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        try {
            ensureDataDirectoryExists();

            List<String> lines = mapper.toCsvLines(parkingLot);
            Path reportFile = filePaths.getReportFile();

            Files.write(reportFile, lines);

            logger.info("Generated parking report at " + reportFile);
            return reportFile.toString();
        } catch (IOException exception) {
            logger.error("Failed to generate parking report", exception);
            return "";
        } catch (Exception exception) {
            logger.error("Unexpected error while generating parking report", exception);
            return "";
        }
    }

    private void ensureDataDirectoryExists() throws IOException {
        Path dataDirectory = filePaths.getDataDirectory();

        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
            logger.info("Created data directory at " + dataDirectory);
        }
    }
}