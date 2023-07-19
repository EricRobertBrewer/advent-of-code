package com.ericrobertbrewer.adventofcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class AocUtil {

    private static final String INPUT_DIR = "./input";
    private static final String ENV_VAR = "AOC_SESSION";

    public static File getOrDownloadInputFile(int year, int day) throws IOException {
        final File yearDir = getYearDir(year);
        final File inputFile = new File(yearDir, "" + day + ".txt");
        if (!inputFile.exists()) {
            final String token = System.getenv(ENV_VAR);
            if (token == null) {
                System.err.println("Set the environment variable `" + ENV_VAR + "` as your AoC session token " +
                        "(found in Dev Tools > Application > Cookies > 'session'): " +
                        "$ export " + ENV_VAR + "=<my_token>");
                throw new RuntimeException("Unable to find session token.");
            }
            final URL url = new URL("https://adventofcode.com/" + year + "/day/" + day + "/input");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("cookie", "session=" + token);
            connection.connect();
            final int code = connection.getResponseCode();
            if (code == 200) {
                final InputStream uin = connection.getInputStream();
                byte[] data = new byte[1024];
                int read;
                final OutputStream fout = new FileOutputStream(inputFile);
                while ((read = uin.read(data)) != -1) {
                    fout.write(data, 0, read);
                }
                fout.close();
            } else {
                throw new RuntimeException("Request for input returned code: " + code);
            }
        }
        return inputFile;
    }

    public static File getExampleFile(int year, int day) {
        final File yearDir = getYearDir(year);
        return new File(yearDir, "" + day + "-example.txt");
    }

    private static File getYearDir(int year) {
        final File inputDir = new File(INPUT_DIR);
        final File yearDir = new File(inputDir, "" + year);
        if (!yearDir.exists() && !yearDir.mkdirs()) {
            throw new RuntimeException("Unable to create year directory: " + yearDir.getPath());
        }
        return yearDir;
    }

    private AocUtil() {
    }
}