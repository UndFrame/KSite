package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.istu.b1978201.KSite.mode.Services;

import java.util.List;

public interface AccessServiceDao extends JpaRepository<Services, Long> {

    List<Services> findAll();

    Services findFirstById(long id);

    Services findFirstByName(String name);

}
