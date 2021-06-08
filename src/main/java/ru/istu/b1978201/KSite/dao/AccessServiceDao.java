package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.istu.b1978201.KSite.mode.AllowedService;

import java.util.List;

public interface AccessServiceDao extends JpaRepository<AllowedService, Long> {

    List<AllowedService> findAll();

    AllowedService findFirstById(long id);

    AllowedService findFirstByName(String name);

}
