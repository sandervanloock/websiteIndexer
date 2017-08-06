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
 * A Selector.
 */
@Entity
@Table(name = "selector")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Selector implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "jhi_value", nullable = false)
    private String value;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "attribute")
    private String attribute;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @ManyToOne(optional = false)
    @NotNull
    private Site site;

    @OneToMany(mappedBy = "children")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Selector> children = new HashSet<>();

    @ManyToOne
    private Selector parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Selector value(String value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Selector name(String name) {
        this.name = name;
        return this;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Selector attribute(String attribute) {
        this.attribute = attribute;
        return this;
    }

    public Boolean isIsPrimary() {
        return isPrimary;
    }

    public Selector isPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Selector site(Site site) {
        this.site = site;
        return this;
    }

    public Set<Selector> getChildren() {
        return children;
    }

    public void setChildren(Set<Selector> selectors) {
        this.children = selectors;
    }

    public Selector children(Set<Selector> selectors) {
        this.children = selectors;
        return this;
    }

    public Selector addChildren(Selector selector) {
        this.children.add(selector);
        return this;
    }

    public Selector removeChildren(Selector selector) {
        this.children.remove(selector);
        selector.setChildren(null);
        return this;
    }

    public Selector getParent() {
        return parent;
    }

    public void setParent(Selector selector) {
        this.parent = selector;
    }

    public Selector parent(Selector selector) {
        this.parent = selector;
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
        Selector selector = (Selector) o;
        if (selector.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), selector.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Selector{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", name='" + getName() + "'" +
            ", attribute='" + getAttribute() + "'" +
            ", isPrimary='" + isIsPrimary() + "'" +
            "}";
    }
}
