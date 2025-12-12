package org.gamelog.utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class DeviceUtils {

    private static String cachedDeviceId = null;

    public static String getDeviceId() {
        if (cachedDeviceId != null) {
            return cachedDeviceId;
        }

        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String deviceId = dotenv.get("DEVICE_ID");

            if (deviceId == null || deviceId.isEmpty()) {
                // Generates new device ID and add it to .env
                deviceId = UUID.randomUUID().toString();
                appendToEnvFile("DEVICE_ID", deviceId);
            }

            cachedDeviceId = deviceId;
            return deviceId;

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to generated ID (won't persist, but app will work)
            return UUID.randomUUID().toString();
        }
    }

    private static void appendToEnvFile(String key, String value) {
        try {
            String envPath = ".env";

            // Checks if DEVICE_ID already exists in file
            if (Files.exists(Paths.get(envPath))) {
                List<String> lines = Files.readAllLines(Paths.get(envPath));
                boolean deviceIdExists = lines.stream()
                        .anyMatch(line -> line.startsWith("DEVICE_ID="));

                if (deviceIdExists) {
                    return; // Already exists
                }
            }

            // Appends DEVICE_ID to .env file
            try (FileWriter fw = new FileWriter(envPath, true)) {
                fw.write("\n# Device identifier for session management\n");
                fw.write(key + "=" + value + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
