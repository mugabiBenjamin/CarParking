package util;

import javax.swing.*;

public class MessageBox {

    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Displays error message with specific recovery steps in a formatted dialog
    // Uses HTML for bold error and bulleted recovery steps; black text (~21:1
    // contrast on white),
    // red for recovery header (~4.7:1 contrast)
    public static void showError(String errorMessage, String... recoverySteps) {
        StringBuilder htmlMessage = new StringBuilder("<html><p><b>Error:</b> ");
        htmlMessage.append(errorMessage).append("</p>");
        if (recoverySteps.length > 0) {
            htmlMessage.append("<p><font color='red'><b>Recovery Steps:</b></font></p><ul>");
            for (String step : recoverySteps) {
                htmlMessage.append("<li>").append(step).append("</li>");
            }
            htmlMessage.append("</ul>");
        }
        htmlMessage.append("</html>");
        JOptionPane.showMessageDialog(null, htmlMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}