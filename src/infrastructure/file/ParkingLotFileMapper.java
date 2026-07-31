package infrastructure.file;

import domain.entities.Car;
import domain.entities.ParkingLot;
import domain.entities.ParkingSlot;
import domain.valueobjects.LicensePlate;
import infrastructure.logging.AppLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParkingLotFileMapper {
    private static final String EMPTY_SLOT_VALUE = "EMPTY";
    private static final Pattern PARKING_LOT_LINE_PATTERN = Pattern.compile("\\((\\d+),\\s*(.*?)\\)");

    private final AppLogger logger;

    public ParkingLotFileMapper(AppLogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.logger = logger;
    }

    public ParkingLot fromLines(List<String> lines, int size) {
        ParkingLot parkingLot = new ParkingLot(size);

        if (lines == null || lines.isEmpty()) {
            logger.info("Parking lot file is empty. Initializing empty parking lot.");
            return parkingLot;
        }

        if (lines.size() != size) {
            logger.warn("Parking lot file line count mismatch. Expected " + size + " but found " + lines.size());
            return parkingLot;
        }

        for (int index = 0; index < lines.size(); index++) {
            String line = normalize(lines.get(index));

            if (line.isEmpty()) {
                logger.warn("Empty parking lot file line at " + (index + 1) + ". Initializing empty parking lot.");
                return new ParkingLot(size);
            }

            Matcher matcher = PARKING_LOT_LINE_PATTERN.matcher(line);

            if (!matcher.matches()) {
                logger.warn("Invalid parking lot file format at line " + (index + 1) + ": " + line);
                return new ParkingLot(size);
            }

            int slotNumber = parseSlotNumber(matcher.group(1), index + 1, size);

            if (slotNumber == -1) {
                return new ParkingLot(size);
            }

            String content = normalize(matcher.group(2));

            if (EMPTY_SLOT_VALUE.equalsIgnoreCase(content)) {
                continue;
            }

            try {
                LicensePlate licensePlate = LicensePlate.of(content);
                parkingLot.setSlot(slotNumber, createOccupiedSlot(slotNumber, licensePlate));
            } catch (Exception exception) {
                logger.warn("Invalid license plate in parking data at line " + (index + 1) + ". Initializing empty lot.");
                return new ParkingLot(size);
            }
        }

        return parkingLot;
    }

    public List<String> toLines(ParkingLot parkingLot) {
        if (parkingLot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        List<String> lines = new ArrayList<>();

        for (ParkingSlot slot : parkingLot.getSlots()) {
            lines.add(toLine(slot));
        }

        return lines;
    }

    private ParkingSlot createOccupiedSlot(int slotNumber, LicensePlate licensePlate) {
        ParkingSlot slot = new ParkingSlot(slotNumber);
        slot.park(new Car(licensePlate));
        return slot;
    }

    private String toLine(ParkingSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Parking slot cannot be null");
        }

        String content = slot.isOccupied()
                ? slot.getCar().getLicensePlate()
                : EMPTY_SLOT_VALUE;

        return "(" + slot.getNumber() + ", " + content + ")";
    }

    private int parseSlotNumber(String value, int lineNumber, int size) {
        try {
            int slotNumber = Integer.parseInt(value);

            if (slotNumber < 1 || slotNumber > size) {
                logger.warn("Slot number out of range at line " + lineNumber + ": " + slotNumber);
                return -1;
            }

            return slotNumber;
        } catch (NumberFormatException exception) {
            logger.warn("Invalid slot number at line " + lineNumber + ": " + value);
            return -1;
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}