package dev.mikita.rolt.entity;

/**
 * The enum Consumer status.
 */
public enum ConsumerStatus {
    /**
     * Banned consumer status.
     */
    BANNED("BANNED"),
    /**
     * Active consumer status.
     */
    ACTIVE("ACTIVE"),
    /**
     * Deleted consumer status.
     */
    DELETED("DELETED");

    private final String name;

    ConsumerStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
