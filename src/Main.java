import infrastructure.logging.AppLogger;
import infrastructure.logging.ConsoleLogger;
import presentation.swing.SwingApplication;
import presentation.swing.dialogs.MessageBox;

import javax.swing.SwingUtilities;

public final class Main {
    private static final AppLogger LOGGER = new ConsoleLogger();

    private Main() {
        throw new UnsupportedOperationException("Main class cannot be instantiated");
    }

    public static void main(String[] args) {
        registerGlobalExceptionHandler();

        SwingApplication application = new SwingApplication();
        application.start();
    }

    private static void registerGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            LOGGER.error("Unexpected error in thread " + thread.getName(), exception);

            SwingUtilities.invokeLater(() -> {
                MessageBox messageBox = new MessageBox(LOGGER);

                messageBox.showError(
                        "An unexpected application error occurred.",
                        "Close the application and open it again.",
                        "If the issue continues, contact support with the latest log details."
                );
            });
        });
    }
}