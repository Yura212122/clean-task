package academy.prog.julia.telegram.states;

import academy.prog.julia.components.DocsSheets;
import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

/**
 * State class for handling the process of entering credentials for Google API.
 * This class extends CommandState and is responsible for prompting the user to upload a credentials file,
 * processing the uploaded file, extracting necessary client information, and guiding the user through
 * further configuration steps.
 */
public class EnterCredentialsFileState extends CommandState {

    private static final Logger LOGGER = LogManager.getLogger(EnterCredentialsFileState.class);

    private final String inputAttributeName;
    private final String botToken;

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static volatile boolean isProcessing = false;

    private final ApiConfigProperties apiConfigProperties;

    /**
     * Constructor to initialize the state with attribute names and bot token.
     *
     * @param inputAttributeName the name of the attribute to store the file path
     * @param botToken the bot token for Telegram API access
     */
    public EnterCredentialsFileState(
            String inputAttributeName,
            String botToken,
            ApiConfigProperties apiConfigProperties
    ) {
        super(true);
        this.inputAttributeName = inputAttributeName;
        this.botToken = botToken;
        this.apiConfigProperties = apiConfigProperties;
    }

    /**
     * Sends a message to prompt the user to upload the credentials file.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        context.sendMessage("Please upload the credentials.json file from Google:");
    }

    /**
     * Handles the input when a file is uploaded. Processes the file, extracts the client ID,
     * updates user settings, and provides a link for further actions.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void handleInput(StateExecutionContext context) {
        // Check if the update contains a document (file)
        if (context.getUpdate().getMessage().hasDocument()) {
            Document document = context.getUpdate().getMessage().getDocument();
            String fileId = document.getFileId();

            if (isProcessing) {
                context.sendMessage("The process is already in progress!\n" +
                        "Please use the link that was generated earlier.")
                ;
                return;
            }

            isProcessing = true;
            new Thread(() -> {
                try {
                    try {
                        clearStoredCredentialFile();
                        String filePath = downloadFile(fileId);
                        context.putAttribute(inputAttributeName, filePath);

                        String clientIdFromCredentialsFile = extractClientId(filePath);

                        LOGGER.info("Google user id key: {}", clientIdFromCredentialsFile);

                        context.sendMessage("File uploaded successfully!");

                        // Update user ID and provide a link for further configuration
                        DocsSheets.updateUserId(clientIdFromCredentialsFile);
                        context.sendMessage(
                                "Please, visit this link and make the necessary settings: \n" +
                                apiConfigProperties.getGoogleSecurityAccountPartOneUrl() +
                                clientIdFromCredentialsFile +
                                apiConfigProperties.getGoogleSecurityAccountPartTwoUrl() +
                                "\nThe link will remain active for one hour.\n" +
                                "All chat-bot commands you can use as usual."
                        );

                        // Reload Google client secrets and notify the user
                        try {
                            DocsSheets.reloadCredentials();
                            LOGGER.info("Google client secrets reloaded successfully!");
                            context.sendMessage("Google client secrets reloaded successfully!");
                        } catch (GeneralSecurityException | IOException e) {
                            context.sendMessage("Failed to reload Google client secrets: " + e.getMessage());
                            LOGGER.error("Error reloading Google client secrets", e);
                        }

                        context.setFinished(true);

                    } catch (IOException | TelegramApiException e) {
                        context.sendMessage("Failed to upload file: " + e.getMessage());
                        LOGGER.error("Error uploading file", e);
                    }

                } finally {
                    isProcessing = false; // Mark processing as complete
                }
            }).start();

        } else {
            context.sendMessage("Invalid input. Please upload a valid file.");
        }
    }

    /**
     * Downloads the file from Telegram using its file ID and saves it locally.
     *
     * @param fileId the ID of the file to download
     * @return the local path where the file was saved
     * @throws IOException if an error occurs during file operations
     * @throws TelegramApiException if an error occurs with the Telegram API
     */
    public String downloadFile(String fileId) throws IOException, TelegramApiException {

        URL url = new URL(
                apiConfigProperties.getTelegramApiUrlPartOne() +
                botToken +
                apiConfigProperties.getTelegramApiUrlPartTwo() +
                fileId
        );

        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String res = in.readLine();
            JSONObject jResult = new JSONObject(res);
            JSONObject path = jResult.getJSONObject("result");
            String filePath = path.getString("file_path");
            URL download = new URL(apiConfigProperties.getDownloadViaTelegram() + botToken + "/" + filePath);

            Path savePath = Paths.get(apiConfigProperties.getPathUpCredentials() + "credentials.json");

            if (Files.exists(savePath)) {
                Files.delete(savePath);
            }

            try (FileOutputStream fos = new FileOutputStream(savePath.toFile());
                 ReadableByteChannel rbc = Channels.newChannel(download.openStream())) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                LOGGER.info("File uploaded successfully!");
            } catch (IOException e) {
                LOGGER.error("Error saving file", e);
                throw e;
            }

            return savePath.toString();

        } catch (IOException e) {
            LOGGER.error("Error downloading file", e);
            throw e;
        }
    }

    /**
     * Deletes any previously stored credential file to clear old data.
     */
    private void clearStoredCredentialFile() {
        try {
            Path tokensPath = Paths.get(TOKENS_DIRECTORY_PATH, "StoredCredential");
            Files.deleteIfExists(tokensPath);
            LOGGER.info("Cached credentials cleared and token file deleted successfully.");
        } catch (IOException e) {
            LOGGER.error("Error deleting token file", e);
        }
    }

    /**
     * Extracts the client ID from the credentials JSON file.
     *
     * @param filePath the path to the credentials file
     * @return the client ID extracted from the file
     * @throws IOException if an error occurs while reading the file
     */
    private String extractClientId(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            JSONObject json = new JSONObject(reader.lines().reduce("", (acc, line) -> acc + line));

            return json.getJSONObject("installed").getString("client_id");
        }
    }

}
