package view;

import controller.ParkingController;
import model.ParkingLot;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ParkingView extends JFrame {
    private final ParkingController controller;
    private final ParkingLot lot;
    private JLabel statusBar; // Removed 'final' to allow assignment in initUI
    private SlotPanel slotPanel;
    private final Timer statusBarTimer;

    public ParkingView() {
        setTitle("Car Parking System");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add Help Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JMenuItem visitGithubItem = new JMenuItem("Visit GitHub Repository");
        visitGithubItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        visitGithubItem.setToolTipText("Open the GitHub repository in your browser");
        visitGithubItem.addActionListener(e -> openGitHubRepository());
        helpMenu.add(visitGithubItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Set window icon
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
        this.controller = new ParkingController(lot);
        this.slotPanel = null;
        this.statusBarTimer = new Timer(5000, e -> statusBar.setText("Ready"));
        this.statusBarTimer.setRepeats(false);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        statusBar = new JLabel("Ready", SwingConstants.CENTER);
        statusBar.setForeground(Color.BLACK);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusBar.setToolTipText("Shows parking and search status, clears after 5 seconds");

        ParkPanel parkPanel = new ParkPanel(controller);
        SearchPanel searchPanel = new SearchPanel(controller, slotPanel, statusBar);
        HelpPanel helpPanel = new HelpPanel(statusBar);
        slotPanel = new SlotPanel(controller, lot, statusBar);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(parkPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(searchPanel);
        controlPanel.add(helpPanel);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(slotPanel), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Update slots after initialization
        slotPanel.updateSlots();
    }

    private void openGitHubRepository() {
        String url = "https://github.com/mugabiBenjamin/CarParking.git";
        boolean opened = false;
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                    statusBar.setText("Opened GitHub repository");
                    statusBarTimer.restart();
                    opened = true;
                } catch (Exception ex) {
                    System.err.println("Desktop.browse failed: " + ex.getMessage());
                }
            }
        }
        if (!opened) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                String command;
                if (os.contains("linux")) {
                    command = "xdg-open " + url;
                } else if (os.contains("mac")) {
                    command = "open " + url;
                } else if (os.contains("win")) {
                    command = "start " + url;
                } else {
                    throw new UnsupportedOperationException("Unsupported OS");
                }
                Runtime.getRuntime().exec(command);
                statusBar.setText("Opened GitHub repository");
                statusBarTimer.restart();
                opened = true;
            } catch (Exception ex) {
                System.err.println("Command execution failed: " + ex.getMessage());
            }
        }
        if (!opened) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("<html>Could not open browser. Please visit:<br>" + url + "</html>");
            JButton copyButton = new JButton("Copy URL to Clipboard");
            copyButton.addActionListener(e2 -> {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(url), null);
                statusBar.setText("GitHub URL copied to clipboard");
                statusBarTimer.restart();
            });
            panel.add(label, BorderLayout.CENTER);
            panel.add(copyButton, BorderLayout.SOUTH);
            JOptionPane.showMessageDialog(this, panel, "Open GitHub Manually", JOptionPane.INFORMATION_MESSAGE);
            statusBar.setText("Displayed GitHub URL");
            statusBarTimer.restart();
        }
    }

    // For testing
    public SlotPanel getSlotPanel() {
        return slotPanel;
    }

    public JLabel getStatusBar() {
        return statusBar;
    }
}