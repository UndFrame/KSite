package ru.istu.b1978201.KSite.dao;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.istu.b1978201.KSite.mode.Role;
import ru.istu.b1978201.KSite.mode.User;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserDaoTest {

    @Autowired
    public UserDao userDao;

    /**
     *Проверка корректной работы метода findByUsername(String name) jpa-интерфейса UserDao
     */
    @Test
    void findByUsername() {
        Assert.assertNotNull(userDao.findByUsername("undframe"));
    }
    /**
     *Проверка корректной работы метода findByEmail(String email) jpa-интерфейса UserDao
     */
    @Test
    void findByEmail() {
        Assert.assertNotNull(userDao.findByEmail("undframe@gmail.com"));
    }
    /**
     *Проверка корректной работы метода removeByUsername(String name) jpa-интерфейса UserDao
     */
    @Test
    void removeByUsername() {

        User testUser = new User();
        testUser.setPassword("password");
        testUser.setUsername("username");
        testUser.setEmail("email@email.mail");
        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setName("USER");
        testUser.setRoles(new HashSet<>(Collections.singleton(testRole)));

        userDao.save(testUser);

        Assert.assertNotNull(userDao.findByEmail(testUser.getEmail()));
        userDao.removeByUsername(testUser.getUsername());
        Assert.assertNull(userDao.findByEmail(testUser.getEmail()));


    }
}