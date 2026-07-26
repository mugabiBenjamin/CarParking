package util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class MessageBox {
    private static final String INFO_TITLE = "Info";
    private static final String ERROR_TITLE = "Error";
    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred.";
    private static final String DEFAULT_RECOVERY_STEP = "Try the action again. If the issue continues, contact support.";

    private MessageBox() {
        throw new UnsupportedOperationException("MessageBox class cannot be instantiated");
    }

    public static void showInfo(String message) {
        String safeMessage = normalizeMessage(message, "Operation completed.");

        showDialog(() -> JOptionPane.showMessageDialog(
                null,
                safeMessage,
                INFO_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        ));
    }

    public static void showError(String errorMessage, String... recoverySteps) {
        String safeErrorMessage = normalizeMessage(errorMessage, DEFAULT_ERROR_MESSAGE);
        String htmlMessage = buildErrorMessage(safeErrorMessage, recoverySteps);

        showDialog(() -> JOptionPane.showMessageDialog(
                null,
                htmlMessage,
                ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE
        ));
    }

    private static String buildErrorMessage(String errorMessage, String... recoverySteps) {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<p><b>Error:</b> ");
        builder.append(escapeHtml(errorMessage));
        builder.append("</p>");

        if (recoverySteps == null || recoverySteps.length == 0) {
            builder.append("<p><font color='red'><b>Recovery Steps:</b></font></p><ul>");
            builder.append("<li>").append(escapeHtml(DEFAULT_RECOVERY_STEP)).append("</li>");
            builder.append("</ul>");
        } else {
            builder.append("<p><font color='red'><b>Recovery Steps:</b></font></p><ul>");

            for (String step : recoverySteps) {
                String safeStep = normalizeMessage(step, DEFAULT_RECOVERY_STEP);
                builder.append("<li>").append(escapeHtml(safeStep)).append("</li>");
            }

            builder.append("</ul>");
        }

        builder.append("</html>");

        return builder.toString();
    }

    private static void showDialog(Runnable dialogAction) {
        if (dialogAction == null) {
            Logger.warn("MessageBox dialog action was null");
            return;
        }

        try {
            if (SwingUtilities.isEventDispatchThread()) {
                dialogAction.run();
            } else {
                SwingUtilities.invokeLater(dialogAction);
            }
        } catch (Exception exception) {
            Logger.error("Failed to display message dialog", exception);
        }
    }

    private static String normalizeMessage(String message, String fallback) {
        if (message == null || message.trim().isEmpty()) {
            return fallback;
        }

        return message.trim();
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}