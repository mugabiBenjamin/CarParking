package view;

import util.IconUtil;
import util.Logger;
import util.MessageBox;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

public final class HelpPanel extends JPanel {
    private static final int STATUS_CLEAR_DELAY_MILLISECONDS = 5000;
    private static final int HELP_DIALOG_WIDTH = 1000;
    private static final int HELP_DIALOG_HEIGHT = 500;
    private static final int HELP_ICON_SIZE = 16;

    private final JLabel statusBar;
    private JButton helpButton;

    public HelpPanel(JLabel statusBar) {
        if (statusBar == null) {
            throw new IllegalArgumentException("Status bar cannot be null");
        }

        this.statusBar = statusBar;
        setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        initComponents();
    }

    private void initComponents() {
        helpButton = new JButton("Help", IconUtil.createHelpIcon(HELP_ICON_SIZE, HELP_ICON_SIZE));
        helpButton.setBorder(new RoundedBorder(8, 1));
        helpButton.setFocusPainted(false);
        helpButton.setContentAreaFilled(true);
        helpButton.setMargin(new Insets(5, 10, 5, 10));
        helpButton.setIconTextGap(4);
        helpButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        helpButton.setToolTipText("Open help guide for using the parking system");
        helpButton.addMouseListener(createHoverEffect(helpButton));
        helpButton.addActionListener(event -> showHelpDialog());

        add(helpButton);
    }

    private MouseAdapter createHoverEffect(JButton button) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBorder(new RoundedBorder(8, 2));

                if (button.getBackground() != null) {
                    button.setBackground(button.getBackground().darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBorder(new RoundedBorder(8, 1));
                button.setBackground(UIManager.getColor("Button.background"));
            }
        };
    }

    private void showHelpDialog() {
        try {
            JLabel helpLabel = new JLabel(buildHelpContent());

            JScrollPane scrollPane = new JScrollPane(
                    helpLabel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            scrollPane.setPreferredSize(new Dimension(HELP_DIALOG_WIDTH, HELP_DIALOG_HEIGHT));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "Car Parking System User Guide",
                    JOptionPane.INFORMATION_MESSAGE
            );

            updateStatus("Displayed user guide");
        } catch (Exception exception) {
            Logger.error("Failed to display help dialog", exception);
            MessageBox.showError(
                    "Unable to display the help guide.",
                    "Close the dialog and try again.",
                    "Restart the application if the problem continues."
            );
            updateStatus("Failed to display user guide");
        }
    }

    private String buildHelpContent() {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<div style='width: 950px; font-size: 11px; margin: 5px; line-height: 1.2; overflow-wrap: break-word;'>");

        builder.append("<h3 style='margin: 5px 0;'>Car Parking System User Guide</h3>");

        builder.append("<p>Welcome to the Car Parking System, a tool to manage parking slots efficiently.<br>");
        builder.append("Below are instructions for all features.<br></p>");

        builder.append("<p><b>Parking a Car:</b> In the 'Park a Car' panel, enter a license plate and click 'Park' or press Enter.<br>");
        builder.append("Supported examples include UAA 123B, UG 123B, UA 001AA, UG 32 00042, CD 01 02 U, UMA 001AA, and valid personalized plates.<br>");
        builder.append("A green check indicates a valid plate; a red X with a tooltip shows errors.<br>");
        builder.append("Invalid inputs are preserved for correction.<br></p>");

        builder.append("<p><b>Searching for a Car:</b> In the 'Search for a Car' panel, enter a license plate and click 'Search' or press Enter.<br>");
        builder.append("If found, the slot highlights blue for 2 seconds.<br>");
        builder.append("Invalid or unfound plates show a message, and input is preserved.<br></p>");

        builder.append("<p><b>Unparking a Car:</b> Click the unpark button on an occupied slot.<br>");
        builder.append("Confirm the action in the dialog before the slot is cleared.<br></p>");

        builder.append("<p><b>Batch Unparking:</b> In the 'Batch Operations' panel, select occupied slots using checkboxes, then click 'Batch Unpark'.<br>");
        builder.append("Confirm the action in the dialog before selected slots are cleared.<br></p>");

        builder.append("<p><b>Generating Reports:</b> Click 'Generate Report' in the 'Batch Operations' panel to create a CSV report.<br>");
        builder.append("The report lists slot numbers, status, and license plates.<br></p>");

        builder.append("<p><b>Slot Status:</b></p>");
        builder.append("<ul style='margin: 5px 0; padding-left: 20px;'>");
        builder.append("<li><font color='#90EE90'>Light green</font>: Empty slot.</li>");
        builder.append("<li><font color='#FFB6C1'>Light red</font>: Occupied slot.</li>");
        builder.append("<li><font color='blue'>Blue</font>: Found slot highlight.</li>");
        builder.append("</ul>");

        builder.append("<p><b>License Plate Formats:</b></p>");
        builder.append("<ul style='margin: 5px 0; padding-left: 20px;'>");
        builder.append("<li><b>Ordinary private:</b> UA 001AA.</li>");
        builder.append("<li><b>Legacy private:</b> UAA 123B.</li>");
        builder.append("<li><b>Government:</b> UG 32 00042.</li>");
        builder.append("<li><b>Legacy government:</b> UG 123B.</li>");
        builder.append("<li><b>Diplomatic:</b> CD 01 02 U.</li>");
        builder.append("<li><b>Motorcycle:</b> UMA 001AA.</li>");
        builder.append("<li><b>Personalized:</b> 2–8 characters, starts with a letter.</li>");
        builder.append("</ul>");

        builder.append("<p><b>Shortcuts:</b> Press Enter in the Park or Search input fields to trigger actions.<br></p>");

        builder.append("<p><b>Errors:</b> Red borders and tooltips indicate invalid inputs.<br>");
        builder.append("Error dialogs provide recovery steps.<br>");
        builder.append("Inputs are preserved for easy correction.<br></p>");

        builder.append("<p><b>Status Bar:</b> Displays action results and clears after 5 seconds.<br></p>");

        builder.append("<p><b>Accessibility:</b> High-contrast colors, readable tooltips, and preserved inputs improve usability.<br></p>");

        builder.append("<p><b>Data Storage:</b> Parking data is saved locally and loaded on startup.<br>");
        builder.append("Invalid file formats initialize an empty lot.<br></p>");

        builder.append("</div>");
        builder.append("</html>");

        return builder.toString();
    }

    private void updateStatus(String message) {
        statusBar.setText(message);
        clearStatusBar();
    }

    private void clearStatusBar() {
        Timer timer = new Timer(STATUS_CLEAR_DELAY_MILLISECONDS, event -> statusBar.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }

    public JButton getHelpButton() {
        return helpButton;
    }
}