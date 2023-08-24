package dev.mikita.rolt.entity;

/**
 * The enum Property type.
 */
public enum PropertyType {
    /**
     * Apartment property type.
     */
    APARTMENT("APARTMENT"),
    /**
     * House property type.
     */
    HOUSE("HOUSE"),
    /**
     * Room property type.
     */
    ROOM("ROOM");

    private final String name;

    PropertyType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
