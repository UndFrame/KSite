package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Services;

@Component

public interface ServicesDao extends JpaRepository<Services, Long> {

    Services findFirstById(long id);
    Services findFirstByName(String name);

}
