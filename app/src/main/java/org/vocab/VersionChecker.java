package org.vocab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Checks if the application is up-to-date.
 * @see #isUpToDate().
 */
public final class VersionChecker {
    /**
     * The location of the latest version file (GitHub repo of the application).
     */
    private static final String VERSION_URL = "https://raw.githubusercontent.com/iasonasTan/Vocabulary-Quiz/master/app/src/main/resources/app_version.txt";

    /**
     * Checks if the application is up-to-date.
     * @return True whether the app is up-to-date; false otherwise.
     */
    public boolean isUpToDate() {
        try {
            int appVersion = getAppVersion();
            int latestVersion = getLatestVersion();
            return !(latestVersion > appVersion);
        } catch (IOException ioe) {
            IO.println(ioe.getMessage());
            return false; // Tell 'false' so the user downloads the version without the bug.
        }
    }

    /**
     * Returns the version of the application using {@link #readVersionFromStream(InputStream)}.
     * @return Returns latest version of the application.
     * @throws IOException If any IOException occurs with reading the file or the input stream or if any NFE occurs (in the stack).
     * @see #readVersionFromStream(InputStream).
     */
    private int getAppVersion() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/app_version.txt")) {
            return readVersionFromStream(inputStream);
        }
    }

    /**
     * Returns the latest version of the application using {@link #readVersionFromStream(InputStream)}.
     * @return Returns latest version of the application.
     * @throws IOException If any IOException occurs with reading the file or the input stream or if any NFE occurs (in the stack).
     * @see #readVersionFromStream(InputStream).
     */
    private int getLatestVersion() throws IOException {
        try (InputStream inputStream = URI.create(VERSION_URL).toURL().openStream()) {
            return readVersionFromStream(inputStream);
        }
    }

    /**
     * Reads file from given {@link InputStream} and returns the containing number if it is a number.
     * @param inputStream Input stream to read number from.
     * @return Returns contents of the file as string.
     * @throws IOException If any IOException occurs with reading the file or the input stream or if any NFE occurs.
     */
    private int readVersionFromStream(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            String version = inputStreamReader.readAllAsString()
                    .replace(" ", "")
                    .replace("\n", "");
            return Integer.parseInt(version);
        } catch (NumberFormatException nfe) {
            throw new IOException("Could not read version from given InputStream (NumberFormatException).");
        }
    }
}
