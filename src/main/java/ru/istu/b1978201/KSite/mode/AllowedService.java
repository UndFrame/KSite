package ru.istu.b1978201.KSite.mode;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "services")
public class AllowedService {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "company")
    private String company;


    @Column(name = "lore")
    private String lore;


    public AllowedService() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedService services = (AllowedService) o;
        return Objects.equals(id, services.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
