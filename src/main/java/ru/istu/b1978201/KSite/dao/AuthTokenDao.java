package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.AuthToken;

@Component
public interface AuthTokenDao extends JpaRepository<AuthToken, Long> {

    AuthToken findFirstByUserIdAndServiceIdAndDeviceId(long userId, String serviceId,String deviceId);
    void deleteAllByUserId(long userId);

    void deleteAllByUserIdAndServiceId(long userId, String serviceId);

    void deleteAllByUserIdAndServiceIdAndDeviceId(long userId, String serviceId, String deviceId);


}
