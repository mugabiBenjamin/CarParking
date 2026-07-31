package infrastructure.file;

import application.repositories.ParkingLotRepository;
import application.repositories.ParkingLotUpdateCommand;
import application.repositories.ParkingLotUpdateResult;
import domain.entities.ParkingLot;
import infrastructure.logging.AppLogger;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class FileParkingLotRepository implements ParkingLotRepository {
    private final FilePaths filePaths;
    private final ParkingLotFileMapper mapper;
    private final AppLogger logger;

    public FileParkingLotRepository(
            FilePaths filePaths,
            ParkingLotFileMapper mapper,
            AppLogger logger
    ) {
        if (filePaths == null) {
            throw new IllegalArgumentException("File paths cannot be null");
        }

        if (mapper == null) {
            throw new IllegalArgumentException("Parking lot file mapper cannot be null");
        }

        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.filePaths = filePaths;
        this.mapper = mapper;
        this.logger = logger;
    }

    @Override
    public synchronized ParkingLot load(int size) {
        validateSize(size);

        try {
            ensureDataDirectoryExists();

            Path parkingLotFile = filePaths.getParkingLotFile();

            if (!Files.exists(parkingLotFile)) {
                logger.info("Parking lot file not found. Creating empty parking lot file.");
                ParkingLot emptyParkingLot = new ParkingLot(size);
                writeParkingLot(emptyParkingLot);
                return emptyParkingLot;
            }

            List<String> lines = Files.readAllLines(parkingLotFile);
            return mapper.fromLines(lines, size);
        } catch (IOException exception) {
            logger.error("Failed to load parking lot file. Returning empty parking lot.", exception);
            return new ParkingLot(size);
        } catch (Exception exception) {
            logger.error("Unexpected error while loading parking lot. Returning empty parking lot.", exception);
            return new ParkingLot(size);
        }
    }

    @Override
    public synchronized <T> ParkingLotUpdateResult<T> update(int size, ParkingLotUpdateCommand<T> command) {
        validateSize(size);

        if (command == null) {
            return ParkingLotUpdateResult.failed("Parking lot update command cannot be null");
        }

        try {
            ensureDataDirectoryExists();

            ParkingLot parkingLot = load(size);
            T result = command.execute(parkingLot);

            writeParkingLot(parkingLot);

            return ParkingLotUpdateResult.committed(result);
        } catch (IOException exception) {
            logger.error("Failed to commit parking lot update", exception);
            return ParkingLotUpdateResult.failed("Failed to save parking data. Check file permissions or disk space.");
        } catch (SecurityException exception) {
            logger.error("Permission denied while committing parking lot update", exception);
            return ParkingLotUpdateResult.failed("Permission denied while saving parking data.");
        }
    }

    private void writeParkingLot(ParkingLot parkingLot) throws IOException {
        if (parkingLot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        ensureDataDirectoryExists();

        List<String> lines = mapper.toLines(parkingLot);
        Path targetFile = filePaths.getParkingLotFile();
        Path temporaryFile = targetFile.resolveSibling(targetFile.getFileName() + ".tmp");

        Files.write(temporaryFile, lines);

        try {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException exception) {
            logger.warn("Atomic file move is not supported. Falling back to regular replace.");
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void ensureDataDirectoryExists() throws IOException {
        Path dataDirectory = filePaths.getDataDirectory();

        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
            logger.info("Created data directory at " + dataDirectory);
        }
    }

    private void validateSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }
    }
}