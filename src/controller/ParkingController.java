package controller;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;
import util.FileHelper;
import util.Logger;
import util.MessageBox;
import view.ParkingView;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ParkingController {
    private final ParkingLot lot;
    private final ParkingView view;
    private final FileHelper fileHelper;

    public ParkingController(ParkingLot lot, ParkingView view) {
        this.lot = lot;
        this.view = view;
        this.fileHelper = new FileHelper("parking_lot.txt");
    }

    public void loadParkingData() {
        try {
            fileHelper.loadFromFile(lot);
            view.getSlotPanel().updateSlots();
            Logger.log("Parking data loaded successfully");
        } catch (IOException e) {
            Logger.error("Failed to load parking data: " + e.getMessage());
            view.updateStatusBar("Error loading parking data");
        }
    }

    public void parkCar(String licensePlate) {
        try {
            if (lot.isPlateAlreadyParked(licensePlate)) {
                String message = "Car " + licensePlate + " is already parked in the system.";
                Logger.warn(message);
                JOptionPane.showMessageDialog(
                        null,
                        message,
                        "Duplicate License Plate",
                        JOptionPane.ERROR_MESSAGE);
                view.updateStatusBar(message);
                return;
            }

            Optional<ParkingSlot> availableSlot = lot.getAvailableSlot();
            if (availableSlot.isPresent()) {
                Car car = new Car(licensePlate);
                availableSlot.get().parkCar(car);
                saveParkingData();
                view.getSlotPanel().updateSlots();
                String message = "Car " + licensePlate + " parked in slot " + availableSlot.get().getNumber();
                Logger.log(message);
                MessageBox.showInfo(message);
                view.updateStatusBar(message);
            } else {
                Logger.warn("No available slots for car " + licensePlate);
                view.updateStatusBar("Parking failed: No available slots");
            }
        } catch (IllegalArgumentException e) {
            Logger.error("Invalid license plate: " + licensePlate + ", " + e.getMessage());
            view.updateStatusBar("Parking failed: Invalid license plate");
        }
    }

    public void unparkCar(int slotNumber) {
        Optional<ParkingSlot> slot = lot.getSlot(slotNumber - 1);
        if (slot.isPresent() && slot.get().isOccupied()) {
            String licensePlate = slot.get().getCar().getPlateNumber();
            slot.get().unparkCar();
            saveParkingData();
            view.getSlotPanel().updateSlots();
            String message = "Car " + licensePlate + " unparked from slot " + slotNumber;
            Logger.log(message);
            view.updateStatusBar(message);
        } else {
            Logger.warn("No car to unpark in slot " + slotNumber);
            view.updateStatusBar("Unpark failed: Slot " + slotNumber + " is empty");
        }
    }

    public Optional<ParkingSlot> findCarByPlate(String licensePlate) {
        Optional<ParkingSlot> slot = lot.findCarByPlate(licensePlate);
        if (slot.isPresent()) {
            String message = "Car " + licensePlate + " found in slot " + slot.get().getNumber();
            Logger.log(message);
            view.updateStatusBar(message);
        } else {
            Logger.warn("Car " + licensePlate + " not found");
            view.updateStatusBar("Car " + licensePlate + " not found");
        }
        return slot;
    }

    public int batchUnpark(List<Integer> slotNumbers) {
        int unparkedCount = 0;
        for (int slotNumber : slotNumbers) {
            Optional<ParkingSlot> slot = lot.getSlot(slotNumber - 1);
            if (slot.isPresent() && slot.get().isOccupied()) {
                slot.get().unparkCar();
                unparkedCount++;
                Logger.log("Car unparked from slot " + slotNumber);
            }
        }
        if (unparkedCount > 0) {
            saveParkingData();
            view.getSlotPanel().updateSlots();
        }
        Logger.log("Batch unpark completed: " + unparkedCount + " cars unparked");
        return unparkedCount;
    }

    public void generateReport() throws IOException {
        fileHelper.generateReport(lot);
        Logger.log("Parking lot report generated");
    }

    private void saveParkingData() {
        try {
            fileHelper.save(lot);
            Logger.log("Parking data saved successfully");
        } catch (IOException e) {
            Logger.error("Failed to save parking data: " + e.getMessage());
            view.updateStatusBar("Error saving parking data");
        }
    }

    // For testing
    public ParkingLot getLot() {
        return lot;
    }
}