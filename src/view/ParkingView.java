package view;

import controller.ParkingController;
import controller.ParkingListener;
import controller.Result;
import model.ParkingLot;
import util.Logger;
import util.MessageBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ParkingView extends JFrame implements ParkingListener {
    private final ParkingController controller;
    private final ParkingLot lot;
    private JLabel statusBar;
    private SlotPanel slotPanel;
    private ParkPanel parkPanel;
    private SearchPanel searchPanel;
    private BatchPanel batchPanel;
    private final Timer statusBarTimer;

    public ParkingView() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            Logger.log("Failed to set cross-platform look and feel: " + e.getMessage());
        }

        setTitle("Car Parking System");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Online Help");
        helpMenu.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JMenuItem visitGithubItem = new JMenuItem("Visit GitHub Repository");
        visitGithubItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        visitGithubItem.setToolTipText("Open the GitHub repository in your browser for online help");
        visitGithubItem.addActionListener(e -> openGitHubRepository());
        helpMenu.add(visitGithubItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        try {
            InputStream iconStream = getClass().getResourceAsStream("/resources/icons/car.png");
            if (iconStream != null) {
                BufferedImage icon = ImageIO.read(iconStream);
                setIconImage(icon);
            } else {
                System.err.println("Icon resource not found: /resources/icons/car.png");
            }
        } catch (IOException e) {
            System.err.println("Failed to load window icon: " + e.getMessage());
        }

        this.lot = new ParkingLot(10);
        this.controller = new ParkingController(lot, this);
        this.statusBarTimer = new Timer(5000, e -> statusBar.setText("Ready"));
        this.statusBarTimer.setRepeats(false);

        initUI();
        if (parkPanel != null && parkPanel.getPlateInput() != null) {
            boolean focused = parkPanel.getPlateInput().requestFocusInWindow();
            Logger.log("ParkingView: Requested focus for ParkPanel plateInput, success=" + focused);
        }
        controller.loadParkingData();
        setVisible(true);
    }

    private void initUI() {
        statusBar = new JLabel("Ready", SwingConstants.CENTER);
        statusBar.setForeground(Color.BLACK);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusBar.setToolTipText("Shows parking and search status, clears after 5 seconds");

        slotPanel = new SlotPanel(controller, lot, statusBar);

        parkPanel = new ParkPanel(controller, statusBar);
        searchPanel = new SearchPanel(controller, slotPanel, statusBar);
        batchPanel = new BatchPanel(controller, slotPanel, statusBar);
        HelpPanel helpPanel = new HelpPanel(statusBar);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setFocusable(true);
        controlPanel.add(parkPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(searchPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(batchPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(helpPanel);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(slotPanel), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        slotPanel.updateSlots();
    }

    @Override
    public void onParkResult(Result result) {
        slotPanel.updateSlots(); // Ensure UI updates even on failure
        if (result.isSuccess()) {
            MessageBox.showInfo(result.getMessage());
        } else {
            MessageBox.showError(result.getMessage(), "Check the license plate format or availability.");
        }
        updateStatusBar(result.getMessage());
    }

    @Override
    public void onUnparkResult(Result result) {
        slotPanel.updateSlots(); // Ensure UI updates
        if (result.isSuccess()) {
            MessageBox.showInfo(result.getMessage());
        }
        updateStatusBar(result.getMessage());
    }

    @Override
    public void onBatchUnparkResult(Result result) {
        slotPanel.updateSlots(); // Ensure UI updates
        if (result.getUnparkedCount() > 0) {
            MessageBox.showInfo(result.getMessage());
        }
        updateStatusBar(result.getMessage());
    }

    @Override
    public void onFindCarResult(Result result) {
        if (result.getSlot() != null) {
            slotPanel.highlightSlot(result.getSlot().getNumber());
            MessageBox.showInfo(result.getMessage());
        } else {
            MessageBox.showInfo(result.getMessage());
        }
        updateStatusBar(result.getMessage());
    }

    @Override
    public void onReportResult(Result result) {
        if (result.isSuccess()) {
            MessageBox.showInfo(result.getMessage());
        } else {
            MessageBox.showError(result.getMessage(), "Check file permissions or disk space.");
        }
        updateStatusBar(result.getMessage());
    }

    @Override
    public void onLoadDataResult(Result result) {
        slotPanel.updateSlots(); // Ensure UI updates on load
        if (result.isSuccess()) {
            Logger.log("ParkingView: Updated slots after loading data");
        } else {
            MessageBox.showError(result.getMessage(), "Using empty parking lot. Check parking_lot.txt format.");
        }
        updateStatusBar(result.getMessage());
    }

    @Override
    public void onStatusUpdate(String message) {
        updateStatusBar(message);
    }

    public void updateStatusBar(String message) {
        statusBar.setText(message);
        statusBarTimer.restart();
    }

    private void openGitHubRepository() {
        String url = "https://github.com/mugabiBenjamin/CarParking.git";
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                updateStatusBar("Opened GitHub repository for online help");
                return;
            } catch (Exception ex) {
                Logger.log("Failed to open browser: " + ex.getMessage());
            }
        }
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html>Could not open browser. Please visit:<br>" + url + "</html>");
        JButton copyButton = new JButton("Copy URL to Clipboard");
        copyButton.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(url), null);
            updateStatusBar("GitHub URL copied to clipboard");
        });
        panel.add(label, BorderLayout.CENTER);
        panel.add(copyButton, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, panel, "Open Online Help Manually", JOptionPane.INFORMATION_MESSAGE);
        updateStatusBar("Displayed online help URL");
    }

    public SlotPanel getSlotPanel() {
        return slotPanel;
    }

    public JLabel getStatusBar() {
        return statusBar;
    }

    public ParkPanel getParkPanel() {
        return parkPanel;
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    public BatchPanel getBatchPanel() {
        return batchPanel;
    }
}