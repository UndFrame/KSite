package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.AccessServiceDao;
import ru.istu.b1978201.KSite.mode.AllowedService;

import java.util.Optional;

@Service
public class AllowedServicesService {

    @Autowired
    private AccessServiceDao servicesDao;

    public Optional<AllowedService> allowedService(long id){
        return Optional.of(servicesDao.findFirstById(id));
    }

    public Optional<AllowedService> allowedService(String name){
        return Optional.ofNullable(servicesDao.findFirstByName(name));

    }



}
