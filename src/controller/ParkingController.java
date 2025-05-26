package controller;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;
import util.FileHelper;
import util.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ParkingController {
    private final ParkingLot lot;
    private final FileHelper fileHelper;
    private final ParkingListener listener;
    private static final String REPORT_FILE_PATH = "src/data/parking_lot_report.csv";

    public ParkingController(ParkingLot lot, ParkingListener listener) {
        this.lot = lot;
        this.listener = listener;
        this.fileHelper = new FileHelper("parking_lot.txt");
    }

    public void loadParkingData() {
        try {
            fileHelper.loadFromFile(lot);
            String message = "Parking data loaded successfully";
            listener.onLoadDataResult(new LoadDataResult(true, message));
            listener.onStatusUpdate(message);
            Logger.log(message);
        } catch (IOException e) {
            String message = "Error loading parking data: " + e.getMessage();
            listener.onLoadDataResult(new LoadDataResult(false, message));
            listener.onStatusUpdate(message);
            Logger.error("Failed to load parking data: " + e.getMessage());
        }
    }

    public void parkCar(String licensePlate) {
        try {
            if (lot.isPlateAlreadyParked(licensePlate)) {
                String message = "Car " + licensePlate + " is already parked in the system.";
                listener.onParkResult(new ParkResult(false, message, null, licensePlate));
                Logger.warn(message);
                return;
            }

            Optional<ParkingSlot> availableSlot = lot.getAvailableSlot();
            if (availableSlot.isPresent()) {
                Car car = new Car(licensePlate);
                availableSlot.get().parkCar(car);
                saveParkingData();
                String message = "Car " + licensePlate + " parked in slot " + availableSlot.get().getNumber();
                listener.onParkResult(new ParkResult(true, message, availableSlot.get(), licensePlate));
                Logger.log(message);
            } else {
                String message = "Parking failed: No available slots";
                listener.onParkResult(new ParkResult(false, message, null, licensePlate));
                Logger.warn("No available slots for car " + licensePlate);
            }
        } catch (IllegalArgumentException e) {
            String message = "Parking failed: Invalid license plate - " + e.getMessage();
            listener.onParkResult(new ParkResult(false, message, null, licensePlate));
            Logger.error("Invalid license plate: " + licensePlate + ", " + e.getMessage());
        }
    }

    public void unparkCar(int slotNumber) {
        Optional<ParkingSlot> slot = lot.getSlot(slotNumber - 1);
        if (slot.isPresent() && slot.get().isOccupied()) {
            String licensePlate = slot.get().getCar().getPlateNumber();
            slot.get().unparkCar();
            saveParkingData();
            String message = "Car " + licensePlate + " unparked from slot " + slotNumber;
            listener.onUnparkResult(new UnparkResult(true, message, slotNumber, licensePlate));
            Logger.log(message);
        } else {
            String message = "Unpark failed: Slot " + slotNumber + " is empty";
            listener.onUnparkResult(new UnparkResult(false, message, slotNumber, null));
            Logger.warn("No car to unpark in slot " + slotNumber);
        }
    }

    public void findCarByPlate(String licensePlate) {
        Optional<ParkingSlot> slot = lot.findCarByPlate(licensePlate);
        if (slot.isPresent()) {
            String message = "Car " + licensePlate + " found in slot " + slot.get().getNumber();
            listener.onFindCarResult(new FindCarResult(true, message, slot.get()));
            Logger.log(message);
        } else {
            String message = "Car " + licensePlate + " not found";
            listener.onFindCarResult(new FindCarResult(false, message, null));
            Logger.warn(message);
        }
    }

    public void batchUnpark(List<Integer> slotNumbers) {
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
        }
        String message = "Batch unparked " + unparkedCount + " car(s)";
        listener.onBatchUnparkResult(new BatchUnparkResult(unparkedCount, message));
        Logger.log("Batch unpark completed: " + unparkedCount + " cars unparked");
    }

    public void generateReport() {
        try {
            fileHelper.generateReport(lot);
            String message = "Report generated successfully at: " + REPORT_FILE_PATH;
            listener.onReportResult(new ReportResult(true, message, REPORT_FILE_PATH));
            Logger.log("Parking lot report generated at: " + REPORT_FILE_PATH);
        } catch (IOException e) {
            String message = "Failed to generate report: " + e.getMessage();
            listener.onReportResult(new ReportResult(false, message, REPORT_FILE_PATH));
            Logger.error("Failed to generate report: " + e.getMessage());
        }
    }

    private void saveParkingData() {
        try {
            fileHelper.save(lot);
            Logger.log("Parking data saved successfully");
        } catch (IOException e) {
            String message = "Error saving parking data: " + e.getMessage();
            listener.onStatusUpdate(message);
            Logger.error("Failed to save parking data: " + e.getMessage());
        }
    }

    // For testing
    public ParkingLot getLot() {
        return lot;
    }
}