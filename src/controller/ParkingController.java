package controller;

import model.*;
import util.Logger;
import view.MessageBox;

public class ParkingController {
    private ParkingLot lot;

    public ParkingController(ParkingLot lot) {
        this.lot = lot;
    }

    public void parkCar(String plate) {
        if (plate.isBlank()) {
            MessageBox.showError("Plate number is required.");
            return;
        }

        var car = new Car(plate);
        var slotOpt = lot.findFirstFreeSlot();

        if (slotOpt.isPresent()) {
            slotOpt.get().parkCar(car);
            Logger.log("Car parked: " + plate);
            MessageBox.showInfo("Car parked in slot " + slotOpt.get().getNumber());
        } else {
            MessageBox.showError("Parking is full.");
        }
    }

    public void unparkCar(int slotNumber) {
        var slot = lot.getSlots().get(slotNumber - 1);
        if (slot.isOccupied()) {
            Logger.log("Car removed: " + slot.getCar());
            slot.removeCar();
            MessageBox.showInfo("Car removed from slot " + slotNumber);
        } else {
            MessageBox.showError("Slot already empty.");
        }
    }
}
