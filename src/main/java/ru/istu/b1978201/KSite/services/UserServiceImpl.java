package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.RoleDao;
import ru.istu.b1978201.KSite.dao.TokenDao;
import ru.istu.b1978201.KSite.dao.UserDao;
import ru.istu.b1978201.KSite.mode.Role;
import ru.istu.b1978201.KSite.mode.Token;
import ru.istu.b1978201.KSite.mode.User;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Сервия для работы с данными пользоватея
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;


    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setTokenDao(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void save(User user) {
        userDao.save(user);
    }

    @Override
    public User findByUsername(String name) {
        return userDao.findByUsername(name.toLowerCase());
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email.toLowerCase());
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    public boolean createUser(User user) {
        User byEmail = findByEmail(user.getEmail());
        User byUsername = findByUsername(user.getUsername());
        if (byEmail != null || byUsername != null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roleDao.findById(1L).ifPresent(roles::add);
        user.setRoles(roles);
        Token token = new Token();
        user.setToken(token);
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setDate(new Date());
        user.setBan(false);
        user.setEnabled(false);
        save(user);
        tokenDao.save(token);
        mailService.sendRegisterToken(user);
        return true;
    }

    @Override
    public boolean activateUser(String tokenId) {
        Token token = tokenDao.findByToken(tokenId);
        if(token==null){
            return false;
        }
        User user = userDao.findByToken(token);
        if (user == null) {
            return false;
        }
        user.getToken().setUser(null);
        user.setToken(null);
        user.setEnabled(true);
        save(user);
        tokenDao.deleteById(token.getId());
        return true;
    }

    @Override
    public void refreshUserLikeDislike(User user) {
         userDao.findById(user.getId()).ifPresent(newUser -> user.setLikeDislikes(newUser.getLikeDislikes()));

    }

}
