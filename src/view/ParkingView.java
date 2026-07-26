package view;

import controller.ParkingController;
import controller.ParkingListener;
import controller.Result;
import model.ParkingLot;
import util.IconUtil;
import util.Logger;
import util.MessageBox;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

public final class ParkingView extends JFrame implements ParkingListener {
    private static final String APP_TITLE = "Car Parking System";
    private static final String READY_STATUS = "Ready";
    private static final String GITHUB_REPOSITORY_URL = "https://github.com/mugabiBenjamin/CarParking";

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 700;
    private static final int PARKING_LOT_SIZE = 10;
    private static final int STATUS_CLEAR_DELAY_MILLISECONDS = 5000;
    private static final int PANEL_SPACING = 10;

    private final ParkingLot lot;
    private final ParkingController controller;
    private final Timer statusBarTimer;

    private JLabel statusBar;
    private SlotPanel slotPanel;
    private ParkPanel parkPanel;
    private SearchPanel searchPanel;
    private BatchPanel batchPanel;
    private HelpPanel helpPanel;

    public ParkingView() {
        this.lot = new ParkingLot(PARKING_LOT_SIZE);
        this.controller = new ParkingController(lot, this);
        this.statusBarTimer = new Timer(STATUS_CLEAR_DELAY_MILLISECONDS, event -> setStatus(READY_STATUS));
        this.statusBarTimer.setRepeats(false);

        initializeWindow();
        initializeMenu();
        initializeUI();
        loadApplicationData();
        setVisible(true);
        requestInitialFocus();
    }

    private void initializeWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception exception) {
            Logger.warn("Failed to set cross-platform look and feel: " + exception.getMessage());
        }

        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        try {
            setIconImage(IconUtil.createCarIcon(32, 32).getImage());
        } catch (Exception exception) {
            Logger.warn("Failed to set application icon: " + exception.getMessage());
        }
    }

    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu helpMenu = new JMenu("Online Help");
        helpMenu.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JMenuItem visitGithubItem = new JMenuItem("Visit GitHub Repository");
        visitGithubItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        visitGithubItem.setToolTipText("Open the GitHub repository in your browser");
        visitGithubItem.addActionListener(event -> openGitHubRepository());

        helpMenu.add(visitGithubItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initializeUI() {
        statusBar = new JLabel(READY_STATUS, SwingConstants.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusBar.setToolTipText("Shows parking and search status");

        slotPanel = new SlotPanel(controller, lot, statusBar);
        parkPanel = new ParkPanel(controller, statusBar);
        searchPanel = new SearchPanel(controller, slotPanel, statusBar);
        batchPanel = new BatchPanel(controller, slotPanel, statusBar);
        helpPanel = new HelpPanel(statusBar);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setFocusable(true);

        controlPanel.add(parkPanel);
        controlPanel.add(Box.createVerticalStrut(PANEL_SPACING));
        controlPanel.add(searchPanel);
        controlPanel.add(Box.createVerticalStrut(PANEL_SPACING));
        controlPanel.add(batchPanel);
        controlPanel.add(Box.createVerticalStrut(PANEL_SPACING));
        controlPanel.add(helpPanel);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(slotPanel), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        slotPanel.updateSlots();
    }

    private void loadApplicationData() {
        try {
            controller.loadParkingData();
        } catch (Exception exception) {
            Logger.error("Failed to load application data", exception);
            setStatus("Failed to load parking data");
            MessageBox.showError(
                    "The application could not load parking data.",
                    "Check that the data directory is accessible.",
                    "Restart the application and try again."
            );
        }
    }

    private void requestInitialFocus() {
        try {
            if (parkPanel != null && parkPanel.getPlateInput() != null) {
                parkPanel.getPlateInput().requestFocusInWindow();
            }
        } catch (Exception exception) {
            Logger.warn("Failed to request initial focus: " + exception.getMessage());
        }
    }

    private void openGitHubRepository() {
        try {
            URI repositoryUri = URI.create(GITHUB_REPOSITORY_URL);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(repositoryUri);
                setStatus("Opened GitHub repository");
                clearStatusLater();
                return;
            }

            copyToClipboard(GITHUB_REPOSITORY_URL);
            MessageBox.showInfo("Browser opening is not supported. Repository URL copied to clipboard.");
            setStatus("Repository URL copied to clipboard");
            clearStatusLater();
        } catch (Exception exception) {
            Logger.error("Failed to open GitHub repository", exception);
            copyToClipboard(GITHUB_REPOSITORY_URL);
            MessageBox.showError(
                    "Unable to open the GitHub repository.",
                    "The repository URL has been copied to the clipboard if possible.",
                    "Paste it into your browser manually."
            );
            setStatus("Failed to open GitHub repository");
            clearStatusLater();
        }
    }

    private void copyToClipboard(String value) {
        try {
            StringSelection selection = new StringSelection(value);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (Exception exception) {
            Logger.warn("Failed to copy text to clipboard: " + exception.getMessage());
        }
    }

    @Override
    public void onParkResult(Result result) {
        Result safeResult = normalizeResult(result, "Parking operation completed");

        if (safeResult.isSuccess()) {
            updateSlots();
            MessageBox.showInfo(safeResult.getMessage());
        } else {
            MessageBox.showError(
                    safeResult.getMessage(),
                    "Check the license plate and try again.",
                    "Confirm that an empty parking slot is available."
            );
        }

        setStatus(safeResult.getMessage());
        clearStatusLater();
    }

    @Override
    public void onUnparkResult(Result result) {
        Result safeResult = normalizeResult(result, "Unparking operation completed");

        if (safeResult.isSuccess()) {
            updateSlots();
            MessageBox.showInfo(safeResult.getMessage());
        } else {
            MessageBox.showError(
                    safeResult.getMessage(),
                    "Choose an occupied slot and try again.",
                    "Refresh the parking slots if the display looks outdated."
            );
        }

        setStatus(safeResult.getMessage());
        clearStatusLater();
    }

    @Override
    public void onBatchUnparkResult(Result result) {
        Result safeResult = normalizeResult(result, "Batch unpark operation completed");

        if (safeResult.isSuccess()) {
            updateSlots();
            MessageBox.showInfo(safeResult.getMessage());
        } else {
            MessageBox.showError(
                    safeResult.getMessage(),
                    "Select at least one occupied slot.",
                    "Try the batch unpark operation again."
            );
        }

        setStatus(safeResult.getMessage());
        clearStatusLater();
    }

    @Override
    public void onFindCarResult(Result result) {
        Result safeResult = normalizeResult(result, "Search operation completed");

        if (safeResult.isSuccess()) {
            safeResult.findSlot().ifPresent(slot -> slotPanel.highlightSlot(slot.getNumber()));
            MessageBox.showInfo(safeResult.getMessage());
        } else {
            MessageBox.showError(
                    safeResult.getMessage(),
                    "Check that the license plate is correct.",
                    "Confirm that the car is currently parked."
            );
            setStatus(safeResult.getMessage());
            clearStatusLater();
        }
    }

    @Override
    public void onReportResult(Result result) {
        Result safeResult = normalizeResult(result, "Report operation completed");

        if (safeResult.isSuccess()) {
            MessageBox.showInfo(safeResult.getMessage());
        } else {
            MessageBox.showError(
                    safeResult.getMessage(),
                    "Check file permissions for the data directory.",
                    "Close any open report file and try again."
            );
        }

        setStatus(safeResult.getMessage());
        clearStatusLater();
    }

    @Override
    public void onLoadDataResult(Result result) {
        Result safeResult = normalizeResult(result, "Load data operation completed");

        if (!safeResult.isSuccess()) {
            MessageBox.showError(
                    safeResult.getMessage(),
                    "Check the parking data file format.",
                    "The application may initialize an empty parking lot."
            );
        }

        updateSlots();
        setStatus(safeResult.getMessage());
        clearStatusLater();
    }

    @Override
    public void onStatusUpdate(String message) {
        setStatus(normalizeMessage(message, READY_STATUS));
        clearStatusLater();
    }

    private Result normalizeResult(Result result, String fallbackMessage) {
        if (result == null) {
            Logger.warn("Received null result in ParkingView");
            return Result.failure(fallbackMessage);
        }

        return result;
    }

    private void updateSlots() {
        try {
            if (slotPanel != null) {
                slotPanel.updateSlots();
            }
        } catch (Exception exception) {
            Logger.error("Failed to update slot panel", exception);
            setStatus("Failed to update parking slots");
        }
    }

    private void setStatus(String message) {
        if (statusBar != null) {
            statusBar.setText(normalizeMessage(message, READY_STATUS));
        }
    }

    private void clearStatusLater() {
        if (statusBarTimer != null) {
            statusBarTimer.restart();
        }
    }

    private String normalizeMessage(String message, String fallback) {
        if (message == null || message.trim().isEmpty()) {
            return fallback;
        }

        return message.trim();
    }
}