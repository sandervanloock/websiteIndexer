package be.sandervl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created")
    private ZonedDateTime created;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "document_matches",
        joinColumns = @JoinColumn(name = "documents_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "matches_id", referencedColumnName = "id"))
    private Set<Document> matches = new HashSet<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Site site;

    @OneToMany(mappedBy = "document")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Attribute> attributes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public Document created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Document url(String url) {
        this.url = url;
        return this;
    }

    public Set<Document> getMatches() {
        return matches;
    }

    public void setMatches(Set<Document> documents) {
        this.matches = documents;
    }

    public Document matches(Set<Document> documents) {
        this.matches = documents;
        return this;
    }

    public Document addMatches(Document document) {
        this.matches.add(document);
        document.getMatches().add(this);
        return this;
    }

    public Document removeMatches(Document document) {
        this.matches.remove(document);
        document.getMatches().remove(this);
        return this;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Document site(Site site) {
        this.site = site;
        return this;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Document attributes(Set<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public Document addAttributes(Attribute attribute) {
        this.attributes.add(attribute);
        attribute.setDocument(this);
        return this;
    }

    public Document removeAttributes(Attribute attribute) {
        this.attributes.remove(attribute);
        attribute.setDocument(null);
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
        Document document = (Document) o;
        if (document.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), document.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Document{" +
            "id=" + getId() +
            ", created='" + getCreated() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
