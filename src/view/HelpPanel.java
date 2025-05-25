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
                "<html><div style='width: 550px; font-size: 11px; margin: 5px; line-height: 1.2;'>" +
                        "<h3 style='margin: 5px 0;'>Car Parking System Help</h3>" +
                        "<p><b>Parking a Car:</b> Enter a license plate (e.g., UAA 123B, UG 123B, ABC123) in the top field. "
                        +
                        "Use 'Park' or Enter. Green check for valid, red X with tooltip for invalid. Input preserved on errors.</p>"
                        +
                        "<p><b>Searching:</b> Enter a plate in the search field, use 'Search' or Enter. Valid plates highlight slot in blue for 2s. Input preserved if invalid or not found.</p>"
                        +
                        "<p><b>Removing a Car:</b> Click an occupied (light red) slot, confirm to unpark (irreversible).</p>"
                        +
                        "<p><b>Slot Status:</b><ul style='margin: 5px 0; padding-left: 20px;'>" +
                        "<li><font color='#90EE90'>Light green</font>: Empty (check icon, not clickable).</li>" +
                        "<li><font color='#FFB6C1'>Light red</font>: Occupied (car icon, clickable).</li>" +
                        "<li><font color='blue'>Blue</font>: Found (after search).</li></ul></p>" +
                        "<p><b>Plate Formats:</b><ul style='margin: 5px 0; padding-left: 20px;'>" +
                        "<li><b>Normal</b>: UAA 123B (U, 2 letters, space, 3 digits, letter).</li>" +
                        "<li><b>Government</b>: UG 123B (UG, space, 3 digits, letter).</li>" +
                        "<li><b>Personalized</b>: 2–8 chars, starts with letter (e.g., ABC123, X12 Y34).</li></ul></p>"
                        +
                        "<p><b>Shortcuts:</b> Enter in input fields to park or search.</p>" +
                        "<p><b>Errors:</b> Tooltips and red borders guide corrections. Input preserved for editing.</p>" +
                        "<p><b>Documentation:</b> Use the Help menu to visit the GitHub repository.</p>" +
                        "<p><b>Accessibility:</b> High-contrast colors, preserved inputs, readable tooltips.</p>" +
                        "<p><b>Status Bar:</b> Shows actions, clears after 5s.</p>" +
                        "</div></html>");
        JScrollPane scrollPane = new JScrollPane(helpLabel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JOptionPane.showMessageDialog(this, scrollPane, "Help Guide", JOptionPane.INFORMATION_MESSAGE);
        statusBar.setText("Displayed help guide");
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