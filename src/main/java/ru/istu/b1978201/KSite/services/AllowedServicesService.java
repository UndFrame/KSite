package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.ServicesDao;
import ru.istu.b1978201.KSite.mode.Services;

import java.util.Optional;

@Service
public class AllowedServicesService {

    @Autowired
    private ServicesDao servicesDao;

    public Optional<Services> allowedService(long id){
        return Optional.of(servicesDao.findFirstById(id));
    }

    public Optional<Services> allowedService(String name){
        return Optional.of(servicesDao.findFirstByName(name));

    }



}
