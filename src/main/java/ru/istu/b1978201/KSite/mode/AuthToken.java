package ru.istu.b1978201.KSite.mode;

import javax.persistence.*;
import java.util.Objects;

/**
 * Объект - data(хранилище данных), который хранит в себе информацию о статье
 * В классе описана структуры таблицы хранящейся в базе данных
 */
@Entity
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "service_id")
    private long serviceId;
    @Column(name = "device_id")
    private long deviceId;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "refresh_token")
    private String refreshToken;


    public AuthToken() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return userId == authToken.userId && serviceId == authToken.serviceId && Objects.equals(id, authToken.id) && Objects.equals(accessToken, authToken.accessToken) && Objects.equals(refreshToken, authToken.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, serviceId, accessToken, refreshToken);
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", serviceId=" + serviceId +
                ", deviceId=" + deviceId +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
