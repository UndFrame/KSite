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


    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER)
    private Collection<Comment> comment;

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
        if(comment==null)
            setComment(new ArrayList<>());
        return comment;
    }

    public void setComment(Collection<Comment> comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", text='" + text + '\'' +
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
