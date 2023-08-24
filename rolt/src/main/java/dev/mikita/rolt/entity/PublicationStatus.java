package dev.mikita.rolt.entity;

/**
 * The enum Publication status.
 */
public enum PublicationStatus {
    /**
     * Published publication status.
     */
    PUBLISHED("PUBLISHED"),
    /**
     * Deleted publication status.
     */
    DELETED("DELETED"),
    /**
     * Moderation publication status.
     */
    MODERATION("MODERATION");

    private final String name;

    PublicationStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
