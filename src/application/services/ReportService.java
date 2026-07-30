package application.services;

import application.dto.GenerateReportResponse;
import application.dto.OperationResult;
import application.repositories.ParkingLotRepository;
import application.repositories.ReportRepository;
import domain.entities.ParkingLot;

public final class ReportService {
    private final ParkingLotRepository parkingLotRepository;
    private final ReportRepository reportRepository;
    private final int parkingLotSize;

    public ReportService(
            ParkingLotRepository parkingLotRepository,
            ReportRepository reportRepository,
            int parkingLotSize
    ) {
        if (parkingLotRepository == null) {
            throw new IllegalArgumentException("Parking lot repository cannot be null");
        }

        if (reportRepository == null) {
            throw new IllegalArgumentException("Report repository cannot be null");
        }

        if (parkingLotSize <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }

        this.parkingLotRepository = parkingLotRepository;
        this.reportRepository = reportRepository;
        this.parkingLotSize = parkingLotSize;
    }

    public OperationResult<GenerateReportResponse> generateReport() {
        try {
            ParkingLot parkingLot = parkingLotRepository.load(parkingLotSize);
            String filePath = reportRepository.generate(parkingLot);

            if (filePath == null || filePath.trim().isEmpty()) {
                return OperationResult.failure(
                        "REPORT_GENERATION_FAILED",
                        "Report generation failed",
                        "Check file permissions and try again."
                );
            }

            return OperationResult.success(
                    "Report generated successfully at " + filePath,
                    new GenerateReportResponse(filePath)
            );
        } catch (Exception exception) {
            return OperationResult.failure(
                    "REPORT_GENERATION_ERROR",
                    "Report generation failed because an unexpected error occurred",
                    "Close any open report file and try again."
            );
        }
    }
}