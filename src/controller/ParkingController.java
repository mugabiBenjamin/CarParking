package controller;

import model.Car;
import model.ParkingLot;
import model.ParkingSlot;
import util.FileHelper;
import util.Logger;
import util.Validator;
import java.util.ArrayList;
import java.util.List;

public class ParkingController {
    private final ParkingLot lot;
    private final ParkingListener listener;

    public ParkingController(ParkingLot lot, ParkingListener listener) {
        this.lot = lot;
        this.listener = listener;
    }

    public void loadParkingData() {
        List<ParkingSlot> slots = FileHelper.readParkingLotFile(lot.getSize());
        if (slots.size() == lot.getSize()) {
            for (ParkingSlot slot : slots) {
                lot.setSlot(slot.getNumber(), slot);
            }
            listener.onLoadDataResult(new Result(true, "Loaded parking data successfully"));
            listener.onStatusUpdate("Parking data loaded");
        } else {
            Logger.log("Failed to load parking data: slot count mismatch. Expected " + lot.getSize() + ", got "
                    + slots.size());
            listener.onLoadDataResult(
                    new Result(false, "Failed to load parking data due to invalid file format. Using empty lot."));
            listener.onStatusUpdate("Initialized empty parking lot");
        }
    }

    public void parkCar(String licensePlate) {
        if (!Validator.isValidPlate(licensePlate)) {
            listener.onParkResult(new Result(false, "Invalid license plate: " + licensePlate));
            return;
        }
        // Check for duplicate license plate
        for (ParkingSlot slot : lot.getSlots()) {
            if (slot.isOccupied() && slot.getCar().getLicensePlate().equals(licensePlate)) {
                listener.onParkResult(
                        new Result(false, "Car with license plate " + licensePlate + " is already parked"));
                return;
            }
        }
        for (ParkingSlot slot : lot.getSlots()) {
            if (!slot.isOccupied()) {
                slot.park(new Car(licensePlate));
                FileHelper.saveParkingLotFile(lot.getSlots());
                listener.onParkResult(new Result(true, "Car " + licensePlate + " parked in slot " + slot.getNumber()));
                return;
            }
        }
        listener.onParkResult(new Result(false, "No available slots for " + licensePlate));
    }

    public void unparkCar(int slotNumber) {
        if (slotNumber < 1 || slotNumber > lot.getSize()) {
            listener.onUnparkResult(new Result(false, "Invalid slot number: " + slotNumber));
            return;
        }
        ParkingSlot slot = lot.getSlot(slotNumber).orElse(null);
        if (slot == null || !slot.isOccupied()) {
            listener.onUnparkResult(new Result(false, "Slot " + slotNumber + " is already empty"));
            return;
        }
        String licensePlate = slot.getCar().getLicensePlate();
        slot.unpark();
        FileHelper.saveParkingLotFile(lot.getSlots());
        listener.onUnparkResult(new Result(true, "Car " + licensePlate + " unparked from slot " + slotNumber));
    }

    public void findCarByPlate(String licensePlate) {
        if (!Validator.isValidPlate(licensePlate)) {
            listener.onFindCarResult(new Result(false, null, "Invalid license plate: " + licensePlate));
            return;
        }
        for (ParkingSlot slot : lot.getSlots()) {
            if (slot.isOccupied() && slot.getCar().getLicensePlate().equals(licensePlate)) {
                listener.onFindCarResult(
                        new Result(true, slot, "Car " + licensePlate + " found in slot " + slot.getNumber()));
                return;
            }
        }
        listener.onFindCarResult(new Result(false, null, "Car " + licensePlate + " not found"));
    }

    public void batchUnpark(List<Integer> slotNumbers) {
        if (slotNumbers.isEmpty()) {
            listener.onBatchUnparkResult(new Result(0, "No slots selected for batch unpark"));
            return;
        }
        int unparkedCount = 0;
        List<String> unparkedPlates = new ArrayList<>();
        for (Integer slotNumber : slotNumbers) {
            if (slotNumber < 1 || slotNumber > lot.getSize()) {
                continue;
            }
            ParkingSlot slot = lot.getSlot(slotNumber).orElse(null);
            if (slot != null && slot.isOccupied()) {
                unparkedPlates.add(slot.getCar().getLicensePlate());
                slot.unpark();
                unparkedCount++;
            }
        }
        if (unparkedCount > 0) {
            FileHelper.saveParkingLotFile(lot.getSlots());
            listener.onBatchUnparkResult(new Result(unparkedCount,
                    "Unparked " + unparkedCount + " cars: " + String.join(", ", unparkedPlates)));
        } else {
            listener.onBatchUnparkResult(new Result(0, "No occupied slots selected"));
        }
    }

    public void generateReport() {
        boolean success = FileHelper.generateReport(lot.getSlots());
        if (success) {
            listener.onReportResult(new Result(true, "Report generated successfully at data/parking_lot_report.csv"));
        } else {
            listener.onReportResult(
                    new Result(false, "Failed to generate report. Check file permissions or disk space."));
        }
    }
}