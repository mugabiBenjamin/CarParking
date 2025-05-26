package view;

import util.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpPanel extends JPanel {
    private final JLabel statusBar;

    public HelpPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        initComponents();
    }

    private void initComponents() {
        JButton helpButton = new JButton("Help", IconUtil.createHelpIcon(16, 16));
        helpButton.setBorder(new RoundedBorder(8, 1));
        helpButton.setFocusPainted(false);
        helpButton.setContentAreaFilled(true);
        helpButton.setMargin(new Insets(5, 10, 5, 10));
        helpButton.setIconTextGap(4);
        helpButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        helpButton.setToolTipText("Open help guide for using the parking system");
        helpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                helpButton.setBorder(new RoundedBorder(8, 2));
                helpButton.setBackground(helpButton.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                helpButton.setBorder(new RoundedBorder(8, 1));
                helpButton.setBackground(UIManager.getColor("Button.background"));
            }
        });
        helpButton.addActionListener(e -> showHelpDialog());
        add(helpButton);
    }

    private void showHelpDialog() {
        JLabel helpLabel = new JLabel(
                "<html><div style='width: 950px; font-size: 11px; margin: 5px; line-height: 1.2; overflow-wrap: break-word;'>"
                        +
                        "<h3 style='margin: 5px 0;'>Car Parking System User Guide</h3>" +
                        "<p>Welcome to the Car Parking System, a tool to manage parking slots efficiently.<br>" +
                        "Below are instructions for all features.<br></p>" +
                        "<p><b>Parking a Car:</b> In the 'Park a Car' panel, enter a license plate (e.g., UAA 123B, UG 123B, ABC123) and click 'Park' or press Enter.<br>"
                        +
                        "A green check indicates a valid plate; a red X with a tooltip shows errors.<br>" +
                        "Invalid inputs are preserved for correction.<br></p>" +
                        "<p><b>Searching for a Car:</b> In the 'Search for a Car' panel, enter a license plate and click 'Search' or press Enter.<br>"
                        +
                        "If found, the slot highlights blue for 2 seconds.<br>" +
                        "Invalid or unfound plates show a message, and input is preserved.<br></p>" +
                        "<p><b>Unparking a Car:</b> Click the unpark button (trash icon) on an occupied (light red) slot.<br>"
                        +
                        "Confirm the action in the dialog (irreversible).<br></p>" +
                        "<p><b>Batch Unparking:</b> In the 'Batch Operations' panel, select occupied slots using checkboxes, then click 'Batch Unpark'.<br>"
                        +
                        "Confirm the action in the dialog (irreversible).<br>" +
                        "Selected slots are cleared after completion or cancellation.<br></p>" +
                        "<p><b>Generating Reports:</b> Click 'Generate Report' in the 'Batch Operations' panel to create a CSV file (data/parking_lot_report.csv).<br>"
                        +
                        "The report lists slot numbers, status, and license plates.<br></p>" +
                        "<p><b>Slot Status:</b><ul style='margin: 5px 0; padding-left: 20px;'>" +
                        "<li><font color='#90EE90'>Light green</font>: Empty (check icon, not clickable).</li>" +
                        "<li><font color='#FFB6C1'>Light red</font>: Occupied (car icon, clickable for unpark).</li>" +
                        "<li><font color='blue'>Blue</font>: Found (highlighted for 2 seconds after search).</li></ul></p>"
                        +
                        "<p><b>License Plate Formats:</b><ul style='margin: 5px 0; padding-left: 20px;'>" +
                        "<li><b>Normal</b>: UAA 123B (U, 2 letters, space, 3 digits, letter).</li>" +
                        "<li><b>Government</b>: UG 123B (UG, space, 3 digits, letter).</li>" +
                        "<li><b>Personalized</b>: 2–8 characters, starts with a letter (e.g., ABC123, X12 Y34).</li></ul></p>"
                        +
                        "<p><b>Shortcuts:</b> Press Enter in the Park or Search input fields to trigger actions.<br></p>"
                        +
                        "<p><b>Errors:</b> Red borders and tooltips indicate invalid inputs.<br>" +
                        "Error dialogs provide recovery steps.<br>" +
                        "Inputs are preserved for easy correction.<br></p>" +
                        "<p><b>Status Bar:</b> Displays action results (e.g., park, unpark, search) and clears after 5 seconds.<br></p>"
                        +
                        "<p><b>Documentation:</b> Access the GitHub repository via the Online Help menu for source code and further details.<br></p>"
                        +
                        "<p><b>Accessibility:</b> High-contrast colors (light red/green, blue), readable tooltips, and preserved inputs ensure usability.<br></p>"
                        +
                        "<p><b>Data Storage:</b> Parking data is saved to data/parking_lot.txt and loaded on startup.<br>"
                        +
                        "Invalid file formats initialize an empty lot.<br></p>" +
                        "</div></html>");
        JScrollPane scrollPane = new JScrollPane(helpLabel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JOptionPane.showMessageDialog(this, scrollPane, "Car Parking System User Guide",
                JOptionPane.INFORMATION_MESSAGE);
        statusBar.setText("Displayed user guide");
        clearStatusBar();
    }

    private void clearStatusBar() {
        Timer statusBarTimer = new Timer(5000, e -> statusBar.setText("Ready"));
        statusBarTimer.setRepeats(false);
        statusBarTimer.start();
    }

    // For testing
    public JButton getHelpButton() {
        return (JButton) getComponent(0);
    }
}