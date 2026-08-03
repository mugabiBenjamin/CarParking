package presentation.swing.panels;

import infrastructure.logging.AppLogger;
import presentation.swing.components.RoundedBorder;
import presentation.swing.dialogs.MessageBox;
import presentation.swing.resources.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class HelpPanel extends JPanel {
    private static final int HELP_DIALOG_WIDTH = 1000;
    private static final int HELP_DIALOG_HEIGHT = 500;
    private static final int ICON_SIZE = 16;

    private final IconUtil iconUtil;
    private final MessageBox messageBox;
    private final AppLogger logger;

    public HelpPanel(IconUtil iconUtil, MessageBox messageBox, AppLogger logger) {
        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        if (messageBox == null) {
            throw new IllegalArgumentException("Message box cannot be null");
        }

        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.iconUtil = iconUtil;
        this.messageBox = messageBox;
        this.logger = logger;

        setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        initializeComponents();
    }

    private void initializeComponents() {
        JButton helpButton = new JButton("Help", iconUtil.createHelpIcon(ICON_SIZE, ICON_SIZE));
        helpButton.setBorder(new RoundedBorder(8, 1));
        helpButton.setFocusPainted(false);
        helpButton.setContentAreaFilled(true);
        helpButton.setMargin(new Insets(5, 10, 5, 10));
        helpButton.setIconTextGap(4);
        helpButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        helpButton.setToolTipText("Open help guide");
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
        } catch (Exception exception) {
            logger.error("Failed to display help dialog", exception);

            messageBox.showError(
                    "Unable to display the help guide.",
                    "Close the dialog and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    private String buildHelpContent() {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<div style='width: 950px; font-size: 11px; margin: 5px; line-height: 1.2;'>");

        builder.append("<h3>Car Parking System User Guide</h3>");

        builder.append("<p><b>Parking a Car:</b> Enter a valid license plate and click Park or press Enter.</p>");
        builder.append("<p><b>Searching:</b> Enter a license plate and click Search or press Enter.</p>");
        builder.append("<p><b>Unparking:</b> Click Unpark on an occupied slot and confirm.</p>");
        builder.append("<p><b>Batch Unparking:</b> Select occupied slots, then click Batch Unpark.</p>");
        builder.append("<p><b>Reports:</b> Click Generate Report to create a CSV parking report.</p>");

        builder.append("<p><b>Supported Plate Examples:</b></p>");
        builder.append("<ul>");
        builder.append("<li>Ordinary private: UA 001AA</li>");
        builder.append("<li>Legacy private: UAA 123B</li>");
        builder.append("<li>Government: UG 32 00042</li>");
        builder.append("<li>Legacy government: UG 123B</li>");
        builder.append("<li>Diplomatic: CD 01 02 U</li>");
        builder.append("<li>Motorcycle: UMA 001AA</li>");
        builder.append("<li>Personalized: 2–8 characters, starts with a letter</li>");
        builder.append("</ul>");

        builder.append("<p><b>Slot Colors:</b></p>");
        builder.append("<ul>");
        builder.append("<li>Light green: Empty slot</li>");
        builder.append("<li>Light red: Occupied slot</li>");
        builder.append("<li>Blue: Found slot highlight</li>");
        builder.append("</ul>");

        builder.append("</div>");
        builder.append("</html>");

        return builder.toString();
    }
}