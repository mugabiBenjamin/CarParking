package controller;

import model.*;
import util.Logger;
import view.MessageBox;

import java.util.Optional;

public class ParkingController {
    private ParkingLot lot;

    public ParkingController(ParkingLot lot) {
        this.lot = lot;
        // Initialize data folder
        FileHelper.ensureDataFolderExists();

        // Load existing parking data on startup
        loadParkingData();
    }

    public void parkCar(String plate) {
        if (plate.isBlank()) {
            MessageBox.showError("Plate number is required.");
            return;
        }

        // Check if car is already parked
        Optional<ParkingSlot> existingCar = findCarByPlate(plate);
        if (existingCar.isPresent()) {
            MessageBox.showError("Car with plate " + plate + " is already parked in slot " +
                    existingCar.get().getNumber());
            return;
        }

        var car = new Car(plate);
        var slotOpt = lot.findFirstFreeSlot();

        if (slotOpt.isPresent()) {
            slotOpt.get().parkCar(car);
            Logger.log("Car parked: " + plate);
            MessageBox.showInfo("Car parked in slot " + slotOpt.get().getNumber());

            // Save updated parking data
            FileHelper.saveSlotData(lot.getSlots());
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

            // Save updated parking data
            FileHelper.saveSlotData(lot.getSlots());
        } else {
            MessageBox.showError("Slot already empty.");
        }
    }

    public Optional<ParkingSlot> findCarByPlate(String plate) {
        return lot.getSlots().stream()
                .filter(slot -> slot.isOccupied() &&
                        slot.getCar().getPlateNumber().equalsIgnoreCase(plate))
                .findFirst();
    }

    private void loadParkingData() {
        var slotData = FileHelper.loadSlotData();

        for (String data : slotData) {
            String[] parts = data.split(",");
            if (parts.length == 2) {
                try {
                    int slotNumber = Integer.parseInt(parts[0]);
                    String plateNumber = parts[1];

                    if (slotNumber > 0 && slotNumber <= lot.getSlots().size() && !plateNumber.equals("EMPTY")) {
                        ParkingSlot slot = lot.getSlots().get(slotNumber - 1);
                        slot.parkCar(new Car(plateNumber));
                        Logger.log("Loaded car: " + plateNumber + " in slot " + slotNumber);
                    }
                } catch (NumberFormatException e) {
                    Logger.log("Error parsing slot data: " + data);
                }
            }
        }
    }
}