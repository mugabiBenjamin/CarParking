package controller;

import model.*;
import util.Logger;
import util.MessageBox;
import util.Validator;

import java.io.IOException;
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
        // Validate license plate input and provide standardized, actionable error
        // messages
        if (plate.isBlank()) {
            MessageBox.showError("Parking failed for license plate: Enter a valid license plate (e.g., AAA 123B).");
            return;
        }

        if (!Validator.isValidPlate(plate)) {
            MessageBox.showError("Parking failed for license plate " + plate + ": Invalid format, use AAA 123B.");
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
            try {
                FileHelper.saveSlotData(lot.getSlots());
                MessageBox.showInfo("Car with license plate " + plate + " parked in slot " + slotOpt.get().getNumber());
            } catch (IOException e) {
                MessageBox.showError("Failed to save parking data for license plate " + plate,
                        "Ensure the data file is writable and there is sufficient disk space.",
                        "Try parking again or check file permissions.");
                slotOpt.get().removeCar(); // Rollback parking
                Logger.log("Rolled back parking for " + plate + " due to save failure");
            }
        } else {
            MessageBox.showError("Parking failed: No available slots.");
        }
    }

    public void unparkCar(int slotNumber) {
        var slot = lot.getSlots().get(slotNumber - 1);
        if (slot.isOccupied()) {
            String plate = slot.getCar().getPlateNumber();
            slot.removeCar();
            try {
                FileHelper.saveSlotData(lot.getSlots());
                Logger.log("Car removed: " + plate);
                MessageBox.showInfo("Car with license plate " + plate + " removed from slot " + slotNumber);
            } catch (IOException e) {
                MessageBox.showError("Failed to save parking data after removing car from slot " + slotNumber,
                        "Ensure the data file is writable and there is sufficient disk space.",
                        "The car has been removed, but the data may not be saved.");
                Logger.log("Failed to save data after unparking " + plate);
            }
        } else {
            MessageBox.showError("Unparking failed for slot " + slotNumber + ": Slot is already empty.");
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
        try {
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
        } catch (IOException e) {
            MessageBox.showError("Failed to load parking data",
                    "Ensure the data file exists and is readable.",
                    "The system will start with an empty parking lot.");
            Logger.log("Failed to load parking data: " + e.getMessage());
        }
    }
}