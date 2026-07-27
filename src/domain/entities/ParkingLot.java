package domain.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import util.Validator;

public final class ParkingLot {
    private final List<ParkingSlot> slots;
    private final int size;

    public ParkingLot(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }

        this.size = size;
        this.slots = new ArrayList<>();

        for (int slotNumber = 1; slotNumber <= size; slotNumber++) {
            slots.add(new ParkingSlot(slotNumber));
        }
    }

    public int getSize() {
        return size;
    }

    public List<ParkingSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public Optional<ParkingSlot> getSlot(int slotNumber) {
        if (!isValidSlotNumber(slotNumber)) {
            return Optional.empty();
        }

        return Optional.of(slots.get(slotNumber - 1));
    }

    public void setSlot(int slotNumber, ParkingSlot slot) {
        if (!isValidSlotNumber(slotNumber)) {
            throw new IllegalArgumentException("Invalid slot number: " + slotNumber);
        }

        if (slot == null) {
            throw new IllegalArgumentException("Parking slot cannot be null");
        }

        if (slot.getNumber() != slotNumber) {
            throw new IllegalArgumentException("Slot number mismatch");
        }

        slots.set(slotNumber - 1, slot);
    }

    public Optional<ParkingSlot> findFirstAvailableSlot() {
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied()) {
                return Optional.of(slot);
            }
        }

        return Optional.empty();
    }

    public Optional<ParkingSlot> findSlotByPlate(String licensePlate) {
        String normalizedPlate = Validator.normalizePlate(licensePlate);

        if (normalizedPlate.isEmpty()) {
            return Optional.empty();
        }

        for (ParkingSlot slot : slots) {
            if (slot.hasCar(normalizedPlate)) {
                return Optional.of(slot);
            }
        }

        return Optional.empty();
    }

    public boolean hasAvailableSlot() {
        return findFirstAvailableSlot().isPresent();
    }

    public boolean containsPlate(String licensePlate) {
        return findSlotByPlate(licensePlate).isPresent();
    }

    public int getOccupiedSlotCount() {
        int count = 0;

        for (ParkingSlot slot : slots) {
            if (slot.isOccupied()) {
                count++;
            }
        }

        return count;
    }

    public int getAvailableSlotCount() {
        return size - getOccupiedSlotCount();
    }

    public boolean isFull() {
        return getOccupiedSlotCount() == size;
    }

    private boolean isValidSlotNumber(int slotNumber) {
        return slotNumber >= 1 && slotNumber <= size;
    }
}