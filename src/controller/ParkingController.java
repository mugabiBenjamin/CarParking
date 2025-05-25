package controller;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;
import util.FileHelper;
import util.Logger;
import util.MessageBox;
import util.Validator;
import view.ParkingView;

import java.util.Optional;

public class ParkingController {
    private final ParkingLot lot;
    private final FileHelper fileHelper;
    private final ParkingView view;

    public ParkingController(ParkingLot lot, ParkingView view) {
        this.lot = lot;
        this.view = view;
        this.fileHelper = new FileHelper("data/parking_lot.txt"); // Fixed file path
    }

    public void parkCar(String plateNumber) {
        try {
            if (!Validator.isValidPlate(plateNumber)) {
                MessageBox.showError("Parking failed: Invalid license plate format for " + plateNumber,
                        "Ensure the plate follows one of the valid formats.",
                        "Examples: UAA 123B (normal), UG 123B (government), ABC123 (personalized).");
                view.updateStatusBar("Parking failed: Invalid plate format");
                return;
            }

            Optional<ParkingSlot> existingSlot = findCarByPlate(plateNumber);
            if (existingSlot.isPresent()) {
                ParkingSlot slot = existingSlot.get();
                MessageBox.showError(
                        "Parking failed: Car with license plate " + plateNumber + " is already parked in slot "
                                + slot.getNumber(),
                        "A car with this license plate is already parked.",
                        "Search for the car to locate it or use a different license plate.");
                view.updateStatusBar("Parking failed: Car already parked");
                return;
            }

            Optional<ParkingSlot> availableSlot = lot.getAvailableSlot(); // Removed redundant cast
            if (!availableSlot.isPresent()) {
                MessageBox.showError("Parking failed: No available slots.",
                        "All parking slots are currently occupied.",
                        "Try removing a car from an occupied slot to free up space.");
                view.updateStatusBar("Parking failed: No slots available");
                return;
            }

            Car car = new Car(plateNumber);
            availableSlot.get().parkCar(car);
            fileHelper.save(lot);
            Logger.log("Car parked: " + plateNumber + " in slot " + availableSlot.get().getNumber());
            MessageBox.showInfo("Car with license plate " + plateNumber + " parked successfully in slot "
                    + availableSlot.get().getNumber());
            view.getSlotPanel().updateSlots();
            view.updateStatusBar("Parked " + plateNumber + " in slot " + availableSlot.get().getNumber());
        } catch (Exception e) {
            Logger.error("Error parking car: " + e.getMessage());
            MessageBox.showError("Failed to park car: " + e.getMessage(),
                    "An error occurred while parking the car.",
                    "Please try again or contact support if the issue persists.");
            view.updateStatusBar("Parking failed: Error");
        }
    }

    public void unparkCar(int slotNumber) {
        try {
            Optional<ParkingSlot> slotOptional = lot.getSlot(slotNumber - 1);
            if (!slotOptional.isPresent() || !slotOptional.get().isOccupied()) {
                MessageBox.showError("Unparking failed: No car in slot " + slotNumber,
                        "The selected slot is empty or invalid.",
                        "Please select an occupied slot (light red with a car icon).");
                view.updateStatusBar("Unpark failed: Slot empty");
                return;
            }

            ParkingSlot slot = slotOptional.get();
            String plateNumber = slot.getCar().getPlateNumber();
            slot.removeCar();
            fileHelper.save(lot);
            Logger.log("Car unparked: " + plateNumber + " from slot " + slotNumber);
            MessageBox.showInfo(
                    "Car with license plate " + plateNumber + " successfully unparked from slot " + slotNumber);
            view.getSlotPanel().updateSlots();
            view.updateStatusBar("Unparked " + plateNumber + " from slot " + slotNumber);
        } catch (Exception e) {
            Logger.error("Error unparking car from slot " + slotNumber + ": " + e.getMessage());
            MessageBox.showError("Failed to unpark car from slot " + slotNumber + ": " + e.getMessage(),
                    "An error occurred while unparking the car.",
                    "Please try again or contact support if the issue persists.");
            view.updateStatusBar("Unpark failed: Error");
        }
    }

    public Optional<ParkingSlot> findCarByPlate(String plateNumber) {
        Optional<ParkingSlot> foundSlot = lot.getSlots().stream()
                .filter(s -> s.isOccupied() && s.getCar().getPlateNumber().equalsIgnoreCase(plateNumber))
                .findFirst();
        if (foundSlot.isPresent()) {
            view.updateStatusBar("Found car with plate " + plateNumber + " in slot " + foundSlot.get().getNumber());
        }
        return foundSlot;
    }

    public void loadParkingData() {
        try {
            fileHelper.loadFromFile(lot);
            view.getSlotPanel().updateSlots();
            view.updateStatusBar("Parking data loaded successfully");
        } catch (Exception e) {
            Logger.error("Error loading parking data: " + e.getMessage());
            MessageBox.showError("Failed to load parking data: " + e.getMessage(),
                    "Could not load existing parking data.",
                    "The system will start with an empty parking lot.");
            view.updateStatusBar("Failed to load parking data");
        }
    }
}