package be.sandervl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Site.
 */
@Entity
@Table(name = "site")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Site implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "seed")
    private String seed;

    @OneToMany(mappedBy = "site")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Document> pages = new HashSet<>();

    @OneToMany(mappedBy = "site")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Selector> selectors = new HashSet<>();

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

    public Site name(String name) {
        this.name = name;
        return this;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Site regex(String regex) {
        this.regex = regex;
        return this;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public Site seed(String seed) {
        this.seed = seed;
        return this;
    }

    public Set<Document> getPages() {
        return pages;
    }

    public void setPages(Set<Document> documents) {
        this.pages = documents;
    }

    public Site pages(Set<Document> documents) {
        this.pages = documents;
        return this;
    }

    public Site addPages(Document document) {
        this.pages.add(document);
        document.setSite(this);
        return this;
    }

    public Site removePages(Document document) {
        this.pages.remove(document);
        document.setSite(null);
        return this;
    }

    public Set<Selector> getSelectors() {
        return selectors;
    }

    public void setSelectors(Set<Selector> selectors) {
        this.selectors = selectors;
    }

    public Site selectors(Set<Selector> selectors) {
        this.selectors = selectors;
        return this;
    }

    public Site addSelectors(Selector selector) {
        this.selectors.add(selector);
        selector.setSite(this);
        return this;
    }

    public Site removeSelectors(Selector selector) {
        this.selectors.remove(selector);
        selector.setSite(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Site site = (Site) o;
        if (site.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), site.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Site{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", regex='" + getRegex() + "'" +
            ", seed='" + getSeed() + "'" +
            "}";
    }
}
