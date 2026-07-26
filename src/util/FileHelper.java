package util;

import model.Car;
import model.ParkingSlot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileHelper {
    private static final int MINIMUM_SLOT_NUMBER = 1;

    private static final String EMPTY_SLOT_VALUE = "EMPTY";
    private static final String REPORT_HEADER = "Slot Number,Status,License Plate";
    private static final String STATUS_OCCUPIED = "Occupied";
    private static final String STATUS_EMPTY = "Empty";

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String PARKING_LOT_FILE_NAME = "parking_lot.txt";
    private static final String REPORT_FILE_NAME = "parking_lot_report.csv";

    private static final Pattern PARKING_LOT_LINE_PATTERN = Pattern.compile("\\((\\d+),\\s*(.*?)\\)");

    private static final Path DATA_DIRECTORY = Paths.get(DATA_DIRECTORY_NAME);
    private static final Path PARKING_LOT_FILE = DATA_DIRECTORY.resolve(PARKING_LOT_FILE_NAME);
    private static final Path REPORT_FILE = DATA_DIRECTORY.resolve(REPORT_FILE_NAME);

    private FileHelper() {
        throw new UnsupportedOperationException("FileHelper class cannot be instantiated");
    }

    public static List<ParkingSlot> readParkingLotFile(int size) {
        if (size <= 0) {
            Logger.warn("Invalid parking lot size supplied while reading file: " + size);
            return new ArrayList<>();
        }

        try {
            ensureDataDirectoryExists();

            if (!Files.exists(PARKING_LOT_FILE)) {
                Logger.info("Parking lot file not found. Creating empty file at " + PARKING_LOT_FILE);
                Files.createFile(PARKING_LOT_FILE);
                return initializeEmptySlots(size);
            }

            List<String> lines = Files.readAllLines(PARKING_LOT_FILE);

            if (lines.size() != size) {
                Logger.warn(
                        "Invalid parking lot file line count. Expected " + size + " but found " + lines.size()
                );
                return initializeEmptySlots(size);
            }

            List<ParkingSlot> slots = parseParkingLotLines(lines, size);

            if (slots.size() != size) {
                Logger.warn("Parsed parking slot count mismatch. Initializing empty parking lot.");
                return initializeEmptySlots(size);
            }

            Logger.info("Successfully loaded " + slots.size() + " parking slots from " + PARKING_LOT_FILE);
            return slots;
        } catch (IOException exception) {
            Logger.error("Failed to read parking lot file", exception);
            return initializeEmptySlots(size);
        } catch (Exception exception) {
            Logger.error("Unexpected error while reading parking lot file", exception);
            return initializeEmptySlots(size);
        }
    }

    public static boolean saveParkingLotFile(List<ParkingSlot> slots) {
        if (slots == null) {
            Logger.warn("Cannot save parking lot file because slot list is null");
            return false;
        }

        try {
            ensureDataDirectoryExists();

            List<String> lines = new ArrayList<>();

            for (ParkingSlot slot : slots) {
                if (slot == null) {
                    Logger.warn("Skipped null parking slot while saving parking lot file");
                    continue;
                }

                lines.add(formatParkingLotLine(slot));
            }

            Files.write(PARKING_LOT_FILE, lines);
            Logger.info("Successfully saved " + lines.size() + " parking slots to " + PARKING_LOT_FILE);
            return true;
        } catch (IOException exception) {
            Logger.error("Failed to save parking lot file", exception);
            return false;
        } catch (Exception exception) {
            Logger.error("Unexpected error while saving parking lot file", exception);
            return false;
        }
    }

    public static boolean generateReport(List<ParkingSlot> slots) {
        if (slots == null) {
            Logger.warn("Cannot generate report because slot list is null");
            return false;
        }

        try {
            ensureDataDirectoryExists();

            List<String> lines = new ArrayList<>();
            lines.add(REPORT_HEADER);

            for (ParkingSlot slot : slots) {
                if (slot == null) {
                    Logger.warn("Skipped null parking slot while generating report");
                    continue;
                }

                lines.add(formatReportLine(slot));
            }

            Files.write(REPORT_FILE, lines);
            Logger.info("Successfully generated parking report at " + REPORT_FILE);
            return true;
        } catch (IOException exception) {
            Logger.error("Failed to generate parking report", exception);
            return false;
        } catch (Exception exception) {
            Logger.error("Unexpected error while generating parking report", exception);
            return false;
        }
    }

    private static List<ParkingSlot> parseParkingLotLines(List<String> lines, int size) {
        List<ParkingSlot> slots = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index) == null ? "" : lines.get(index).trim();

            if (line.isEmpty()) {
                Logger.warn("Empty line found in parking lot file at line " + (index + 1));
                return initializeEmptySlots(size);
            }

            Matcher matcher = PARKING_LOT_LINE_PATTERN.matcher(line);

            if (!matcher.matches()) {
                Logger.warn("Invalid parking lot file format at line " + (index + 1) + ": " + line);
                return initializeEmptySlots(size);
            }

            int slotNumber = parseSlotNumber(matcher.group(1), index, size);

            if (slotNumber == -1) {
                return initializeEmptySlots(size);
            }

            String slotContent = matcher.group(2) == null ? "" : matcher.group(2).trim();

            if (!isValidSlotNumber(slotNumber, size)) {
                Logger.warn("Slot number out of range at line " + (index + 1) + ": " + slotNumber);
                return initializeEmptySlots(size);
            }

            ParkingSlot slot = new ParkingSlot(slotNumber);

            if (!EMPTY_SLOT_VALUE.equalsIgnoreCase(slotContent)) {
                String normalizedPlate = Validator.normalizePlate(slotContent);

                if (!Validator.isValidPlate(normalizedPlate)) {
                    Logger.warn("Invalid license plate found at line " + (index + 1) + ": " + slotContent);
                    return initializeEmptySlots(size);
                }

                slot.park(new Car(normalizedPlate));
            }

            slots.add(slot);
        }

        return slots;
    }

    private static int parseSlotNumber(String rawSlotNumber, int index, int size) {
        try {
            int slotNumber = Integer.parseInt(rawSlotNumber);

            if (!isValidSlotNumber(slotNumber, size)) {
                Logger.warn("Invalid slot number at line " + (index + 1) + ": " + slotNumber);
                return -1;
            }

            return slotNumber;
        } catch (NumberFormatException exception) {
            Logger.warn("Unable to parse slot number at line " + (index + 1) + ": " + rawSlotNumber);
            return -1;
        }
    }

    private static List<ParkingSlot> initializeEmptySlots(int size) {
        List<ParkingSlot> slots = new ArrayList<>();

        for (int slotNumber = MINIMUM_SLOT_NUMBER; slotNumber <= size; slotNumber++) {
            slots.add(new ParkingSlot(slotNumber));
        }

        boolean saved = saveParkingLotFile(slots);

        if (!saved) {
            Logger.warn("Initialized empty parking lot in memory, but failed to save it to file");
        }

        return slots;
    }

    private static String formatParkingLotLine(ParkingSlot slot) {
        String content = slot.isOccupied()
                ? slot.getCar().getLicensePlate()
                : EMPTY_SLOT_VALUE;

        return "(" + slot.getNumber() + ", " + content + ")";
    }

    private static String formatReportLine(ParkingSlot slot) {
        String status = slot.isOccupied() ? STATUS_OCCUPIED : STATUS_EMPTY;
        String plate = slot.isOccupied() ? escapeCsvValue(slot.getCar().getLicensePlate()) : "";

        return slot.getNumber() + "," + status + "," + plate;
    }

    private static String escapeCsvValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String sanitizedValue = preventCsvFormulaInjection(value);

        if (sanitizedValue.contains(",") || sanitizedValue.contains("\"") || sanitizedValue.contains("\n")) {
            return "\"" + sanitizedValue.replace("\"", "\"\"") + "\"";
        }

        return sanitizedValue;
    }

    private static String preventCsvFormulaInjection(String value) {
        String trimmedValue = value.trim();

        if (trimmedValue.startsWith("=")
                || trimmedValue.startsWith("+")
                || trimmedValue.startsWith("-")
                || trimmedValue.startsWith("@")) {
            return "'" + trimmedValue;
        }

        return trimmedValue;
    }

    private static boolean isValidSlotNumber(int slotNumber, int size) {
        return slotNumber >= MINIMUM_SLOT_NUMBER && slotNumber <= size;
    }

    private static void ensureDataDirectoryExists() throws IOException {
        if (!Files.exists(DATA_DIRECTORY)) {
            Files.createDirectories(DATA_DIRECTORY);
            Logger.info("Created data directory at " + DATA_DIRECTORY);
        }
    }
}