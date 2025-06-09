package academy.prog.julia.telegram;

import academy.prog.julia.model.User;
import academy.prog.julia.services.*;
import academy.prog.julia.telegram.executor.CommandExecutor;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MainBot is a Spring-managed component and a custom Telegram bot implementation that handles interactions with
 * users, processes commands, and sends messages.
 * It extends TelegramLongPollingBot to receive updates via long polling.
 */
@Component
public class MainBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(MainBot.class);

    private static final String START_COMMAND = "/start";

    private final UserService userService;
    private final CertificateService certificateService;
    private final GroupService groupService;
    private final DocsSheetsService docsSheetsService;
    private final TaskService taskService;
    private final BotCredentials botCredentials;
    private final CommandExecutor commandExecutor;
    private final SseService sseService;

    /**
     * Constructor to initialize the bot with necessary services and credentials.
     * It also registers the bot with the Telegram API.
     *
     * @param telegramBotsApi the Telegram API instance to register the bot.
     * @param userService the service for user-related operations.
     * @param certificateService the service for certificate-related operations.
     * @param groupService the service for group-related operations.
     * @param docsSheetsService the service for document and sheets operations.
     * @param taskService the service for task-related operations.
     * @param botCredentials the bot credentials (name and token).
     * @param commandExecutor the executor responsible for handling bot commands.
     * @param sseService the service for server-sent events.
     */
    public MainBot(
            TelegramBotsApi telegramBotsApi,
            UserService userService,
            CertificateService certificateService,
            GroupService groupService,
            DocsSheetsService docsSheetsService,
            TaskService taskService,
            BotCredentials botCredentials,
            CommandExecutor commandExecutor,
            SseService sseService
    ) {
        super(botCredentials.getBotToken());

        this.userService = userService;
        this.certificateService = certificateService;
        this.groupService = groupService;
        this.docsSheetsService = docsSheetsService;
        this.taskService = taskService;
        this.botCredentials = botCredentials;
        this.commandExecutor = commandExecutor;
        this.sseService = sseService;

        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Handles incoming updates from the Telegram API.
     * If a message is received, it processes the text or command, or delegates to appropriate methods
     * based on the type of message.
     *
     * @param update the update received from Telegram.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        var message = update.getMessage();
        var chatId = message.getChatId();
        LOGGER.debug("Message received: {}", message.getText());
        LOGGER.info("chatId: {}", chatId);

        if (message.hasText()) {
            var text = message.getText();
            var args = text.split(" ");

            if (START_COMMAND.equals(args[0])) {
                if (args.length > 1)
                    handleDeepLink(chatId, args[1]);
                else
                    sendWelcomeMessage(chatId);
                    sseService.notifyFrontend("The user clicks Start");
            } else {
                handleCommand(chatId, text, args, update);
            }
        } else if (message.hasDocument()) {
            handleCommand(chatId, null, null, update);
        } else {
            sendUnknownCommandMessage(chatId);
        }
    }

    /**
     * Handles commands received from users. It first checks if the user is registered
     * and not blocked, then creates an execution context and delegates the command
     * execution to the CommandExecutor.
     *
     * @param chatId the ID of the chat where the command was issued.
     * @param text the text of the command.
     * @param args the arguments passed with the command.
     * @param update the update object containing the message details.
     */
    private void handleCommand(
            long chatId,
            String text,
            String[] args,
            Update update
    ) {
        Optional<User> userOpt = userService.findUserByChatId(Long.toString(chatId));

        if (userOpt.isEmpty()) {
            sendMessage(chatId, "No user registered to execute the command", getCommandKeyboard());
            LOGGER.info("{}: No user registered to execute the command", chatId);
            return;
        }

        User user = userOpt.get();

        if (userService.isUserBlocked(user.getId())) {
            sendMessage(chatId, "Your Account is Blocked", getCommandKeyboard());
            LOGGER.info("{}: Account is Blocked", user);
            return;
        }

        StateExecutionContext context = new StateExecutionContext(
                this,
                userService,
                certificateService,
                groupService,
                docsSheetsService,
                taskService,
                user,
                chatId,
                text,
                args,
                update
        );

        commandExecutor.execute(context);
    }

    /**
     * Handles deep links sent with the /start command. If the deep link matches a user,
     * it updates the user's chat ID and sends a welcome message.
     *
     * @param chatId the ID of the chat.
     * @param deepLink the deep link parameter.
     */
    private void handleDeepLink(
            long chatId,
            String deepLink
    ) {
        try {
            userService.updateUserChatId(deepLink, Long.toString(chatId));
            sendMessage(chatId, "Welcome to Prog Academy!", getCommandKeyboard());
        } catch (JuliaRuntimeException ex) {
            sendMessage(chatId, ex.getMessage(), getCommandKeyboard());
            LOGGER.error(ex);
        }
    }

    /**
     * Sends a welcome message to the user.
     *
     * @param chatId the ID of the chat.
     */
    private void sendWelcomeMessage(long chatId) {
        var responseText = "Welcome to Prog Academy bot! Please request invitation link from our staff.";
        sendMessage(chatId, responseText, getCommandKeyboard());
    }

    /**
     * Sends a message to the specified chat without a keyboard markup.
     * This is a convenience method that calls the overloaded sendMessage method.
     *
     * @param chatId the ID of the chat to send the message to.
     * @param text the message text.
     */
    public void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    /**
     * Sends a message to the specified chat with optional keyboard markup.
     *
     * @param chatId the ID of the chat.
     * @param text the message text.
     * @param keyboardMarkup the keyboard markup to include (optional).
     */
    public void sendMessage(
            long chatId,
            String text,
            ReplyKeyboardMarkup keyboardMarkup
    ) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        if (keyboardMarkup != null) {
            message.setReplyMarkup(keyboardMarkup);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Creates a custom reply keyboard for the bot's commands.
     *
     * @return a configured ReplyKeyboardMarkup.
     */
    private ReplyKeyboardMarkup getCommandKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/help"));
        row1.add(new KeyboardButton("/exit"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/group_list"));
        row2.add(new KeyboardButton("/users_list"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("/users_list_web"));
        row3.add(new KeyboardButton("/web"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    /**
     * Retrieves the bot's username.
     *
     * @return the bot's name as configured in BotCredentials.
     */
    @Override
    public String getBotUsername() {
        return botCredentials.getBotName();
    }

    /**
     * Sends a message when an unknown command is received.
     *
     * @param chatId the ID of the chat.
     */
    private void sendUnknownCommandMessage(long chatId) {
        sendMessage(chatId, "Unknown command. Please try again.", getCommandKeyboard());
    }

}