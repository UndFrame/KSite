package ru.istu.b1978201.KSite.mode;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "text")
    private String text;
    @Column(name = "description")
    private String description;
    @Column(name = "hash")
    private String hash;
    @Column(name = "likes")
    private int likes;
    @Column(name = "dislikes")
    private int dislikes;
    @Column(name = "icon")
    private String icon;

    @Column(name = "data_create")
    private Date dateCreate;


    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private Collection<Comment> comment;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private Collection<LikeDislike> likeDislikes;


    public Article() {
    }  

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Collection<Comment> getComment() {
        if (comment == null)
            setComment(new ArrayList<>());
        return comment;
    }

    public Collection<LikeDislike> getLikeDislikes() {
        if (likeDislikes == null)
            setLikeDislikes(new ArrayList<>());
        return likeDislikes;
    }

    public void setLikeDislikes(Collection<LikeDislike> likeDislikes) {
        this.likeDislikes = likeDislikes;
    }

    public void setComment(Collection<Comment> comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getIconUrl() {
        return "http://87.250.0.132:8081/files/" + this.getIcon();
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", hash='" + hash + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", icon='" + icon + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article role = (Article) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
