package ru.istu.b1978201.KSite.services;


import ru.istu.b1978201.KSite.mode.User;

/**
 * Интерфейс сервиса помогающий облегчить работу с пользователем
 */
public interface UserService {

    void save(User user);
    User findByUsername(String name);
    User findByEmail(String name);
    User findById(Long id);

    boolean createUser(User user);

    boolean activateUser(String token);
    void refreshUserLikeDislike(User user);


}
