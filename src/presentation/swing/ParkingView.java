package presentation.swing;

import application.dto.ParkingLotViewData;
import infrastructure.logging.AppLogger;
import presentation.swing.dialogs.MessageBox;
import presentation.swing.panels.BatchPanel;
import presentation.swing.panels.HelpPanel;
import presentation.swing.panels.ParkPanel;
import presentation.swing.panels.SearchPanel;
import presentation.swing.panels.SlotPanel;
import presentation.swing.resources.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;

public final class ParkingView extends JFrame {
    private static final String READY_STATUS = "Ready";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 700;
    private static final int STATUS_CLEAR_DELAY_MILLISECONDS = 5000;
    private static final int PANEL_SPACING = 10;

    private final ParkingViewController controller;
    private final IconUtil iconUtil;
    private final MessageBox messageBox;
    private final AppLogger logger;
    private final Timer statusTimer;

    private JLabel statusBar;
    private SlotPanel slotPanel;
    private ParkPanel parkPanel;

    public ParkingView(
            String appTitle,
            ParkingViewController controller,
            IconUtil iconUtil,
            MessageBox messageBox,
            AppLogger logger,
            int parkingLotSize
    ) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        if (messageBox == null) {
            throw new IllegalArgumentException("Message box cannot be null");
        }

        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        if (parkingLotSize <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }

        this.controller = controller;
        this.iconUtil = iconUtil;
        this.messageBox = messageBox;
        this.logger = logger;
        this.statusTimer = new Timer(STATUS_CLEAR_DELAY_MILLISECONDS, event -> setStatus(READY_STATUS));
        this.statusTimer.setRepeats(false);

        initializeWindow(appTitle);
        initializeUi(parkingLotSize);
    }

    public void displayParkingLot(ParkingLotViewData parkingLotViewData) {
        if (slotPanel != null) {
            slotPanel.displayParkingLot(parkingLotViewData);
        }
    }

    public void highlightSlot(int slotNumber) {
        if (slotPanel != null) {
            slotPanel.highlightSlot(slotNumber);
        }
    }

    public void showInfo(String message) {
        messageBox.showInfo(message);
        setStatus(message);
        clearStatusLater();
    }

    public void showError(String message, String... recoverySteps) {
        messageBox.showError(message, recoverySteps);
        setStatus(message);
        clearStatusLater();
    }

    public void setStatus(String message) {
        if (statusBar != null) {
            statusBar.setText(normalize(message, READY_STATUS));
        }
    }

    public void focusParkInput() {
        if (parkPanel != null && parkPanel.getPlateInput() != null) {
            parkPanel.getPlateInput().requestFocusInWindow();
        }
    }

    private void initializeWindow(String appTitle) {
        setTitle(normalize(appTitle, "Car Parking System"));
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        try {
            if (iconUtil.createCarIcon(32, 32) != null) {
                setIconImage(iconUtil.createCarIcon(32, 32).getImage());
            }
        } catch (Exception exception) {
            logger.warn("Failed to set application icon: " + exception.getMessage());
        }
    }

    private void initializeUi(int parkingLotSize) {
        statusBar = new JLabel(READY_STATUS, SwingConstants.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        slotPanel = new SlotPanel(controller, iconUtil, parkingLotSize);
        parkPanel = new ParkPanel(controller, iconUtil);
        SearchPanel searchPanel = new SearchPanel(controller, iconUtil);
        BatchPanel batchPanel = new BatchPanel(controller, slotPanel, iconUtil);
        HelpPanel helpPanel = new HelpPanel(iconUtil, messageBox, logger);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
    }

    private void clearStatusLater() {
        statusTimer.restart();
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }
}