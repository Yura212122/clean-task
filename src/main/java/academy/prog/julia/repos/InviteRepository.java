package academy.prog.julia.repos;

import academy.prog.julia.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> { //набір стандартних методів JPA для роботи з БД,
    //в нашому випадку з таблицею Запрошень
    /**
     * Finds an invite by its unique code.
     *
     * @param code the code of the invite to search for
     * @return an {@code Optional<Invite>} containing the invite if found, or empty if not found
     */
    Optional<Invite> findByCode(String code); //метод для пошуку Запрошень по коду, на вхід отримуємо код у вигляді строки, на
    //виході об'єкт типу Optional<Invite>, що дозволяє значення null (не ловимо NullPointerException)
}
