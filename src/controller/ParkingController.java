package controller;

import model.*;
import util.Logger;
import util.Validator;
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
        // Validate license plate input and provide actionable error messages
        if (plate.isBlank()) {
            MessageBox.showError("Enter a valid license plate (e.g., AAA 123B).");
            return;
        }

        if (!Validator.isValidPlate(plate)) {
            MessageBox.showError("Invalid license plate format, use AAA 123B.");
            return;
        }

        // Check if car is already parked
        Optional<ParkingSlot> existingCar = findCarByPlate(plate);
        if (existingCar.isPresent()) {
            MessageBox.showError("Car with license plate " + plate + " is already parked in slot " +
                    existingCar.get().getNumber() + ". Search to locate it.");
            return;
        }

        var car = new Car(plate);
        var slotOpt = lot.findFirstFreeSlot();

        if (slotOpt.isPresent()) {
            slotOpt.get().parkCar(car);
            Logger.log("Car parked: " + plate);
            // Show message with license plate and slot number
            MessageBox.showInfo("Car with license plate " + plate + " parked in slot " + slotOpt.get().getNumber());

            // Save updated parking data
            FileHelper.saveSlotData(lot.getSlots());
        } else {
            MessageBox.showError("Parking is full.");
        }
    }

    public void unparkCar(int slotNumber) {
        var slot = lot.getSlots().get(slotNumber - 1);
        if (slot.isOccupied()) {
            String plate = slot.getCar().getPlateNumber();
            Logger.log("Car removed: " + slot.getCar());
            slot.removeCar();
            // Show message with license plate and slot number
            MessageBox.showInfo("Car with license plate " + plate + " removed from slot " + slotNumber);

            // Save updated parking data
            FileHelper.saveSlotData(lot.getSlots());
        } else {
            MessageBox.showError("Slot already empty.");
        }
    }

    // Find a car by its license plate
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