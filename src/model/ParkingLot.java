package model;

import java.util.*;

public class ParkingLot {
    private List<ParkingSlot> slots;

    public ParkingLot(int size) {
        slots = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            slots.add(new ParkingSlot(i));
        }
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public Optional<ParkingSlot> findFirstFreeSlot() {
        return slots.stream().filter(s -> !s.isOccupied()).findFirst();
    }
}
