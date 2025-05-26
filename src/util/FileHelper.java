package util;

import model.ParkingSlot;
import model.Car;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {
    private static final Pattern LINE_PATTERN = Pattern.compile("\\((\\d+),\\s*(.*?)\\)");
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path PARKING_LOT_FILE = DATA_DIR.resolve("parking_lot.txt");
    private static final Path REPORT_FILE = DATA_DIR.resolve("parking_lot_report.csv");

    public static List<ParkingSlot> readParkingLotFile(int size) {
        List<ParkingSlot> slots = new ArrayList<>();
        try {
            Files.createDirectories(DATA_DIR);
            if (!Files.exists(PARKING_LOT_FILE)) {
                Logger.log("File not found: " + PARKING_LOT_FILE + ". Creating new file.");
                Files.createFile(PARKING_LOT_FILE);
                return initializeEmptySlots(size);
            }

            List<String> lines = Files.readAllLines(PARKING_LOT_FILE);
            if (lines.size() != size) {
                Logger.log("Invalid line count in " + PARKING_LOT_FILE + ": " + lines.size() + ". Expected: " + size);
                return initializeEmptySlots(size);
            }

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    Logger.log("Empty line at index " + i + " in " + PARKING_LOT_FILE);
                    return initializeEmptySlots(size);
                }
                Matcher matcher = LINE_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    Logger.log("Invalid line format at index " + i + " in " + PARKING_LOT_FILE + ": " + line);
                    return initializeEmptySlots(size);
                }
                int slotNumber;
                try {
                    slotNumber = Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    Logger.log(
                            "Invalid slot number at index " + i + " in " + PARKING_LOT_FILE + ": " + matcher.group(1));
                    return initializeEmptySlots(size);
                }
                String content = matcher.group(2).trim();
                if (slotNumber < 1 || slotNumber > size) {
                    Logger.log(
                            "Slot number out of range at index " + i + " in " + PARKING_LOT_FILE + ": " + slotNumber);
                    return initializeEmptySlots(size);
                }
                ParkingSlot slot = new ParkingSlot(slotNumber);
                if (!content.equals("EMPTY")) {
                    if (Validator.isValidPlate(content)) {
                        slot.park(new Car(content));
                    } else {
                        Logger.log("Invalid license plate at index " + i + " in " + PARKING_LOT_FILE + ": " + content);
                        return initializeEmptySlots(size);
                    }
                }
                slots.add(slot);
            }
            Logger.log("Successfully read " + slots.size() + " slots from " + PARKING_LOT_FILE);
        } catch (IOException e) {
            Logger.log("Error reading " + PARKING_LOT_FILE + ": " + e.getMessage());
            slots = initializeEmptySlots(size);
        }
        return slots;
    }

    private static List<ParkingSlot> initializeEmptySlots(int size) {
        List<ParkingSlot> slots = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            slots.add(new ParkingSlot(i));
        }
        saveParkingLotFile(slots);
        return slots;
    }

    public static boolean saveParkingLotFile(List<ParkingSlot> slots) {
        try {
            Files.createDirectories(DATA_DIR);
            List<String> lines = new ArrayList<>();
            for (ParkingSlot slot : slots) {
                String content = slot.isOccupied() ? slot.getCar().getLicensePlate() : "EMPTY";
                lines.add("(" + slot.getNumber() + ", " + content + ")");
            }
            Files.write(PARKING_LOT_FILE, lines);
            Logger.log("Successfully saved " + slots.size() + " slots to " + PARKING_LOT_FILE);
            return true;
        } catch (IOException e) {
            Logger.log("Error saving " + PARKING_LOT_FILE + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean generateReport(List<ParkingSlot> slots) {
        try {
            Files.createDirectories(DATA_DIR);
            List<String> lines = new ArrayList<>();
            lines.add("Slot Number,Status,License Plate");
            for (ParkingSlot slot : slots) {
                String status = slot.isOccupied() ? "Occupied" : "Empty";
                String plate = slot.isOccupied() ? slot.getCar().getLicensePlate() : "";
                lines.add(slot.getNumber() + "," + status + "," + plate);
            }
            Files.write(REPORT_FILE, lines);
            Logger.log("Successfully generated report to " + REPORT_FILE);
            return true;
        } catch (IOException e) {
            Logger.log("Error generating report to " + REPORT_FILE + ": " + e.getMessage());
            return false;
        }
    }
}