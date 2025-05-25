package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParkingLot {
    private final List<ParkingSlot> slots;

    public ParkingLot(int size) {
        slots = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            slots.add(new ParkingSlot(i));
        }
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public Optional<ParkingSlot> getSlot(int index) {
        if (index >= 0 && index < slots.size()) {
            return Optional.of(slots.get(index));
        }
        return Optional.empty();
    }

    public Optional<ParkingSlot> getAvailableSlot() {
        return slots.stream()
                .filter(slot -> !slot.isOccupied())
                .findFirst();
    }

    public Optional<ParkingSlot> findCarByPlate(String licensePlate) {
        return slots.stream()
                .filter(slot -> slot.isOccupied() && slot.getCar().getPlateNumber().equalsIgnoreCase(licensePlate))
                .findFirst();
    }

    public boolean isPlateAlreadyParked(String licensePlate) {
        return findCarByPlate(licensePlate).isPresent();
    }
}