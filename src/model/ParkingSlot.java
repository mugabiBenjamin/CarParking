package model;

public class ParkingSlot {
    private final int number;
    private Car car;

    public ParkingSlot(int number) {
        this.number = number;
        this.car = null;
    }

    public void park(Car car) {
        this.car = car;
    }

    public void unpark() {
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