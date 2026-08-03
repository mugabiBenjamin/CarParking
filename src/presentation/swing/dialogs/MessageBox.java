package presentation.swing.dialogs;

import infrastructure.logging.AppLogger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class MessageBox {
    private static final String INFO_TITLE = "Info";
    private static final String ERROR_TITLE = "Error";
    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred.";
    private static final String DEFAULT_RECOVERY_STEP = "Try again. If the issue continues, contact support.";

    private final AppLogger logger;

    public MessageBox(AppLogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger cannot be null");
        }

        this.logger = logger;
    }

    public void showInfo(String message) {
        String safeMessage = normalize(message, "Operation completed.");

        showDialog(() -> JOptionPane.showMessageDialog(
                null,
                safeMessage,
                INFO_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        ));
    }

    public void showError(String errorMessage, String... recoverySteps) {
        String safeErrorMessage = normalize(errorMessage, DEFAULT_ERROR_MESSAGE);
        String htmlMessage = buildErrorMessage(safeErrorMessage, recoverySteps);

        showDialog(() -> JOptionPane.showMessageDialog(
                null,
                htmlMessage,
                ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE
        ));
    }

    private String buildErrorMessage(String errorMessage, String... recoverySteps) {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<p><b>Error:</b> ");
        builder.append(escapeHtml(errorMessage));
        builder.append("</p>");
        builder.append("<p><font color='red'><b>Recovery Steps:</b></font></p>");
        builder.append("<ul>");

        if (recoverySteps == null || recoverySteps.length == 0) {
            builder.append("<li>").append(escapeHtml(DEFAULT_RECOVERY_STEP)).append("</li>");
        } else {
            for (String step : recoverySteps) {
                builder.append("<li>").append(escapeHtml(normalize(step, DEFAULT_RECOVERY_STEP))).append("</li>");
            }
        }

        builder.append("</ul>");
        builder.append("</html>");

        return builder.toString();
    }

    private void showDialog(Runnable dialogAction) {
        if (dialogAction == null) {
            logger.warn("Dialog action was null");
            return;
        }

        try {
            if (SwingUtilities.isEventDispatchThread()) {
                dialogAction.run();
            } else {
                SwingUtilities.invokeLater(dialogAction);
            }
        } catch (Exception exception) {
            logger.error("Failed to display dialog", exception);
        }
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private String escapeHtml(String value) {
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