package dev.mikita.rolt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.*;

/**
 * The type Landlord.
 */
@Entity
@Table(name = "rolt_landlord")
@DiscriminatorValue("landlord")
public class Landlord extends Consumer {
    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Property> properties;

    /**
     * Instantiates a new Landlord.
     */
    public Landlord() {
        this.role = Role.LANDLORD;
    }

    /**
     * Gets properties.
     *
     * @return the properties
     */
    public Set<Property> getProperties() {
        return properties;
    }

    /**
     * Sets properties.
     *
     * @param properties the properties
     */
    public void setProperties(Set<Property> properties) {
        Objects.requireNonNull(properties);
        this.properties = properties;
    }

    /**
     * Add property.
     *
     * @param property the property
     */
    public void addProperty(Property property) {
        Objects.requireNonNull(property);

        if (properties == null) {
            properties = new HashSet<>();
        }

        properties.add(property);
        property.setOwner(this);
    }

    /**
     * Remove property.
     *
     * @param property the property
     */
    public void removeProperty(Property property) {
        Objects.requireNonNull(property);
        if (properties == null) return;

        properties.remove(property);
    }
}
