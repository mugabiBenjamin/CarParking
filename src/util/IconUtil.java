package util;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public final class IconUtil {
    private static final String CAR_ICON = "/resources/icons/car.png";
    private static final String SEARCH_ICON = "/resources/icons/search.png";
    private static final String UNPARK_ICON = "/resources/icons/unpark.png";
    private static final String CHECK_ICON = "/resources/icons/check.png";
    private static final String CHECK_GREEN_ICON = "/resources/icons/check-green.png";
    private static final String X_ICON = "/resources/icons/x.png";
    private static final String REPORT_ICON = "/resources/icons/report.png";
    private static final String HELP_ICON = "/resources/icons/help.png";

    private static final int DEFAULT_ICON_SIZE = 16;

    private IconUtil() {
        throw new UnsupportedOperationException("IconUtil class cannot be instantiated");
    }

    public static ImageIcon createCarIcon(int width, int height) {
        return loadIcon(CAR_ICON, width, height);
    }

    public static ImageIcon createSearchIcon(int width, int height) {
        return loadIcon(SEARCH_ICON, width, height);
    }

    public static ImageIcon createUnparkIcon(int width, int height) {
        return loadIcon(UNPARK_ICON, width, height);
    }

    public static ImageIcon createCheckIcon(int width, int height, String type) {
        if ("slot".equalsIgnoreCase(safeText(type))) {
            return loadIcon(CHECK_ICON, width, height);
        }

        return loadIcon(CHECK_GREEN_ICON, width, height);
    }

    public static ImageIcon createXIcon(int width, int height) {
        return loadIcon(X_ICON, width, height);
    }

    public static ImageIcon createReportIcon(int width, int height) {
        return loadIcon(REPORT_ICON, width, height);
    }

    public static ImageIcon createHelpIcon(int width, int height) {
        return loadIcon(HELP_ICON, width, height);
    }

    private static ImageIcon loadIcon(String path, int width, int height) {
        String safePath = safeText(path);

        if (safePath.isEmpty()) {
            Logger.warn("Icon path was empty");
            return null;
        }

        int safeWidth = normalizeSize(width);
        int safeHeight = normalizeSize(height);

        try (InputStream stream = IconUtil.class.getResourceAsStream(safePath)) {
            if (stream == null) {
                Logger.warn("Icon resource not found: " + safePath);
                return null;
            }

            ImageIcon icon = new ImageIcon(stream.readAllBytes());

            if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
                Logger.warn("Invalid icon resource: " + safePath);
                return null;
            }

            Image scaledImage = icon.getImage().getScaledInstance(safeWidth, safeHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException exception) {
            Logger.error("Failed to load icon " + safePath, exception);
            return null;
        } catch (Exception exception) {
            Logger.error("Unexpected error while loading icon " + safePath, exception);
            return null;
        }
    }

    private static int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_ICON_SIZE;
        }

        return size;
    }

    private static String safeText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}