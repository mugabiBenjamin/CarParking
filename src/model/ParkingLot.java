package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParkingLot {
    private final List<ParkingSlot> slots;
    private final int size;

    public ParkingLot(int size) {
        this.size = size;
        this.slots = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            slots.add(new ParkingSlot(i));
        }
    }

    public int getSize() {
        return size;
    }

    public List<ParkingSlot> getSlots() {
        return new ArrayList<>(slots);
    }

    public void setSlot(int number, ParkingSlot slot) {
        if (number >= 1 && number <= size) {
            slots.set(number - 1, slot);
        }
    }

    public Optional<ParkingSlot> getSlot(int slotNumber) {
        if (slotNumber >= 1 && slotNumber <= size) {
            return Optional.ofNullable(slots.get(slotNumber - 1));
        }
        return Optional.empty();
    }
}