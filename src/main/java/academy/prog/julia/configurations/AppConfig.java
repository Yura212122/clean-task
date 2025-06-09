package academy.prog.julia.configurations;

import academy.prog.julia.model.Invite;
import academy.prog.julia.model.UserRole;
import academy.prog.julia.repos.InviteRepository;
import academy.prog.julia.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CommandLineRunner runner(
            InviteRepository inviteRepository,
            UserRepository userRepository
    ) {
        return args -> {
            // Создаем новый инвайт для роли ADMIN
            var invite = Invite.createNewOf(UserRole.ADMIN, 30, 5);
            invite.setDestinationType(Invite.DESTINATION_GROUP);
            invite.setDestination("ProgAcademy");

            // Сохраняем инвайт в репозитории (базе)
            inviteRepository.save(invite);

            // Выводим код в консоль, чтобы точно увидеть
            System.out.println("\n\n===== INVITE CODE: " + invite.getCode() + " =====\n\n");

            // (Опционально) пример запроса пользователя по email
            userRepository.findByEmailLike("qqq@bbb.com");
        };
    }
}
