package dev.mikita.rolt.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The type Property.
 */
@Entity
@Table(name = "rolt_property")
public class Property implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "created_on", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "updated_on", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Landlord owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PublicationStatus status = PublicationStatus.PUBLISHED;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PropertyType type;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "square", nullable = false)
    private Double square;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "postal_code", nullable = false, length = 16)
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "city", nullable = false)
    private City city;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets created on.
     *
     * @return the created on
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets created on.
     *
     * @param createdOn the created on
     */
    public void setCreatedOn(LocalDateTime createdOn) {
        Objects.requireNonNull(createdOn);
        this.createdOn = createdOn;
    }

    /**
     * Gets updated on.
     *
     * @return the updated on
     */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets updated on.
     *
     * @param updatedOn the updated on
     */
    public void setUpdatedOn(LocalDateTime updatedOn) {
        Objects.requireNonNull(updatedOn);

        if (updatedOn.isBefore(createdOn)) {
            throw new RuntimeException("The date of the last login must be later than the creation date.");
        }

        this.updatedOn = updatedOn;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public Landlord getOwner() {
        return owner;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(Landlord owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public PublicationStatus getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(PublicationStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public PropertyType getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(PropertyType type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    /**
     * Gets available.
     *
     * @return the available
     */
    public Boolean getAvailable() {
        return isAvailable;
    }

    /**
     * Sets available.
     *
     * @param available the available
     */
    public void setAvailable(Boolean available) {
        Objects.requireNonNull(available);
        isAvailable = available;
    }

    /**
     * Gets square.
     *
     * @return the square
     */
    public Double getSquare() {
        return square;
    }

    /**
     * Sets square.
     *
     * @param square the square
     */
    public void setSquare(Double square) {
        Objects.requireNonNull(square);
        this.square = square;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    /**
     * Gets street.
     *
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets street.
     *
     * @param street the street
     */
    public void setStreet(String street) {
        Objects.requireNonNull(street);
        this.street = street;
    }

    /**
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        Objects.requireNonNull(postalCode);
        this.postalCode = postalCode;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public City getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(City city) {
        Objects.requireNonNull(city);
        this.city = city;
    }

    /**
     * Pre update.
     */
    @PreUpdate
    public void preUpdate() {
        updatedOn = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        Property property = (Property) o;
        return Objects.equals(id, property.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                ", owner=" + owner +
                ", status=" + status +
                ", type=" + type +
                ", isAvailable=" + isAvailable +
                ", square=" + square +
                ", description='" + description + '\'' +
                ", street='" + street + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city=" + city +
                '}';
    }
}
