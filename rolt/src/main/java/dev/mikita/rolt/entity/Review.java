package dev.mikita.rolt.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The type Review.
 */
@Entity
@Table(name = "rolt_review",
        uniqueConstraints = {
                @UniqueConstraint(name="unique_author_contract", columnNames = {"author_id", "contract_id"})
})
@NamedQueries({
        @NamedQuery(name = "Review.findByContractAndAuthor", query = "SELECT r from Review r WHERE r.contract = :contract AND r.author = :author")
})
public class Review implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "created_on", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "updated_on", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Consumer author;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PublicationStatus status = PublicationStatus.PUBLISHED;

    @Column(name = "rating", nullable = false)
    private Integer rating;

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
     * Gets contract.
     *
     * @return the contract
     */
    public Contract getContract() {
        return contract;
    }

    /**
     * Sets contract.
     *
     * @param contract the contract
     */
    public void setContract(Contract contract) {
        Objects.requireNonNull(contract);
        this.contract = contract;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public Consumer getAuthor() {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     */
    public void setAuthor(Consumer author) {
        Objects.requireNonNull(author);
        this.author = author;
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
            throw new RuntimeException("The update date must be after the creation date.");
        }

        this.updatedOn = updatedOn;
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
     * Gets rating.
     *
     * @return the rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Sets rating.
     *
     * @param rating the rating
     */
    public void setRating(Integer rating) {
        Objects.requireNonNull(rating);

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("The rating value should be between 1 and 5.");
        }

        this.rating = rating;
    }

    /**
     * Pre persist.
     */
    @PrePersist
    public void prePersist() {
        if (!author.equals(contract.getTenant()) && !author.equals(contract.getProperty().getOwner())) {
            throw new RuntimeException("The user has no right to leave feedback for this contract");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                ", contract=" + contract +
                ", author=" + author +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", rating=" + rating +
                '}';
    }
}
