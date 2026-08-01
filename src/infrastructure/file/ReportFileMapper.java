package infrastructure.file;

import domain.entities.ParkingLot;
import domain.entities.ParkingSlot;

import java.util.ArrayList;
import java.util.List;

public final class ReportFileMapper {
    private static final String REPORT_HEADER = "Slot Number,Status,License Plate";
    private static final String STATUS_OCCUPIED = "Occupied";
    private static final String STATUS_EMPTY = "Empty";

    public List<String> toCsvLines(ParkingLot parkingLot) {
        if (parkingLot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        List<String> lines = new ArrayList<>();
        lines.add(REPORT_HEADER);

        for (ParkingSlot slot : parkingLot.getSlots()) {
            lines.add(toCsvLine(slot));
        }

        return lines;
    }

    private String toCsvLine(ParkingSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Parking slot cannot be null");
        }

        String status = slot.isOccupied() ? STATUS_OCCUPIED : STATUS_EMPTY;
        String licensePlate = slot.isOccupied() ? escapeCsvValue(slot.getCar().getLicensePlate()) : "";

        return slot.getNumber() + "," + status + "," + licensePlate;
    }

    private String escapeCsvValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String safeValue = preventFormulaInjection(value);

        if (safeValue.contains(",") || safeValue.contains("\"") || safeValue.contains("\n")) {
            return "\"" + safeValue.replace("\"", "\"\"") + "\"";
        }

        return safeValue;
    }

    private String preventFormulaInjection(String value) {
        String trimmedValue = value.trim();

        if (trimmedValue.startsWith("=")
                || trimmedValue.startsWith("+")
                || trimmedValue.startsWith("-")
                || trimmedValue.startsWith("@")) {
            return "'" + trimmedValue;
        }

        return trimmedValue;
    }
}