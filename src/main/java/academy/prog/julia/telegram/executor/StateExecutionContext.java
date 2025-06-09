package academy.prog.julia.telegram.executor;

import academy.prog.julia.model.User;
import academy.prog.julia.services.*;
import academy.prog.julia.telegram.MainBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Context class for managing state and interactions within the bot.
 * It holds references to services, user information, and the current update.
 */
public class StateExecutionContext {

    private final MainBot mainBot;
    private final UserService userService;
    private final CertificateService certificateService;
    private final GroupService groupService;
    private final DocsSheetsService docsSheetsService;
    private final TaskService taskService;
    private final long chatId;
    private final String message;
    private final String[] args;
    private final User user;
    private final Update update;

    // Flag indicating whether the current state execution is finished
    private boolean finished;

    private CommandRegistry commandRegistry;
    private ActiveCommand activeCommand;

    /**
     * Constructor for initializing the state execution context with provided services and parameters.
     *
     * @param mainBot              bot instance for sending messages
     * @param userService          service for managing users
     * @param certificateService   service for managing certificates
     * @param groupService         service for managing user groups
     * @param docsSheetsService    service for managing documents and sheets
     * @param taskService          service for managing tasks
     * @param user                 user information associated with the current state
     * @param chatId               chat ID for communication
     * @param message              message to be sent or received
     * @param args                 command arguments
     * @param update               update object from Telegram API
     */
    public StateExecutionContext(
            MainBot mainBot,
            UserService userService,
            CertificateService certificateService,
            GroupService groupService,
            DocsSheetsService docsSheetsService,
            TaskService taskService, User user,
            long chatId,
            String message,
            String[] args,
            Update update
    ) {
        this.mainBot = mainBot;
        this.userService = userService;
        this.certificateService = certificateService;
        this.groupService = groupService;
        this.docsSheetsService = docsSheetsService;
        this.taskService = taskService;
        this.user = user;
        this.chatId = chatId;
        this.message = message;
        this.args = args;
        this.update = update;
    }

    /**
     * Sends a message to the current chat.
     *
     * @param message the message to be sent
     */
    public void sendMessage(String message) {
        mainBot.sendMessage(chatId, message);
    }

    /**
     * Sends a message to a specified chat ID.
     *
     * @param chatId  the chat ID to send the message to
     * @param message the message to be sent
     */
    public void sendMessage(
            long chatId,
            String message
    ) {
        mainBot.sendMessage(chatId, message);
    }

    /**
     * Sends a message to a chat ID represented as a string.
     *
     * @param chatId  the chat ID (as a string) to send the message to
     * @param message the message to be sent
     */
    public void sendMessage(
            String chatId,
            String message
    ) {
        var id = Long.parseLong(chatId);
        mainBot.sendMessage(id, message);
    }

    /**
     * Gets the chat ID for communication.
     *
     * @return the chat ID
     */
    public long getChatId() {
        return chatId;
    }

    /**
     * Gets the count of command arguments, excluding the command itself.
     *
     * @return the number of arguments
     */
    public int getArgCount() {
        return args.length - 1; // without command
    }

    /**
     * Gets the command from the arguments.
     *
     * @return the command, or null if there are no arguments
     */
    public String getCommand() {
        if (args == null || args.length == 0) {
            return null;
        }

        return getArg(0);
    }

    /**
     * Gets an argument by its index.
     *
     * @param index the index of the argument
     * @return the argument value
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public String getArg(int index) {
        if (args == null || index < 0 || index >= args.length) {
            throw new IndexOutOfBoundsException("Wrong command argument index: " + index);
        }

        return args[index];
    }

    /**
     * Puts an attribute into the active command's attributes map.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     */
    public void putAttribute(
            String name,
            Object value
    ) {
        getActiveCommand().getAttributes().put(name, value);
    }

    /**
     * Gets an attribute value as a String.
     *
     * @param name the name of the attribute
     * @return the attribute value as a String
     */
    public String getAttributeAsString(String name) {
        return (String) getActiveCommand().getAttributes().get(name);
    }

    /**
     * Gets an attribute value as an Integer.
     *
     * @param name the name of the attribute
     * @return the attribute value as an Integer
     */
    public Integer getAttributeAsInt(String name) {
        return (Integer) getActiveCommand().getAttributes().get(name);
    }

    /**
     * Gets an attribute value as a specified type.
     *
     * @param name the name of the attribute
     * @param <T>  the type of the attribute value
     * @return the attribute value as the specified type
     */
    public <T> T getAttribute(String name) {
        return (T) getActiveCommand().getAttributes().get(name);
    }

    /**
     * Gets the user service.
     *
     * @return the user service
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Gets the certificate service.
     *
     * @return the certificate service
     */
    public CertificateService getCertificateService() {
        return certificateService;
    }

    /**
     * Gets the group service.
     *
     * @return the group service
     */
    public GroupService getGroupService() {
        return groupService;
    }

    /**
     * Gets the documents and sheets service.
     *
     * @return the documents and sheets service
     */
    public DocsSheetsService getDocsSheetsService() {
        return docsSheetsService;
    }

    /**
     * Gets the task service.
     *
     * @return the task service
     */
    public TaskService getTaskService() {
        return taskService;
    }

    /**
     * Gets the message for the current update.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the user associated with the current state.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the currently active command.
     *
     * @return the active command
     */
    public ActiveCommand getActiveCommand() {
        return activeCommand;
    }

    /**
     * Sets the currently active command.
     *
     * @param activeCommand the active command to set
     */
    public void setActiveCommand(ActiveCommand activeCommand) {
        this.activeCommand = activeCommand;
    }

    /**
     * Gets the command registry.
     *
     * @return the command registry
     */
    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    /**
     * Sets the command registry.
     *
     * @param commandRegistry the command registry to set
     */
    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    /**
     * Sends a message with text and optional markup, including a link button.
     *
     * @param message             the message to send
     * @param actualLink          the link text and URL for the button
     * @param markdown            the parsing mode for the message (e.g., Markdown)
     * @param replyKeyboardMarkup the reply keyboard markup to include (can be null)
     *
     * Not in use now
     */
    public void sendTextWithMarkup(
            String message,
            String actualLink,
            String markdown,
            ReplyKeyboardMarkup replyKeyboardMarkup
    ) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        sendMessage.setParseMode(markdown);

        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        } else {
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
            sendMessage.setReplyMarkup(replyKeyboardRemove);
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton linkButton = new InlineKeyboardButton();
        linkButton.setText(actualLink);
        linkButton.setUrl(actualLink);

        row.add(linkButton);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            mainBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the update object from the Telegram API.
     *
     * @return the update
     */
    public Update getUpdate() {
        return update;
    }

    /**
     * Checks if the current state execution is finished.
     *
     * @return true if finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the finished status of the current state execution.
     *
     * @param finished true to mark as finished, false otherwise
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}
