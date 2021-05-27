package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.AccessServiceDao;
import ru.istu.b1978201.KSite.mode.AllowedService;

@Service
public class AccessServices {

    @Autowired
    private AccessServiceDao accessServiceDao;

    public boolean isAccessService(String name){
        return accessServiceDao.findFirstByName(name)!=null;
    }

    public void registerService(String name, String company, String lore) {
        AllowedService services = new AllowedService();
        services.setName(name);
        services.setCompany(company);
        services.setLore(lore);

        accessServiceDao.save(services);
    }

}
