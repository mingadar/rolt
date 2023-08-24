package dev.mikita.rolt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.*;

/**
 * The type Tenant.
 */
@Entity
@Table(name = "rolt_tenant")
@DiscriminatorValue("tenant")
public class Tenant extends Consumer {
    @Column(name = "in_search")
    private Boolean inSearch = false;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Property> favorites;

    /**
     * Instantiates a new Tenant.
     */
    public Tenant() {
        this.role = Role.TENANT;
    }

    /**
     * Gets in search.
     *
     * @return the in search
     */
    public Boolean getInSearch() {
        return inSearch;
    }

    /**
     * Sets in search.
     *
     * @param inSearch the in search
     */
    public void setInSearch(Boolean inSearch) {
        Objects.requireNonNull(inSearch);
        this.inSearch = inSearch;
    }

    /**
     * Gets favorites.
     *
     * @return the favorites
     */
    public Set<Property> getFavorites() {
        return favorites;
    }

    /**
     * Sets favorites.
     *
     * @param favorites the favorites
     */
    public void setFavorites(Set<Property> favorites) {
        Objects.requireNonNull(favorites);
        this.favorites = favorites;
    }

    /**
     * Add favorite.
     *
     * @param property the property
     */
    public void addFavorite(Property property) {
        Objects.requireNonNull(property);

        if (favorites == null) {
            favorites = new HashSet<>();
        }

        final Optional<Property> existing = favorites.stream().filter(p -> p.getId()
                .equals(property.getId())).findAny();

        if (existing.isEmpty()) {
            favorites.add(property);
        }
    }

    /**
     * Remove favorite.
     *
     * @param property the property
     */
    public void removeFavorite(Property property) {
        Objects.requireNonNull(property);
        if (favorites == null) return;

        favorites.remove(property);
    }
}