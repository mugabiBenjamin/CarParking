package domain.entities;

import domain.exceptions.DuplicateParkedCarException;
import domain.exceptions.ParkingLotFullException;
import domain.exceptions.ParkingSlotNotFoundException;
import domain.valueobjects.LicensePlate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public ParkingSlot park(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }

        LicensePlate licensePlate = car.getLicensePlateValue();

        if (containsPlate(licensePlate)) {
            throw new DuplicateParkedCarException(licensePlate);
        }

        ParkingSlot slot = findFirstAvailableSlot()
                .orElseThrow(ParkingLotFullException::new);

        slot.park(car);
        return slot;
    }

    public Car unpark(int slotNumber) {
        ParkingSlot slot = getSlotOrThrow(slotNumber);
        return slot.unpark();
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

    public ParkingSlot getSlotOrThrow(int slotNumber) {
        return getSlot(slotNumber)
                .orElseThrow(() -> new ParkingSlotNotFoundException(slotNumber));
    }

    public void setSlot(int slotNumber, ParkingSlot slot) {
        if (!isValidSlotNumber(slotNumber)) {
            throw new ParkingSlotNotFoundException(slotNumber);
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
            if (slot.isEmpty()) {
                return Optional.of(slot);
            }
        }

        return Optional.empty();
    }

    public Optional<ParkingSlot> findSlotByPlate(String rawLicensePlate) {
        if (rawLicensePlate == null || rawLicensePlate.trim().isEmpty()) {
            return Optional.empty();
        }

        LicensePlate licensePlate = LicensePlate.of(rawLicensePlate);
        return findSlotByPlate(licensePlate);
    }

    public Optional<ParkingSlot> findSlotByPlate(LicensePlate licensePlate) {
        if (licensePlate == null) {
            return Optional.empty();
        }

        for (ParkingSlot slot : slots) {
            if (slot.hasCar(licensePlate)) {
                return Optional.of(slot);
            }
        }

        return Optional.empty();
    }

    public boolean hasAvailableSlot() {
        return findFirstAvailableSlot().isPresent();
    }

    public boolean containsPlate(String rawLicensePlate) {
        if (rawLicensePlate == null || rawLicensePlate.trim().isEmpty()) {
            return false;
        }

        return containsPlate(LicensePlate.of(rawLicensePlate));
    }

    public boolean containsPlate(LicensePlate licensePlate) {
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