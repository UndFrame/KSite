package ru.istu.b1978201.KSite.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.istu.b1978201.KSite.dao.RoleDao;
import ru.istu.b1978201.KSite.dao.TokenDao;
import ru.istu.b1978201.KSite.dao.UserDao;
import ru.istu.b1978201.KSite.mode.Role;
import ru.istu.b1978201.KSite.mode.Token;
import ru.istu.b1978201.KSite.mode.User;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplTest {


    @Autowired
    public UserDao userDao;
    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private RoleDao roleDao;

    private static final String name1 = "UserNameForTest";
    private static final String email1 = "email@email.com";


    private static final String name2 = "UserNameForTest2";
    private static final String email2 = "email@email.com2";

    @Before
    public void setup(){

        Optional.ofNullable(userDao.findByUsername(name1)).map(User::getToken)
                .ifPresent(token ->tokenDao.delete(tokenDao.findByToken(token.getToken())));

        Optional.ofNullable(userDao.findByUsername(name2)).map(User::getToken)
                .ifPresent(token ->tokenDao.delete(tokenDao.findByToken(token.getToken())));


        userDao.removeByUsername(name1);
        userDao.removeByUsername(name2);


        System.out.println(tokenDao==null);

    }

    @Test

    void save() {

        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao);
        userService.setTokenDao(tokenDao);

        Role defaultRole = new Role();
        defaultRole.setName("USER");
        defaultRole.setId(1L);
        User user = new User();
        user.setRoles(Collections.singleton(defaultRole));
        user.setEmail(email1);
        user.setUsername(name1);
        user.setPassword("password");
        Token token = new Token();
        user.setToken(token);
        token.setUser(user);
        token.setToken(token.toString());
        token.setDate(new Date());
        user.setBan(false);
        user.setEnabled(false);
        userDao.save(user);
        Assert.assertNotNull(userDao.findByUsername(name1));
    }

    @Test
    void findByUsername() {

        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao);
        userService.setTokenDao(tokenDao);

        Assert.assertNotNull(userService.findByUsername(name1));

    }

    @Test
    @Order(3)
    void findByEmail() {
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao);
        userService.setTokenDao(tokenDao);

        Assert.assertNotNull(userService.findByEmail(email1));


    }

    void createUser() {

        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao);
        userService.setTokenDao(tokenDao);
        userService.setPasswordEncoder(new SCryptPasswordEncoder());
        userService.setRoleDao(roleDao);
        userService.setMailService(new MailService());

        User user = new User();
        user.setEmail(email2);
        user.setUsername(name2);
        user.setPassword("password");

        userService.createUser(user);
        Assert.assertNotNull(userDao.findByUsername(name2));
        System.out.println("1");

    }

    @Test
    void activateUser() {

        createUser();

        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao);
        userService.setTokenDao(tokenDao);

        User byUsername = userDao.findByUsername(name2);

        System.out.println("||| " + byUsername);

        String token = byUsername.getToken().getToken();
        Assert.assertNotNull(tokenDao.findByToken(token));
        userService.activateUser(token);
        Assert.assertNull(tokenDao.findByToken(token));
    }

}