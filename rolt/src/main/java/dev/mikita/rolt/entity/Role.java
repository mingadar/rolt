package dev.mikita.rolt.entity;

/**
 * The enum Role.
 */
public enum Role {
    /**
     * Admin role.
     */
    ADMIN("ROLE_ADMIN"),
    /**
     * Moderator role.
     */
    MODERATOR("ROLE_MODERATOR"),
    /**
     * Tenant role.
     */
    TENANT("ROLE_TENANT"),
    /**
     * Landlord role.
     */
    LANDLORD("ROLE_LANDLORD"),
    /**
     * Guest role.
     */
    GUEST("ROLE_GUEST");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
