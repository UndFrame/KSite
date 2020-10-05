package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Token;

@Component
public interface TokenDao extends JpaRepository<Token, Long> {

    Token findByToken(String token);
    void deleteById(long id);
}
