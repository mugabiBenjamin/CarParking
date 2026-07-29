package application.usecases;

import application.dto.GenerateReportResponse;
import application.dto.OperationResult;
import application.services.ReportService;

public final class GenerateReportUseCase {
    private final ReportService reportService;

    public GenerateReportUseCase(ReportService reportService) {
        if (reportService == null) {
            throw new IllegalArgumentException("Report service cannot be null");
        }

        this.reportService = reportService;
    }

    public OperationResult<GenerateReportResponse> execute() {
        return reportService.generateReport();
    }
}