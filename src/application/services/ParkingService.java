package application.services;

import application.dto.BatchUnparkRequest;
import application.dto.BatchUnparkResponse;
import application.dto.FindCarRequest;
import application.dto.FindCarResponse;
import application.dto.LoadParkingDataResponse;
import application.dto.OperationResult;
import application.dto.ParkCarRequest;
import application.dto.ParkCarResponse;
import application.dto.ParkingLotViewData;
import application.dto.SlotViewData;
import application.dto.UnparkCarRequest;
import application.dto.UnparkCarResponse;
import application.repositories.ParkingLotRepository;
import application.repositories.ParkingLotUpdateResult;
import application.validators.ParkingRequestValidator;
import domain.entities.Car;
import domain.entities.ParkingLot;
import domain.entities.ParkingSlot;
import domain.exceptions.DomainException;
import domain.valueobjects.LicensePlate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ParkingService {
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingRequestValidator requestValidator;
    private final int parkingLotSize;

    public ParkingService(
            ParkingLotRepository parkingLotRepository,
            ParkingRequestValidator requestValidator,
            int parkingLotSize
    ) {
        if (parkingLotRepository == null) {
            throw new IllegalArgumentException("Parking lot repository cannot be null");
        }

        if (requestValidator == null) {
            throw new IllegalArgumentException("Parking request validator cannot be null");
        }

        if (parkingLotSize <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }

        this.parkingLotRepository = parkingLotRepository;
        this.requestValidator = requestValidator;
        this.parkingLotSize = parkingLotSize;
    }

    public OperationResult<LoadParkingDataResponse> loadParkingData() {
        try {
            ParkingLot parkingLot = parkingLotRepository.load(parkingLotSize);
            ParkingLotViewData viewData = toParkingLotViewData(parkingLot);

            return OperationResult.success(
                    "Parking data loaded successfully",
                    new LoadParkingDataResponse(viewData)
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "LOAD_PARKING_DATA_FAILED",
                    "Failed to load parking data",
                    "Check the parking data file and try again."
            );
        }
    }

    public OperationResult<ParkCarResponse> parkCar(ParkCarRequest request) {
        OperationResult<Void> validationResult = requestValidator.validateParkRequest(request);

        if (validationResult.isFailure()) {
            return OperationResult.failure(
                    "PARK_REQUEST_INVALID",
                    validationResult.getMessage(),
                    "Correct the license plate and try again."
            );
        }

        try {
            ParkingLotUpdateResult<ParkCarResponse> updateResult = parkingLotRepository.update(
                    parkingLotSize,
                    parkingLot -> {
                        Car car = new Car(request.getLicensePlate());
                        ParkingSlot slot = parkingLot.park(car);
                        ParkingLotViewData viewData = toParkingLotViewData(parkingLot);

                        return new ParkCarResponse(
                                slot.getNumber(),
                                car.getLicensePlate(),
                                viewData
                        );
                    }
            );

            if (updateResult.isCommitted()) {
                ParkCarResponse response = updateResult.getData();

                return OperationResult.success(
                        "Car " + response.getLicensePlate() + " parked in slot " + response.getSlotNumber(),
                        response
                );
            }

            return parkingCommitFailure(
                    updateResult,
                    "PARKING_COMMIT_CONFLICT",
                    "Parking could not be completed because the parking lot changed. Refresh and try again.",
                    "Parking failed because the update could not be committed."
            );
        } catch (DomainException exception) {
            return OperationResult.failure(
                    "PARKING_DOMAIN_ERROR",
                    exception.getMessage(),
                    "Check the parking lot state and try again."
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "PARKING_FAILED",
                    "Parking failed because an unexpected error occurred",
                    "Try again. If the issue continues, restart the application."
            );
        }
    }

    public OperationResult<UnparkCarResponse> unparkCar(UnparkCarRequest request) {
        OperationResult<Void> validationResult = requestValidator.validateUnparkRequest(request);

        if (validationResult.isFailure()) {
            return OperationResult.failure(
                    "UNPARK_REQUEST_INVALID",
                    validationResult.getMessage(),
                    "Choose a valid occupied slot and try again."
            );
        }

        try {
            ParkingLotUpdateResult<UnparkCarResponse> updateResult = parkingLotRepository.update(
                    parkingLotSize,
                    parkingLot -> {
                        Car removedCar = parkingLot.unpark(request.getSlotNumber());
                        ParkingLotViewData viewData = toParkingLotViewData(parkingLot);

                        return new UnparkCarResponse(
                                request.getSlotNumber(),
                                removedCar.getLicensePlate(),
                                viewData
                        );
                    }
            );

            if (updateResult.isCommitted()) {
                UnparkCarResponse response = updateResult.getData();

                return OperationResult.success(
                        "Car " + response.getLicensePlate() + " unparked from slot " + response.getSlotNumber(),
                        response
                );
            }

            return parkingCommitFailure(
                    updateResult,
                    "UNPARK_COMMIT_CONFLICT",
                    "Unparking could not be completed because the parking lot changed. Refresh and try again.",
                    "Unparking failed because the update could not be committed."
            );
        } catch (DomainException exception) {
            return OperationResult.failure(
                    "UNPARK_DOMAIN_ERROR",
                    exception.getMessage(),
                    "Choose an occupied slot and try again."
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "UNPARK_FAILED",
                    "Unparking failed because an unexpected error occurred",
                    "Try again. If the issue continues, restart the application."
            );
        }
    }

    public OperationResult<BatchUnparkResponse> batchUnpark(BatchUnparkRequest request) {
        OperationResult<Void> validationResult = requestValidator.validateBatchUnparkRequest(request);

        if (validationResult.isFailure()) {
            return OperationResult.failure(
                    "BATCH_UNPARK_REQUEST_INVALID",
                    validationResult.getMessage(),
                    "Select occupied slots and try again."
            );
        }

        try {
            ParkingLotUpdateResult<BatchUnparkResponse> updateResult = parkingLotRepository.update(
                    parkingLotSize,
                    parkingLot -> {
                        List<String> unparkedPlates = new ArrayList<>();

                        for (Integer slotNumber : request.getSlotNumbers()) {
                            Optional<ParkingSlot> optionalSlot = parkingLot.getSlot(slotNumber);

                            if (optionalSlot.isEmpty() || optionalSlot.get().isEmpty()) {
                                continue;
                            }

                            Car removedCar = parkingLot.unpark(slotNumber);
                            unparkedPlates.add(removedCar.getLicensePlate());
                        }

                        if (unparkedPlates.isEmpty()) {
                            throw new IllegalStateException("No occupied slots were selected");
                        }

                        ParkingLotViewData viewData = toParkingLotViewData(parkingLot);

                        return new BatchUnparkResponse(
                                unparkedPlates.size(),
                                unparkedPlates,
                                viewData
                        );
                    }
            );

            if (updateResult.isCommitted()) {
                BatchUnparkResponse response = updateResult.getData();

                return OperationResult.success(
                        "Unparked " + response.getUnparkedCount() + " car(s)",
                        response
                );
            }

            return parkingCommitFailure(
                    updateResult,
                    "BATCH_UNPARK_COMMIT_CONFLICT",
                    "Batch unpark could not be completed because the parking lot changed. Refresh and try again.",
                    "Batch unpark failed because the update could not be committed."
            );
        } catch (IllegalStateException exception) {
            return OperationResult.failure(
                    "NO_OCCUPIED_SLOTS_SELECTED",
                    exception.getMessage(),
                    "Select at least one occupied slot and try again."
            );
        } catch (DomainException exception) {
            return OperationResult.failure(
                    "BATCH_UNPARK_DOMAIN_ERROR",
                    exception.getMessage(),
                    "Refresh the parking slots and try again."
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "BATCH_UNPARK_FAILED",
                    "Batch unpark failed because an unexpected error occurred",
                    "Try again. If the issue continues, restart the application."
            );
        }
    }

    public OperationResult<FindCarResponse> findCar(FindCarRequest request) {
        if (request == null || request.getLicensePlate().isEmpty()) {
            return OperationResult.failure(
                    "INVALID_SEARCH_REQUEST",
                    "License plate is required",
                    "Enter a valid license plate and try again."
            );
        }

        try {
            ParkingLot parkingLot = parkingLotRepository.load(parkingLotSize);
            LicensePlate licensePlate = LicensePlate.of(request.getLicensePlate());
            Optional<ParkingSlot> foundSlot = parkingLot.findSlotByPlate(licensePlate);

            if (foundSlot.isEmpty()) {
                return OperationResult.failure(
                        "CAR_NOT_FOUND",
                        "Car " + licensePlate.getValue() + " was not found",
                        "Confirm that the car is currently parked."
                );
            }

            ParkingSlot slot = foundSlot.get();

            return OperationResult.success(
                    "Car " + licensePlate.getValue() + " found in slot " + slot.getNumber(),
                    new FindCarResponse(true, slot.getNumber(), licensePlate.getValue())
            );
        } catch (DomainException exception) {
            return OperationResult.failure(
                    "FIND_CAR_DOMAIN_ERROR",
                    exception.getMessage(),
                    "Check the license plate and try again."
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "FIND_CAR_FAILED",
                    "Search failed because an unexpected error occurred",
                    "Try again. If the issue continues, restart the application."
            );
        }
    }

    private <T> OperationResult<T> parkingCommitFailure(
            ParkingLotUpdateResult<?> updateResult,
            String conflictCode,
            String conflictRecoveryStep,
            String fallbackMessage
    ) {
        if (updateResult != null && updateResult.isConflict()) {
            return OperationResult.failure(
                    conflictCode,
                    updateResult.getMessage(),
                    conflictRecoveryStep
            );
        }

        return OperationResult.failure(
                "PARKING_UPDATE_FAILED",
                updateResult == null ? fallbackMessage : updateResult.getMessage(),
                "Try again. If the issue continues, restart the application."
        );
    }

    private ParkingLotViewData toParkingLotViewData(ParkingLot parkingLot) {
        if (parkingLot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        List<SlotViewData> slotViewData = new ArrayList<>();

        for (ParkingSlot slot : parkingLot.getSlots()) {
            String licensePlate = slot.isOccupied() ? slot.getCar().getLicensePlate() : "";

            slotViewData.add(new SlotViewData(
                    slot.getNumber(),
                    slot.getStatus(),
                    licensePlate
            ));
        }

        return new ParkingLotViewData(
                parkingLot.getSize(),
                parkingLot.getOccupiedSlotCount(),
                parkingLot.getAvailableSlotCount(),
                slotViewData
        );
    }
}