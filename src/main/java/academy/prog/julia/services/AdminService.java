package academy.prog.julia.services;

import academy.prog.julia.exceptions.BadRequestException;
import academy.prog.julia.exceptions.UserNotFoundException;
import academy.prog.julia.model.User;
import academy.prog.julia.telegram.MainBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminService {
    private final MainBot mainBot;
    private static final Logger LOGGER = LogManager.getLogger(AdminService.class);


    public AdminService(MainBot mainBot) {
        this.mainBot = mainBot;
    }

    public void sendMessageByUserEmail(User user, String message) throws BadRequestException {
        if (user == null) {
            throw new UserNotFoundException("User with ths email is not found");
        }
        if (user.getTelegramChatId() == null) {
            LOGGER.error("User with this " + user.getEmail() + " email has no attached telegram.");
            throw new BadRequestException("User with this " + user.getEmail() + " email has no attached telegram.");
        }
        /*  We cut a message for messages up to 4_000 characters,
        since there can be no more than 4_000 characters in the Telegram chat*/
        if (message.length() > 4000) {
            String regex = "(.{1,4000})(?=\\s|$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                mainBot.sendMessage(Long.parseLong(user.getTelegramChatId()), matcher.group());
            }
        } else {
            mainBot.sendMessage(Long.parseLong(user.getTelegramChatId()), message);
        }
    }

    public StringBuilder sendMessageByUserEmail(List<User> users, String message) throws BadRequestException {
        if(users.isEmpty()){
            throw new UserNotFoundException("Users with this role is non-exist.");
        }
        StringBuilder sb = new StringBuilder("Users with this ");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getTelegramChatId() == null) {
                LOGGER.error("User with this " + users.get(i).getEmail() + " email has no attached telegram.");
                sb.append(users.get(i).getEmail())
                        .append(" ");
                continue;
            }
            sendMessageByUserEmail(users.get(i), message);
        }
        return sb;
    }
}