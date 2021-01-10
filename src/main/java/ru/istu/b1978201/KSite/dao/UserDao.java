package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Token;
import ru.istu.b1978201.KSite.mode.User;

/**
 * Интерфейс обеспечивает основные операции по поиску, сохранения, удалению данных из таблицы users.
 */
@Component
public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByToken(Token token);

    void removeById(long ld);

    void removeByUsername(String name);
}
