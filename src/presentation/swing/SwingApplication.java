package presentation.swing;

import application.repositories.ParkingLotRepository;
import application.repositories.ReportRepository;
import application.services.ParkingService;
import application.services.PlateValidationService;
import application.services.ReportService;
import application.usecases.BatchUnparkUseCase;
import application.usecases.FindCarUseCase;
import application.usecases.GenerateReportUseCase;
import application.usecases.LoadParkingDataUseCase;
import application.usecases.ParkCarUseCase;
import application.usecases.UnparkCarUseCase;
import application.validators.LicensePlateFormatValidator;
import application.validators.ParkingRequestValidator;
import infrastructure.config.AppConfig;
import infrastructure.config.ConfigLoader;
import infrastructure.file.CsvReportRepository;
import infrastructure.file.FileParkingLotRepository;
import infrastructure.file.FilePaths;
import infrastructure.file.ParkingLotFileMapper;
import infrastructure.file.ReportFileMapper;
import infrastructure.logging.AppLogger;
import infrastructure.logging.ConsoleLogger;
import presentation.swing.dialogs.MessageBox;
import presentation.swing.resources.IconUtil;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class SwingApplication {
    private final AppLogger logger;

    public SwingApplication() {
        this.logger = new ConsoleLogger();
    }

    public void start() {
        if (SwingUtilities.isEventDispatchThread()) {
            startOnEventDispatchThread();
        } else {
            SwingUtilities.invokeLater(this::startOnEventDispatchThread);
        }
    }

    private void startOnEventDispatchThread() {
        try {
            configureLookAndFeel();

            ConfigLoader configLoader = new ConfigLoader(logger);
            AppConfig config = configLoader.load();

            FilePaths filePaths = new FilePaths(config);

            ParkingLotFileMapper parkingLotFileMapper = new ParkingLotFileMapper(logger);
            ReportFileMapper reportFileMapper = new ReportFileMapper();

            ParkingLotRepository parkingLotRepository = new FileParkingLotRepository(
                    filePaths,
                    parkingLotFileMapper,
                    logger
            );

            ReportRepository reportRepository = new CsvReportRepository(
                    filePaths,
                    reportFileMapper,
                    logger
            );

            LicensePlateFormatValidator licensePlateFormatValidator = new LicensePlateFormatValidator();
            ParkingRequestValidator parkingRequestValidator = new ParkingRequestValidator(licensePlateFormatValidator);

            ParkingService parkingService = new ParkingService(
                    parkingLotRepository,
                    parkingRequestValidator,
                    config.getParkingLotSize()
            );

            ReportService reportService = new ReportService(
                    parkingLotRepository,
                    reportRepository,
                    config.getParkingLotSize()
            );

            PlateValidationService plateValidationService = new PlateValidationService(licensePlateFormatValidator);

            ParkingViewController controller = new ParkingViewController(
                    new LoadParkingDataUseCase(parkingService),
                    new ParkCarUseCase(parkingService),
                    new UnparkCarUseCase(parkingService),
                    new BatchUnparkUseCase(parkingService),
                    new FindCarUseCase(parkingService),
                    new GenerateReportUseCase(reportService),
                    plateValidationService,
                    logger
            );

            IconUtil iconUtil = new IconUtil(logger);
            MessageBox messageBox = new MessageBox(logger);

            ParkingView view = new ParkingView(
                    config.getAppTitle(),
                    controller,
                    iconUtil,
                    messageBox,
                    logger,
                    config.getParkingLotSize()
            );

            controller.attachView(view);
            view.setVisible(true);
            view.focusParkInput();
            controller.loadParkingData();
        } catch (Exception exception) {
            logger.error("Failed to start Swing application", exception);

            MessageBox messageBox = new MessageBox(logger);
            messageBox.showError(
                    "The application failed to start.",
                    "Check file permissions for the application directory.",
                    "Restart the application and try again."
            );
        }
    }

    private void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception exception) {
            logger.warn("Failed to apply cross-platform look and feel: " + exception.getMessage());
        }
    }
}