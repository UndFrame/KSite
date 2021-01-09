package ru.istu.b1978201.KSite.mode;

import javax.persistence.*;
import java.util.Objects;
/**
 * Объект - data(хранилище данных), который хранит в себе информацию о лайках/дизлайках в статье
 * В классе описана структуры таблицы хранящейся в базе данных
 */
@Entity
@Table(name = "like_dislike")
public class LikeDislike {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "article")
    private Article article;

    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "likes")
    private boolean like;

    @Column(name = "dislike")
    private boolean dislike;

    public LikeDislike() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
        this.dislike = !like;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
        this.like= !dislike;
    }

    public void clear(){
        this.like = false;
        this.dislike = false;
    }

    public boolean isClear(){
        return !this.like && !this.dislike;
    }

    @Override
    public String toString() {
        return "LikeDislike{" +
                "id=" + id +
                ", isLike='" + like + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeDislike role = (LikeDislike) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
