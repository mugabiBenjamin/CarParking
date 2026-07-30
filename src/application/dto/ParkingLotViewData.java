package application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ParkingLotViewData {
    private final int size;
    private final int occupiedSlots;
    private final int availableSlots;
    private final List<SlotViewData> slots;

    public ParkingLotViewData(int size, int occupiedSlots, int availableSlots, List<SlotViewData> slots) {
        if (size <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }

        if (occupiedSlots < 0) {
            throw new IllegalArgumentException("Occupied slot count cannot be negative");
        }

        if (availableSlots < 0) {
            throw new IllegalArgumentException("Available slot count cannot be negative");
        }

        this.size = size;
        this.occupiedSlots = occupiedSlots;
        this.availableSlots = availableSlots;
        this.slots = slots == null ? new ArrayList<>() : new ArrayList<>(slots);
    }

    public int getSize() {
        return size;
    }

    public int getOccupiedSlots() {
        return occupiedSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public List<SlotViewData> getSlots() {
        return Collections.unmodifiableList(slots);
    }
}