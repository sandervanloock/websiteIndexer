package be.sandervl.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Attribute.
 */
@Entity
@Table(name = "attribute")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_value")
    private String value;

    @ManyToOne(optional = false)
    @NotNull
    private Document document;

    @ManyToOne(optional = false)
    @NotNull
    private Selector selector;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "attribute_relatives",
        joinColumns = @JoinColumn(name = "attributes_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "relatives_id", referencedColumnName = "id"))
    private Set<Attribute> relatives = new HashSet<>();

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

    public Attribute value(String value) {
        this.value = value;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Attribute document(Document document) {
        this.document = document;
        return this;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Attribute selector(Selector selector) {
        this.selector = selector;
        return this;
    }

    public Set<Attribute> getRelatives() {
        return relatives;
    }

    public void setRelatives(Set<Attribute> attributes) {
        this.relatives = attributes;
    }

    public Attribute relatives(Set<Attribute> attributes) {
        this.relatives = attributes;
        return this;
    }

    public Attribute addRelatives(Attribute attribute) {
        this.relatives.add(attribute);
        attribute.getRelatives().add(this);
        return this;
    }

    public Attribute removeRelatives(Attribute attribute) {
        this.relatives.remove(attribute);
        attribute.getRelatives().remove(this);
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
        Attribute attribute = (Attribute) o;
        if (attribute.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), attribute.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Attribute{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            "}";
    }
}
