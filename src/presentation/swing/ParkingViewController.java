package presentation.swing;

import application.dto.BatchUnparkRequest;
import application.dto.BatchUnparkResponse;
import application.dto.ErrorResponse;
import application.dto.FindCarRequest;
import application.dto.FindCarResponse;
import application.dto.GenerateReportResponse;
import application.dto.LoadParkingDataResponse;
import application.dto.OperationResult;
import application.dto.ParkCarRequest;
import application.dto.ParkCarResponse;
import application.dto.UnparkCarRequest;
import application.dto.UnparkCarResponse;
import application.services.PlateValidationService;
import application.usecases.BatchUnparkUseCase;
import application.usecases.FindCarUseCase;
import application.usecases.GenerateReportUseCase;
import application.usecases.LoadParkingDataUseCase;
import application.usecases.ParkCarUseCase;
import application.usecases.UnparkCarUseCase;
import infrastructure.logging.AppLogger;

import java.util.List;

public final class ParkingViewController {
    private final LoadParkingDataUseCase loadParkingDataUseCase;
    private final ParkCarUseCase parkCarUseCase;
    private final UnparkCarUseCase unparkCarUseCase;
    private final BatchUnparkUseCase batchUnparkUseCase;
    private final FindCarUseCase findCarUseCase;
    private final GenerateReportUseCase generateReportUseCase;
    private final PlateValidationService plateValidationService;
    private final AppLogger logger;

    private ParkingView view;

    public ParkingViewController(
            LoadParkingDataUseCase loadParkingDataUseCase,
            ParkCarUseCase parkCarUseCase,
            UnparkCarUseCase unparkCarUseCase,
            BatchUnparkUseCase batchUnparkUseCase,
            FindCarUseCase findCarUseCase,
            GenerateReportUseCase generateReportUseCase,
            PlateValidationService plateValidationService,
            AppLogger logger
    ) {
        if (loadParkingDataUseCase == null) {
            throw new IllegalArgumentException("Load parking data use case cannot be null");
        }

        if (parkCarUseCase == null) {
            throw new IllegalArgumentException("Park car use case cannot be null");
        }

        if (unparkCarUseCase == null) {
            throw new IllegalArgumentException("Unpark car use case cannot be null");
        }

        if (batchUnparkUseCase == null) {
            throw new IllegalArgumentException("Batch unpark use case cannot be null");
        }

        if (findCarUseCase == null) {
            throw new IllegalArgumentException("Find car use case cannot be null");
        }

        if (generateReportUseCase == null) {
            throw new IllegalArgumentException("Generate report use case cannot be null");
        }

        if (plateValidationService == null) {
            throw new IllegalArgumentException("Plate validation service cannot be null");
        }

        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.loadParkingDataUseCase = loadParkingDataUseCase;
        this.parkCarUseCase = parkCarUseCase;
        this.unparkCarUseCase = unparkCarUseCase;
        this.batchUnparkUseCase = batchUnparkUseCase;
        this.findCarUseCase = findCarUseCase;
        this.generateReportUseCase = generateReportUseCase;
        this.plateValidationService = plateValidationService;
        this.logger = logger;
    }

    public void attachView(ParkingView view) {
        if (view == null) {
            throw new IllegalArgumentException("Parking view cannot be null");
        }

        this.view = view;
    }

    public OperationResult<?> validateLicensePlate(String licensePlate) {
        return plateValidationService.validate(licensePlate);
    }

    public void loadParkingData() {
        if (!isViewReady("load parking data")) {
            return;
        }

        try {
            OperationResult<LoadParkingDataResponse> result = loadParkingDataUseCase.execute();

            if (result.isSuccess()) {
                LoadParkingDataResponse response = result.getData();

                if (response != null && response.getParkingLot() != null) {
                    view.displayParkingLot(response.getParkingLot());
                }

                view.setStatus(result.getMessage());
            } else {
                showOperationError(result);
            }
        } catch (Exception exception) {
            logger.error("Failed to load parking data", exception);
            view.showError(
                    "Failed to load parking data.",
                    "Check the parking data file.",
                    "Restart the application and try again."
            );
        }
    }

    public void parkCar(String licensePlate) {
        if (!isViewReady("park car")) {
            return;
        }

        try {
            OperationResult<ParkCarResponse> result = parkCarUseCase.execute(new ParkCarRequest(licensePlate));

            if (result.isSuccess()) {
                ParkCarResponse response = result.getData();

                if (response != null && response.getParkingLot() != null) {
                    view.displayParkingLot(response.getParkingLot());
                }

                view.showInfo(result.getMessage());
            } else {
                showOperationError(result);
            }
        } catch (Exception exception) {
            logger.error("Unexpected presentation error while parking car", exception);
            view.showError(
                    "Parking failed because an unexpected error occurred.",
                    "Check the license plate and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    public void unparkCar(int slotNumber) {
        if (!isViewReady("unpark car")) {
            return;
        }

        try {
            OperationResult<UnparkCarResponse> result = unparkCarUseCase.execute(new UnparkCarRequest(slotNumber));

            if (result.isSuccess()) {
                UnparkCarResponse response = result.getData();

                if (response != null && response.getParkingLot() != null) {
                    view.displayParkingLot(response.getParkingLot());
                }

                view.showInfo(result.getMessage());
            } else {
                showOperationError(result);
            }
        } catch (Exception exception) {
            logger.error("Unexpected presentation error while unparking car", exception);
            view.showError(
                    "Unparking failed because an unexpected error occurred.",
                    "Refresh the parking slots and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    public void batchUnpark(List<Integer> slotNumbers) {
        if (!isViewReady("batch unpark")) {
            return;
        }

        try {
            OperationResult<BatchUnparkResponse> result = batchUnparkUseCase.execute(new BatchUnparkRequest(slotNumbers));

            if (result.isSuccess()) {
                BatchUnparkResponse response = result.getData();

                if (response != null && response.getParkingLot() != null) {
                    view.displayParkingLot(response.getParkingLot());
                }

                view.showInfo(result.getMessage());
            } else {
                showOperationError(result);
            }
        } catch (Exception exception) {
            logger.error("Unexpected presentation error during batch unpark", exception);
            view.showError(
                    "Batch unpark failed because an unexpected error occurred.",
                    "Refresh the parking slots and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    public void findCar(String licensePlate) {
        if (!isViewReady("find car")) {
            return;
        }

        try {
            OperationResult<FindCarResponse> result = findCarUseCase.execute(new FindCarRequest(licensePlate));

            if (result.isSuccess()) {
                FindCarResponse response = result.getData();

                if (response != null && response.isFound()) {
                    view.highlightSlot(response.getSlotNumber());
                }

                view.showInfo(result.getMessage());
            } else {
                showOperationError(result);
            }
        } catch (Exception exception) {
            logger.error("Unexpected presentation error while searching for car", exception);
            view.showError(
                    "Search failed because an unexpected error occurred.",
                    "Check the license plate and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    public void generateReport() {
        if (!isViewReady("generate report")) {
            return;
        }

        try {
            OperationResult<GenerateReportResponse> result = generateReportUseCase.execute();

            if (result.isSuccess()) {
                view.showInfo(result.getMessage());
            } else {
                showOperationError(result);
            }
        } catch (Exception exception) {
            logger.error("Unexpected presentation error while generating report", exception);
            view.showError(
                    "Report generation failed because an unexpected error occurred.",
                    "Close any open report file and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    public void showValidationError(String message, String recoveryStep) {
        if (!isViewReady("show validation error")) {
            return;
        }

        view.showError(message, recoveryStep);
    }

    private void showOperationError(OperationResult<?> result) {
        if (result == null) {
            view.showError(
                    "Operation failed.",
                    "Try the action again.",
                    "Restart the application if the problem continues."
            );
            return;
        }

        ErrorResponse error = result.getError();

        if (error != null) {
            view.showError(error.getMessage(), error.getRecoveryStep());
            return;
        }

        view.showError(
                result.getMessage(),
                "Try the action again.",
                "Restart the application if the problem continues."
        );
    }

    private boolean isViewReady(String action) {
        if (view == null) {
            logger.warn("Cannot " + action + " because ParkingView is not attached");
            return false;
        }

        return true;
    }
}