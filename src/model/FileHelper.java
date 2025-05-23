package model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileHelper {

    private static String FILE_PATH;
    private static final String DEFAULT_CONFIG_PATH = "config.properties";

    static {
        // Initialize path from configuration
        initializeFilePath();
    }

    private static void initializeFilePath() {
        // First try to load from config file
        try {
            Properties properties = new Properties();
            File configFile = new File(DEFAULT_CONFIG_PATH);

            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    properties.load(fis);
                    String configuredPath = properties.getProperty("parking.data.file");

                    if (configuredPath != null && !configuredPath.isBlank()) {
                        // Convert to absolute path based on user.dir
                        Path basePath = Paths.get(System.getProperty("user.dir"));
                        Path absolutePath = basePath.resolve(configuredPath).normalize();
                        FILE_PATH = absolutePath.toString();
                        System.out.println("Using configured data path: " + FILE_PATH);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }

        // Fallback to a sensible default if config loading fails
        FILE_PATH = Paths.get(System.getProperty("user.dir"), "data", "parking_lot.txt").toString();
        System.out.println("Using default data path: " + FILE_PATH);
    }

    // Load license plate data for parking slots
    public static List<String> loadSlotData() {
        List<String> data = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return data;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading license plate data: " + e.getMessage());
        }

        return data;
    }

    // Save license plate data for parking slots
    public static void saveSlotData(List<ParkingSlot> slots) {
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
            System.err.println("Error writing license plate data: " + e.getMessage());
        }
    }

    public static void ensureDataFolderExists() {
        File file = new File(FILE_PATH);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("Created directory: " + dir.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + dir.getAbsolutePath());
            }
        }
    }
}