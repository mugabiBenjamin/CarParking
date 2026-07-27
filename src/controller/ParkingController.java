package controller;

import domain.entities.Car;
import domain.entities.ParkingLot;
import domain.entities.ParkingSlot;
import util.FileHelper;
import util.Logger;
import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ParkingController {
    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";

    private final ParkingLot lot;
    private final ParkingListener listener;

    public ParkingController(ParkingLot lot, ParkingListener listener) {
        if (lot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        if (listener == null) {
            throw new IllegalArgumentException("Parking listener cannot be null");
        }

        this.lot = lot;
        this.listener = listener;
    }

    public void loadParkingData() {
        try {
            List<ParkingSlot> loadedSlots = FileHelper.readParkingLotFile(lot.getSize());

            if (loadedSlots.size() != lot.getSize()) {
                Logger.warn("Loaded slot count mismatch. Expected " + lot.getSize() + " but got " + loadedSlots.size());
                listener.onLoadDataResult(Result.failure("Failed to load parking data. Using empty parking lot."));
                listener.onStatusUpdate("Initialized empty parking lot");
                return;
            }

            for (ParkingSlot slot : loadedSlots) {
                if (slot == null) {
                    Logger.warn("Loaded parking data contained a null slot");
                    listener.onLoadDataResult(Result.failure("Failed to load parking data. Using empty parking lot."));
                    listener.onStatusUpdate("Initialized empty parking lot");
                    return;
                }

                lot.setSlot(slot.getNumber(), slot);
            }

            listener.onLoadDataResult(Result.success("Loaded parking data successfully"));
            listener.onStatusUpdate("Parking data loaded");
        } catch (Exception exception) {
            Logger.error("Failed to load parking data", exception);
            listener.onLoadDataResult(Result.failure("Failed to load parking data. Using empty parking lot."));
            listener.onStatusUpdate("Initialized empty parking lot");
        }
    }

    public void parkCar(String licensePlate) {
        try {
            String normalizedPlate = Validator.normalizePlate(licensePlate);

            if (!Validator.isValidPlate(normalizedPlate)) {
                listener.onParkResult(Result.failure("Invalid license plate: " + safePlateText(licensePlate)));
                return;
            }

            if (lot.containsPlate(normalizedPlate)) {
                listener.onParkResult(Result.failure("Car with license plate " + normalizedPlate + " is already parked"));
                return;
            }

            Optional<ParkingSlot> availableSlot = lot.findFirstAvailableSlot();

            if (availableSlot.isEmpty()) {
                listener.onParkResult(Result.failure("No available slots for " + normalizedPlate));
                return;
            }

            ParkingSlot slot = availableSlot.get();
            slot.park(new Car(normalizedPlate));

            if (!FileHelper.saveParkingLotFile(lot.getSlots())) {
                slot.unpark();
                listener.onParkResult(Result.failure("Failed to save parking data. Car was not parked."));
                return;
            }

            listener.onParkResult(Result.found(slot, "Car " + normalizedPlate + " parked in slot " + slot.getNumber()));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Logger.warn("Parking failed: " + exception.getMessage());
            listener.onParkResult(Result.failure(exception.getMessage()));
        } catch (Exception exception) {
            Logger.error("Unexpected error while parking car", exception);
            listener.onParkResult(Result.failure("Parking failed: " + DEFAULT_ERROR_MESSAGE));
        }
    }

    public void unparkCar(int slotNumber) {
        try {
            Optional<ParkingSlot> optionalSlot = lot.getSlot(slotNumber);

            if (optionalSlot.isEmpty()) {
                listener.onUnparkResult(Result.failure("Invalid slot number: " + slotNumber));
                return;
            }

            ParkingSlot slot = optionalSlot.get();

            if (!slot.isOccupied()) {
                listener.onUnparkResult(Result.failure("Slot " + slotNumber + " is already empty"));
                return;
            }

            String licensePlate = slot.getCar().getLicensePlate();
            Car removedCar = slot.getCar();

            slot.unpark();

            if (!FileHelper.saveParkingLotFile(lot.getSlots())) {
                slot.park(removedCar);
                listener.onUnparkResult(Result.failure("Failed to save parking data. Car was not unparked."));
                return;
            }

            listener.onUnparkResult(Result.success("Car " + licensePlate + " unparked from slot " + slotNumber));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Logger.warn("Unparking failed: " + exception.getMessage());
            listener.onUnparkResult(Result.failure(exception.getMessage()));
        } catch (Exception exception) {
            Logger.error("Unexpected error while unparking car", exception);
            listener.onUnparkResult(Result.failure("Unparking failed: " + DEFAULT_ERROR_MESSAGE));
        }
    }

    public void findCarByPlate(String licensePlate) {
        try {
            String normalizedPlate = Validator.normalizePlate(licensePlate);

            if (!Validator.isValidPlate(normalizedPlate)) {
                listener.onFindCarResult(Result.notFound("Invalid license plate: " + safePlateText(licensePlate)));
                return;
            }

            Optional<ParkingSlot> foundSlot = lot.findSlotByPlate(normalizedPlate);

            if (foundSlot.isPresent()) {
                ParkingSlot slot = foundSlot.get();
                listener.onFindCarResult(Result.found(
                        slot,
                        "Car " + normalizedPlate + " found in slot " + slot.getNumber()
                ));
                return;
            }

            listener.onFindCarResult(Result.notFound("Car " + normalizedPlate + " not found"));
        } catch (Exception exception) {
            Logger.error("Unexpected error while searching for car", exception);
            listener.onFindCarResult(Result.notFound("Search failed: " + DEFAULT_ERROR_MESSAGE));
        }
    }

    public void batchUnpark(List<Integer> slotNumbers) {
        try {
            if (slotNumbers == null || slotNumbers.isEmpty()) {
                listener.onBatchUnparkResult(Result.batchUnparked(0, "No slots selected for batch unpark"));
                return;
            }

            List<ParkingSlot> changedSlots = new ArrayList<>();
            List<Car> removedCars = new ArrayList<>();
            List<String> unparkedPlates = new ArrayList<>();

            for (Integer slotNumber : slotNumbers) {
                if (slotNumber == null) {
                    Logger.warn("Skipped null slot number during batch unpark");
                    continue;
                }

                Optional<ParkingSlot> optionalSlot = lot.getSlot(slotNumber);

                if (optionalSlot.isEmpty()) {
                    Logger.warn("Skipped invalid slot number during batch unpark: " + slotNumber);
                    continue;
                }

                ParkingSlot slot = optionalSlot.get();

                if (!slot.isOccupied()) {
                    continue;
                }

                Car removedCar = slot.getCar();
                unparkedPlates.add(removedCar.getLicensePlate());
                changedSlots.add(slot);
                removedCars.add(removedCar);
                slot.unpark();
            }

            if (changedSlots.isEmpty()) {
                listener.onBatchUnparkResult(Result.batchUnparked(0, "No occupied slots selected"));
                return;
            }

            if (!FileHelper.saveParkingLotFile(lot.getSlots())) {
                restoreBatchUnparkedSlots(changedSlots, removedCars);
                listener.onBatchUnparkResult(Result.batchUnparked(
                        0,
                        "Failed to save parking data. Batch unpark was not completed."
                ));
                return;
            }

            listener.onBatchUnparkResult(Result.batchUnparked(
                    changedSlots.size(),
                    "Unparked " + changedSlots.size() + " cars: " + String.join(", ", unparkedPlates)
            ));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Logger.warn("Batch unpark failed: " + exception.getMessage());
            listener.onBatchUnparkResult(Result.batchUnparked(0, exception.getMessage()));
        } catch (Exception exception) {
            Logger.error("Unexpected error during batch unpark", exception);
            listener.onBatchUnparkResult(Result.batchUnparked(0, "Batch unpark failed: " + DEFAULT_ERROR_MESSAGE));
        }
    }

    public void generateReport() {
        try {
            boolean success = FileHelper.generateReport(lot.getSlots());

            if (success) {
                listener.onReportResult(Result.success("Report generated successfully at data/parking_lot_report.csv"));
            } else {
                listener.onReportResult(Result.failure("Failed to generate report. Check file permissions or disk space."));
            }
        } catch (Exception exception) {
            Logger.error("Unexpected error while generating report", exception);
            listener.onReportResult(Result.failure("Report generation failed: " + DEFAULT_ERROR_MESSAGE));
        }
    }

    private void restoreBatchUnparkedSlots(List<ParkingSlot> changedSlots, List<Car> removedCars) {
        int count = Math.min(changedSlots.size(), removedCars.size());

        for (int index = 0; index < count; index++) {
            ParkingSlot slot = changedSlots.get(index);
            Car car = removedCars.get(index);

            try {
                if (!slot.isOccupied()) {
                    slot.park(car);
                }
            } catch (Exception exception) {
                Logger.error("Failed to restore slot " + slot.getNumber() + " after batch unpark failure", exception);
            }
        }
    }

    private String safePlateText(String plate) {
        String normalizedPlate = Validator.normalizePlate(plate);

        if (normalizedPlate.isEmpty()) {
            return "empty input";
        }

        return normalizedPlate;
    }
}