package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParkingLot {
    private final List<ParkingSlot> slots;

    public ParkingLot(int capacity) {
        slots = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            slots.add(new ParkingSlot(i + 1));
        }
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public Optional<ParkingSlot> getAvailableSlot() {
        return slots.stream()
                .filter(slot -> !slot.isOccupied())
                .findFirst();
    }

    public Optional<ParkingSlot> getSlot(int index) {
        if (index >= 0 && index < slots.size()) {
            return Optional.of(slots.get(index));
        }
        return Optional.empty();
    }
}