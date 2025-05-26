package model;

public class ParkingSlot {
    private final int number;
    private Car car;

    public ParkingSlot(int number) {
        this.number = number;
        this.car = null;
    }

    public void parkCar(Car car) {
        if (isOccupied()) {
            throw new IllegalStateException("Slot " + number + " is already occupied");
        }
        this.car = car;
    }

    public void unparkCar() {
        if (!isOccupied()) {
            return; // Silently return if slot is already empty
        }
        this.car = null;
    }

    public boolean isOccupied() {
        return car != null;
    }

    public Car getCar() {
        return car;
    }

    public int getNumber() {
        return number;
    }
}