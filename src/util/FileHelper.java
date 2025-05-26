package util;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {
    private final String filePath;
    private static final Pattern LINE_PATTERN = Pattern.compile("^\\((\\d+),\\s*([A-Z0-9\\s]{1,8}|EMPTY)\\)$");
    private static final String BASE_PATH = "data/";

    public FileHelper(String fileName) {
        this.filePath = BASE_PATH + fileName;
    }

    public void loadFromFile(ParkingLot lot) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        List<String> lines = new ArrayList<>();

        // Ensure parent directory exists
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getPath());
            }
            Logger.log("FileHelper: Created directory " + parentDir.getPath());
        }

        // Read file if it exists
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line.trim());
                }
            } catch (IOException e) {
                Logger.error("FileHelper: Failed to read file: " + e.getMessage());
                throw new IOException("Unable to read parking_lot.txt: " + e.getMessage());
            }
        } else {
            // Initialize empty slots and create new file
            initializeEmptySlots(lot);
            save(lot);
            Logger.log("FileHelper: Created new parking_lot.txt with empty slots at " + filePath);
            return;
        }

        // Validate file content
        try {
            validateFileContent(lines, lot.getSlots().size());
        } catch (IOException e) {
            Logger.error("FileHelper: Validation failed: " + e.getMessage());
            initializeEmptySlots(lot);
            save(lot);
            throw new IOException("Invalid file format: " + e.getMessage());
        }

        // Load valid data
        for (String line : lines) {
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (matcher.matches()) {
                int slotNumber = Integer.parseInt(matcher.group(1)) - 1;
                String content = matcher.group(2).trim();
                ParkingSlot slot = lot.getSlot(slotNumber)
                        .orElseThrow(() -> new IOException("Invalid slot number in file: " + (slotNumber + 1)));
                if (!content.equals("EMPTY")) {
                    slot.parkCar(new Car(content));
                    Logger.log("FileHelper: Loaded slot " + (slotNumber + 1) + " with plate " + content);
                } else {
                    slot.unparkCar();
                    Logger.log("FileHelper: Loaded slot " + (slotNumber + 1) + " as EMPTY");
                }
            }
        }
        Logger.log("FileHelper: Successfully loaded " + lines.size() + " slots from " + filePath);
    }

    private void validateFileContent(List<String> lines, int slotCount) throws IOException {
        if (lines.isEmpty()) {
            throw new IOException("File is empty");
        }
        if (lines.size() != slotCount) {
            throw new IOException("Invalid line count: expected " + slotCount + ", found " + lines.size());
        }

        Set<Integer> slotNumbers = new HashSet<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IOException("Invalid format at line " + (i + 1) + ": " + line);
            }

            int slotNumber = Integer.parseInt(matcher.group(1));
            String content = matcher.group(2).trim();

            // Validate slot number
            if (slotNumber < 1 || slotNumber > slotCount) {
                throw new IOException("Invalid slot number at line " + (i + 1) + ": " + slotNumber);
            }
            if (!slotNumbers.add(slotNumber)) {
                throw new IOException("Duplicate slot number at line " + (i + 1) + ": " + slotNumber);
            }

            // Validate license plate
            if (!content.equals("EMPTY") && !Validator.isValidPlate(content)) {
                throw new IOException("Invalid license plate at line " + (i + 1) + ": " + content);
            }
        }
        Logger.log("FileHelper: Validated " + lines.size() + " lines in " + filePath);
    }

    private void initializeEmptySlots(ParkingLot lot) {
        int clearedCount = 0;
        for (ParkingSlot slot : lot.getSlots()) {
            if (slot.isOccupied()) {
                slot.unparkCar();
                clearedCount++;
            }
        }
        Logger.log("FileHelper: Initialized " + lot.getSlots().size() + " slots, cleared " + clearedCount
                + " occupied slots");
    }

    public void save(ParkingLot lot) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getPath());
            }
            Logger.log("FileHelper: Created directory " + parentDir.getPath());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (ParkingSlot slot : lot.getSlots()) {
                String content = slot.isOccupied() ? slot.getCar().getPlateNumber() : "EMPTY";
                writer.write(String.format("(%d, %s)%n", slot.getNumber(), content));
            }
        } catch (IOException e) {
            Logger.error("FileHelper: Failed to save file: " + e.getMessage());
            throw new IOException("Unable to save parking_lot.txt: " + e.getMessage());
        }
        Logger.log("FileHelper: Saved " + lot.getSlots().size() + " slots to " + filePath);
    }

    public void generateReport(ParkingLot lot) throws IOException {
        File reportFile = new File(BASE_PATH + "parking_lot_report.csv");
        File parentDir = reportFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Logger.error("FileHelper: Failed to create directory for report: " + parentDir.getPath());
                throw new IOException("Failed to create directory for report: " + parentDir.getPath());
            }
            Logger.log("FileHelper: Created directory " + parentDir.getPath());
        }
        if (!reportFile.getParentFile().canWrite()) {
            Logger.error("FileHelper: No write permission for directory: " + parentDir.getPath());
            throw new IOException("No write permission for directory: " + parentDir.getPath());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("Slot Number,Status,License Plate\n");
            for (ParkingSlot slot : lot.getSlots()) {
                String status = slot.isOccupied() ? "Occupied" : "Empty";
                String licensePlate = slot.isOccupied() ? slot.getCar().getPlateNumber() : "N/A";
                writer.write(String.format("%d,%s,%s\n", slot.getNumber(), status, licensePlate));
            }
        } catch (IOException e) {
            Logger.error("FileHelper: Failed to generate report: " + e.getMessage());
            throw new IOException("Unable to generate report: " + e.getMessage());
        }
        Logger.log("FileHelper: Generated report at " + reportFile.getPath());
    }
}