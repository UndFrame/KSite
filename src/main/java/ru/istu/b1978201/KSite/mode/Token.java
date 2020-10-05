package ru.istu.b1978201.KSite.mode;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "date")
    private Date date;

    @OneToOne(mappedBy = "token")
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", tokenId='" + token + '\'' +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}
