import util.Logger;
import util.MessageBox;
import view.ParkingView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() {
        throw new UnsupportedOperationException("Main class cannot be instantiated");
    }

    public static void main(String[] args) {
        registerGlobalExceptionHandler();

        SwingUtilities.invokeLater(() -> {
            try {
                configureLookAndFeel();
                new ParkingView();
            } catch (Exception exception) {
                handleStartupFailure(exception);
            }
        });
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception exception) {
            Logger.warn("Failed to apply cross-platform look and feel: " + exception.getMessage());
        }
    }

    private static void registerGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            Logger.error("Unexpected error in thread " + thread.getName() + ": " + exception.getMessage());

            SwingUtilities.invokeLater(() -> MessageBox.showError(
                    "An unexpected application error occurred.",
                    "Close the application and open it again.",
                    "If the issue continues, contact support with the latest log details."
            ));
        });
    }

    private static void handleStartupFailure(Exception exception) {
        Logger.error("Application startup failed: " + exception.getMessage());

        MessageBox.showError(
                "The application failed to start.",
                "Confirm that all required resources are available.",
                "Check file permissions for the application directory.",
                "Restart the application and try again."
        );
    }
}