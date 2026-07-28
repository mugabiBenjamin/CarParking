package application.dto;

import domain.enums.SlotStatus;

public final class SlotViewData {
    private final int slotNumber;
    private final SlotStatus status;
    private final String licensePlate;

    public SlotViewData(int slotNumber, SlotStatus status, String licensePlate) {
        if (slotNumber <= 0) {
            throw new IllegalArgumentException("Slot number must be greater than zero");
        }

        this.slotNumber = slotNumber;
        this.status = status == null ? SlotStatus.EMPTY : status;
        this.licensePlate = normalizeOptionalText(licensePlate);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public boolean isOccupied() {
        return status == SlotStatus.OCCUPIED;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}