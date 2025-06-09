package academy.prog.julia.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Utils {
    private static final String hostname = "http://localhost:3000"; //change this hostname for production

    public static final String TELEGRAM_URL = "https://t.me/%s?start=%s";
    public static final String REGISTER_URL =  hostname + "/invite%n Your invite code: %s";

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final byte[] SEPARATOR = { ':' };
    private static final SecureRandom random;

    static {
        random = new SecureRandom();
    }

    /**
     * Generates a random string of the specified length.
     * The string is composed of characters from the predefined charset (CHARSET).
     * @param length The desired length of the random string.
     * @return A random string of the specified length.
     */
    public static String randomString(int length) {
        var result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            var randomIndex = random.nextInt(CHARSET.length());
            var randomChar = CHARSET.charAt(randomIndex);
            result.append(randomChar);
        }

        return result.toString();
    }

    /**
     * Hashes the provided byte buffers using the SHA-256 algorithm.
     * For each byte array, the digest is updated and a separator is added between digests.
     * @param bufs One or more byte arrays to be hashed.
     * @return A hexadecimal string representation of the combined hash.
     */
    public static String hashBuffers(byte[]... bufs) {
        try {
            var hash = MessageDigest.getInstance("SHA-256");

            for (var buf : bufs) {
                hash.digest(buf);
                hash.digest(SEPARATOR);
            }

            return arrayToString(hash.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hashes the provided strings using the SHA-256 algorithm.
     * Each string is converted to a byte array (UTF-8 encoding), and a separator is added between digests.
     * @param strings One or more strings to be hashed.
     * @return A hexadecimal string representation of the combined hash.
     */
    public static String hashStrings(String... strings) {
        try {
            var hash = MessageDigest.getInstance("SHA-256");

            for (var s : strings) {
                var buf = s.getBytes(StandardCharsets.UTF_8);

                hash.update(buf);
                hash.update(SEPARATOR);
            }

            return arrayToString(hash.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte array into a hexadecimal string representation.
     * Each byte is converted to a two-digit hex value.
     * @param byteArray The byte array to be converted.
     * @return A string of hexadecimal characters.
     */
    private static String arrayToString(byte[] byteArray) {
        var result = new StringBuilder();

        for (byte b : byteArray) {
            result.append(String.format("%02X", b));
        }

        return result.toString();
    }

    /**
     * Shortens a URL using the TinyURL API, making it more suitable for use in environments like Telegram bots.
     * If the shortening fails for any reason, it returns the original URL as a fallback.
     * @param originalUrl The URL to be shortened.
     * @return The shortened URL if successful, otherwise the original URL.
     */
    public static String getShortenedUrl(String originalUrl) {
        try {
            String tinyUrlApi = "http://tinyurl.com/api-create.php?url=" + originalUrl;
            URL url = new URL(tinyUrlApi);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            return originalUrl;
        }
    }
}
