package academy.prog.julia.components;

import academy.prog.julia.exceptions.TestQuestionsNotFound;
import academy.prog.julia.model.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * This class handles the interaction with Google Sheets and Google Docs APIs to read tasks, tests, and lessons.
 */
@Component
public class DocsSheets {

    /**
     * Variables from Discourse management:
     *
     * @field credentialsFilePath           The file path to the Google API credentials JSON file. This file is used
     *                                      for authentication and authorization when accessing Google APIs.
     * @field tokensDirectoryPath           The directory path where Google API tokens are stored. Tokens are used to
     *                                      manage access and refresh authorization for Google APIs.
     * @field range                         The range in the Google Sheets document for tasks. This specifies the part
     *                                      of the spreadsheet that contains the task data.
     * @field rangeForTestsAnswers          The range in the Google Sheets document for test answers. This specifies
     *                                      the part of the spreadsheet that contains the test answer's data.
     * @field urlForExtractDocumentId       The URL for extracting the document ID. This URL is used to identify and
     *                                      access specific Google Docs documents.
     * @field urlForExtractSpreadsheetsId   The URL for extracting the spreadsheet ID. This URL is used to identify and
     *                                      access specific Google Sheets documents.
     */
    @Value("${google.credentials.file.path}")
    private String credentialsFilePathProp;

    @Value("${google.tokens.directory.path}")
    private String tokensDirectoryPathProp;

    @Value("${google.sheets.range.for.course}")
    private String rangeForCourseProp;

    @Value("${google.sheets.range.for.tests.answers}")
    private String rangeForTestsAnswersProp;

    @Value("${google.sheets.for.extract.document.id}")
    private String urlForExtractDocumentIdProp;

    @Value("${google.sheets.for.extract.spreadsheets.id}")
    private String urlForExtractSpreadsheetsIdProp;


    private static String credentialsFilePath;
    private static String tokensDirectoryPath;
    private static String rangeForCourse;
    private static String rangeForTestsAnswers;
    private static String urlForExtractDocumentId;
    //Unit test does not work without such hardcoding
    private static String urlForExtractSpreadsheetsId = "https://docs.google.com/spreadsheets/d/";

    /**
     * Initializes the static fields with values from the properties file.
     *
     * This method is called after the bean's properties have been set, and it ensures
     * that the static fields used throughout the class are populated with the correct
     * values from the application properties. The fields are necessary for proper
     * configuration and operation of the Google Sheets integration and other related services.
     *
     * This method is annotated with @PostConstruct, which means it will be executed
     * automatically after the dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        credentialsFilePath = credentialsFilePathProp;
        tokensDirectoryPath = tokensDirectoryPathProp;
        rangeForCourse = rangeForCourseProp;
        rangeForTestsAnswers = rangeForTestsAnswersProp;
        urlForExtractDocumentId = urlForExtractDocumentIdProp;
        urlForExtractSpreadsheetsId = urlForExtractSpreadsheetsIdProp;
    }



    private static final String APPLICATION_NAME = "Google Sheets & Docs API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final Logger LOGGER = LogManager.getLogger(DocsSheets.class);

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            List.of(SheetsScopes.SPREADSHEETS_READONLY, DocsScopes.DOCUMENTS_READONLY);

    private static String userIdKey;
    private static Credential cachedCredential = null;
    private static LocalServerReceiver receiver = null;

    /**
     * Updates the user ID key.
     *
     * @param userId    the user ID to set.
     */
    public static void updateUserId(String userId) {
        userIdKey = userId;
    }

    /**
     * Clears the cached credentials.
     */
    public static void clearCachedCredential() {
        cachedCredential = null;
    }

    /**
     * Reloads the credentials for Google API access.
     *
     * @return  a Credential object for Google API access.
     * @throws  GeneralSecurityException if there is a security issue.
     * @throws  IOException if an I/O error occurs.
     */
    public static Credential reloadCredentials() throws GeneralSecurityException, IOException {
        clearCachedCredential();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Credential> task = () -> getCredentials(GoogleNetHttpTransport.newTrustedTransport());
        Future<Credential> future = executor.submit(task);

        try {
            cachedCredential = future.get(60, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            future.cancel(true);
            receiver.stop();
            LOGGER.error("Timeout reached. Failed to reload Google client secrets in time.", e);
            throw new IOException("Timeout reached. Failed to reload Google client secrets in time.", e);
        } catch (InterruptedException | ExecutionException e) {
            receiver.stop();
            LOGGER.error("Failed to reload Google client secrets", e);
            throw new IOException("Failed to reload Google client secrets", e);
        } finally {
            executor.shutdownNow();
        }

        return cachedCredential;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param   HTTP_TRANSPORT the HTTP transport.
     * @return  a Credential object.
     * @throws  IOException if an I/O error occurs.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        if (cachedCredential == null) {
            File credentialsFile = new File(credentialsFilePath);
            if (!credentialsFile.exists() || credentialsFile.length() == 0) {
                LOGGER.error("Credentials file not found at {} or it's EMPTY!", credentialsFilePath);
                throw new FileNotFoundException("Credentials file not found at " + credentialsFilePath +
                        " or it's EMPTY!");
            }

            InputStream in = new FileInputStream(credentialsFilePath);
            try {
                GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
                String clientIdFromCredentialsFile = clientSecrets.getDetails().getClientId();

                if (clientIdFromCredentialsFile == null || clientIdFromCredentialsFile.isBlank()) {
                    clientIdFromCredentialsFile = "default_name";
                }

                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
                        .setAccessType("offline")
                        .build();
                receiver = new LocalServerReceiver.Builder().setPort(8888).build();
                cachedCredential =
                        new AuthorizationCodeInstalledApp(flow, receiver).authorize(clientIdFromCredentialsFile);
            } catch (IOException e) {
                if (e.getMessage().contains("deleted_client")) {
                    LOGGER.error("OAuth client was deleted. Please check your credentials.");
                } else {
                    LOGGER.error("Error loading client secrets", e);
                }
                throw e;
            }
        }
        return cachedCredential;
    }

    /**
     * Reads tasks from a Google Sheets spreadsheet.
     *
     * @param   spreadsheetURL the URL of the spreadsheet.
     * @param   sheetNumber the sheet number to read.
     * @return  a list of tasks.
     * @throws  GeneralSecurityException if there is a security issue.
     * @throws  IOException if an I/O error occurs.
     */
    public static List<List<Task>> taskReader(
            String spreadsheetURL, int sheetNumber
    ) throws GeneralSecurityException, IOException {

        Sheets service = getSheetService();
        Docs docService = getDocService();

        String titleSheetName = getTitleSheetName(spreadsheetURL, sheetNumber, service);

        final String range = titleSheetName + rangeForCourse;
        ValueRange request = service.spreadsheets().values()
                .get(extractSpreadsheetID(spreadsheetURL), range)
                .execute();

        List<List<Object>> values = request.getValues();
        List<List<Task>> taskList = new ArrayList<>();
        List<Task> tempList = new ArrayList<>();

        int rowCounter = -1;

        for (List<Object> row : values) {
            rowCounter++;
            if (row.contains("Task")) {
                Task task = new Task();
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).equals("Task")) {
                        i++;
                        task.setDescriptionUrl(row.get(i).toString());

                        String titles = "";
                        try {
                            Document docResponse =
                                    docService.documents().get(extractDocumentId(row.get(i).toString())).execute();
                            titles = docResponse.getTitle();
                            task.setName(titles);
                        } catch (GoogleJsonResponseException e) {
                            task.setName("NO ACCESS TO LINK!");
                            LOGGER.error("Access Denied! The caller does not have permission");
                        }

                        i++;
                        if (row.get(i).toString().equalsIgnoreCase(ExpectedResult.LINK.toString())) {
                            task.setExpectedResult(ExpectedResult.LINK);
                        } else {
                            task.setExpectedResult(ExpectedResult.FILE);
                        }

                        try {
                            i++;
                            if (row.get(i) != null)
                                task.setDeadline(stringToLocalDate(row.get(i).toString()));
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                        tempList.add(task);
                    }
                }
            } else if (row.isEmpty()) {
                taskList.add(tempList);
                tempList = new ArrayList<>();
            }
            if (rowCounter == (values.size() - 1) && row.contains("Test")) {
                taskList.add(tempList);
            }
        }
        return taskList;
    }

    /**
     * Reads tests from a Google Sheets spreadsheet.
     *
     * @param   spreadsheetURL  the URL of the spreadsheet.
     * @param   sheetNumber     the sheet number to read.
     * @return      a list of tests.
     * @throws      GeneralSecurityException if there is a security issue.
     * @throws      IOException if an I/O error occurs.
     * @throws      TestQuestionsNotFound if test questions are not found.
     */
    public List<List<Test>> testReader(
            String spreadsheetURL, int sheetNumber
    ) throws GeneralSecurityException, IOException, TestQuestionsNotFound {
        Sheets service = getSheetService();
        String titleSheetName = getTitleSheetName(spreadsheetURL, sheetNumber, service);

        final String range = titleSheetName + rangeForCourse;
        ValueRange request = service.spreadsheets().values()
                .get(extractSpreadsheetID(spreadsheetURL), range)
                .execute();

        List<List<Object>> values = request.getValues();
        List<List<Test>> testList = new ArrayList<>();
        List<Test> tempList = new ArrayList<>();

        int rowCounter = -1;
        for (List<Object> row : values) {
            rowCounter++;
            if (row.contains("Test")) {
                Test test = new Test();
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).equals("Test")) {
                        i++;
                        String testUrl = row.get(i).toString();
                        test.setTestUrl(testUrl);

                        String title = "";
                        try {
                            Spreadsheet spreadsheet =
                                    service.spreadsheets().get(extractSpreadsheetID(testUrl)).execute();
                            title = spreadsheet.getProperties().getTitle();

                            test.setName(title);
                        } catch (GoogleJsonResponseException e) {
                            test.setName("NO ACCESS TO LINK!");
                            LOGGER.error("Access Denied! The caller does not have permission");
                        }

                        try {
                            i++;
                            if (row.get(i).toString().equals("Mandatory"))
                                test.setMandatory(true);
                        } catch (IndexOutOfBoundsException ignored) {
                        }

                        try {
                            i++;
                            if (row.get(i) != null)
                                test.setDeadline(stringToLocalDate(row.get(i).toString()));
                        } catch (IndexOutOfBoundsException ignored) {
                        }

                        Set<TestQuestionFromGoogleDocs> testQuestionsFromGoogleDocs =
                                TestQuestionsReader(testUrl, sheetNumber);
                        test.setTestQuestionFromGoogleDocs(testQuestionsFromGoogleDocs);
                        tempList.add(test);
                    }
                }
            } else if (row.isEmpty()) {
                testList.add(tempList);
                tempList = new ArrayList<>();
            }

            if (rowCounter == (values.size() - 1) && row.contains("Test")) {
                testList.add(tempList);
            }
        }
        return testList;
    }

    /**
     * Reads tests' questions from a Google Sheets spreadsheet.
     *
     * @param   spreadsheetURL  the URL of the spreadsheet.
     * @param   sheetNumber     the sheet number to read.
     * @return      a Set<TestQuestionFromGoogleDocs> of tests.
     * @throws      GeneralSecurityException if there is a security issue.
     * @throws      IOException if an I/O error occurs.
     * @throws      TestQuestionsNotFound if test questions are not found.
     */
    public Set<TestQuestionFromGoogleDocs> TestQuestionsReader(
            String spreadsheetURL,
            int sheetNumber
    ) throws GeneralSecurityException, IOException, TestQuestionsNotFound {
        Sheets service = getSheetService();

        String titleSheetName = getTitleSheetName(spreadsheetURL, sheetNumber, service);

        final String range = titleSheetName + rangeForTestsAnswers;
        ValueRange request = service.spreadsheets().values()
                .get(extractSpreadsheetID(spreadsheetURL), range)
                .execute();

        List<List<Object>> values = request.getValues();
        Set<TestQuestionFromGoogleDocs> tempQuestionList = new HashSet<>();

        TestQuestionFromGoogleDocs testQuestionFromGoogleDocs = null;
        for (List<Object> row : values) {
            if (!row.isEmpty()) {
                /*
                  We have questions in the zero column, answers in the first columns
                  and notes about the correctness of the answers in the second columns
                */
                for (int i = 0; i < row.size(); i++) {
                    if(i == 0 && !row.get(i).toString().isBlank() ){
                        if (testQuestionFromGoogleDocs != null) {
                            tempQuestionList.add(testQuestionFromGoogleDocs);
                        }
                        testQuestionFromGoogleDocs = new TestQuestionFromGoogleDocs();
                        testQuestionFromGoogleDocs.setQuestion(row.get(i).toString());
                    }

                    if(i == 1 && !row.get(i).toString().isBlank() ){
                        testQuestionFromGoogleDocs.getOptions().add(row.get(1).toString());
                    }
                    if(i == 2 && !row.get(i).toString().isBlank() ){
                        testQuestionFromGoogleDocs.getCorrectAnswers().add(row.get(1).toString());
                    }
                }
            } else {
                //empty line, then copying the test questions is considered completed
                break;
            }
        }

        if (testQuestionFromGoogleDocs == null) {
            LOGGER.error("No test questions");
            throw new TestQuestionsNotFound("No test questions");
        } else {
            if(!tempQuestionList.contains(testQuestionFromGoogleDocs)){
                tempQuestionList.add(testQuestionFromGoogleDocs);
            }
            return tempQuestionList;
        }
    }


    public List<Lesson> lessonReader(
            String spreadsheetURL,
            int sheetNumber
    ) throws GeneralSecurityException, IOException, TestQuestionsNotFound {
        Sheets service = getSheetService();

        String titleSheetName = getTitleSheetName(spreadsheetURL, sheetNumber, service);

        final String range = titleSheetName + rangeForCourse;
        ValueRange request = service.spreadsheets().values()
                .get(extractSpreadsheetID(spreadsheetURL), range)
                .execute();

        List<List<Object>> values = request.getValues();
        List<Lesson> lessonList = new ArrayList<>();
        Lesson lesson = new Lesson();
        lesson.setSpreadsheetID(extractSpreadsheetID(spreadsheetURL));

        for (List<Object> row : values) {
            if (row.contains("Lesson")) {
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).equals("Lesson")) {
                        i++;
                        lesson.setName(row.get(i).toString());
                    }
                }
            } else if (row.contains("Description")) {
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).equals("Description")) {
                        i++;
                        lesson.setDescriptionUrl(row.get(i).toString());
                    }
                }
            } else if (row.contains("Video")) {
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).equals("Video")) {
                        i++;
                        lesson.setVideoUrl(row.get(i).toString());
                    }
                }
                lessonList.add(lesson);

            } else if (row.isEmpty()) {
                lesson = new Lesson();
                lesson.setSpreadsheetID(extractSpreadsheetID(spreadsheetURL));
            }
        }

        var tasks = taskReader(spreadsheetURL, sheetNumber);
        var tests = testReader(spreadsheetURL, sheetNumber);

        for (int i = 0; i < lessonList.size(); i++) {
            lessonList.get(i).setTasks(Set.copyOf(tasks.get(i)));
            lessonList.get(i).setTests(Set.copyOf(tests.get(i)));
        }
        return lessonList;
    }

    /**
     * Extracts the document ID from the URL.
     *
     * @param url   the URL of the document.
     * @return      the document ID.
     */
    public static String extractDocumentId(String url) {
        if (url.contains(urlForExtractDocumentId)) {
            String idWithEditSuffix = url.replaceAll(urlForExtractDocumentId, "");
            return idWithEditSuffix.split("/")[0];
        } else {
            return null;
        }
    }

    /**
     * Extracts the spreadsheet ID from the URL.
     *
     * @param url   the URL of the spreadsheet.
     * @return      the spreadsheet id.
     */
    public static String extractSpreadsheetID(String url) {
        if (url == null) {
            return null;
        }

        if (urlForExtractSpreadsheetsId == null) {
            throw new IllegalArgumentException("Variable urlForExtractSpreadsheetsId is null.");
        }

        if (url.contains(urlForExtractSpreadsheetsId)) {
            String idWithEditSuffix = url.replaceAll(urlForExtractSpreadsheetsId, "");
            return idWithEditSuffix.split("/")[0];
        } else {
            return null;
        }
    }

    /**
     * Converts a date string to a LocalDate object.
     *
     * @param dateString    the date string in "yyyy-MM-dd" format.
     * @return              the LocalDate object.
     */
    private static LocalDate stringToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.from(formatter.parse(dateString));
    }

    /**
     * Gets the Sheets service object.
     *
     * @return  a Sheets service object.
     * @throws  GeneralSecurityException if there is a security issue.
     * @throws  IOException if an I/O error occurs.
     */
    private static Sheets getSheetService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, reloadCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Gets the Docs service object.
     *
     * @return  a Docs service object.
     * @throws  GeneralSecurityException if there is a security issue.
     * @throws  IOException if an I/O error occurs.
     */
    private static Docs getDocService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, reloadCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Retrieves the title of the sheet.
     *
     * @param   spreadsheetURL the URL of the spreadsheet.
     * @param   sheetNumber the sheet number to read.
     * @param   service the Sheets service object.
     * @return  the title of the sheet.
     * @throws  IOException if an I/O error occurs.
     */
    private static String getTitleSheetName(String spreadsheetURL, int sheetNumber, Sheets service) throws
            IOException {
        String spreadsheetId = extractSpreadsheetID(spreadsheetURL);
        Spreadsheet response = service.spreadsheets().get(spreadsheetId).setIncludeGridData(false)
                .execute();
        return response.getSheets().get(sheetNumber).getProperties().getTitle();
    }

    /**
     * Getter for current user id key
     *
     * @return  user id key
     */
    public static String getUserIdKey() {
        return userIdKey;
    }

}