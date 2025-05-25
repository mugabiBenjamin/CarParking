package model;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import util.Logger;
import util.MessageBox;

public class FileHelper {
    private static String FILE_PATH;
    private static final String DEFAULT_CONFIG_PATH = "config.properties";

    static {
        // Initialize path from configuration
        initializeFilePath();
    }

    private static void initializeFilePath() {
        try {
            Properties properties = new Properties();
            File configFile = new File(DEFAULT_CONFIG_PATH);

            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    properties.load(fis);
                    String configuredPath = properties.getProperty("parking.data.file");
                    if (configuredPath != null && !configuredPath.isBlank()) {
                        Path basePath = Paths.get(System.getProperty("user.dir"));
                        Path absolutePath = basePath.resolve(configuredPath).normalize();
                        FILE_PATH = absolutePath.toString();
                        Logger.log("Using configured data path: " + FILE_PATH);
                        return;
                    }
                }
            }
            // Fallback to default path
            FILE_PATH = Paths.get(System.getProperty("user.dir"), "data", "parking_lot.txt").toString();
            Logger.log("Using default data path: " + FILE_PATH);
        } catch (IOException e) {
            handleIOException("Failed to initialize file path from config", e,
                    "Check if config.properties exists and is readable.");
        }
    }

    // Load license plate data for parking slots
    public static List<String> loadSlotData() throws IOException {
        List<String> data = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            Logger.log("No existing parking data file found at: " + FILE_PATH);
            return data;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.trim());
            }
        } catch (IOException e) {
            handleIOException("Failed to read parking data from " + FILE_PATH, e,
                    "Ensure the file exists and is readable.",
                    "Check file permissions or disk space.");
            throw e; // Propagate to allow caller to handle
        }
        Logger.log("Successfully loaded parking data from: " + FILE_PATH);
        return data;
    }

    // Save license plate data for parking slots
    public static void saveSlotData(List<ParkingSlot> slots) throws IOException {
        ensureDataFolderExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ParkingSlot slot : slots) {
                if (slot.isOccupied()) {
                    writer.write(slot.getNumber() + "," + slot.getCar().getPlateNumber());
                } else {
                    writer.write(slot.getNumber() + ",EMPTY");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            handleIOException("Failed to write parking data to " + FILE_PATH, e,
                    "Ensure the file is writable and there is sufficient disk space.",
                    "Check file permissions.");
            throw e; // Propagate to allow caller to handle
        }
        Logger.log("Successfully saved parking data to: " + FILE_PATH);
    }

    public static void ensureDataFolderExists() {
        File file = new File(FILE_PATH);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            try {
                boolean created = dir.mkdirs();
                if (created) {
                    Logger.log("Created directory: " + dir.getAbsolutePath());
                } else {
                    throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
                }
            } catch (IOException e) {
                handleIOException("Failed to create data directory: " + dir.getAbsolutePath(), e,
                        "Ensure the parent directory is writable.",
                        "Check system permissions or disk space.");
            }
        }
    }

    private static void handleIOException(String message, IOException e, String... recoverySteps) {
        Logger.log(message + ": " + e.getMessage());
        MessageBox.showError(message, recoverySteps);
    }
}