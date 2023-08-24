package dev.mikita.rolt.entity;

import dev.mikita.rolt.environment.Generator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantTest {
    @Test
    public void addFavoriteWorksWhenAddingFavoriteForFirstTime() {
        final Tenant tenant = Generator.generateTenant();
        final Property property = Generator.generateProperty();

        property.setId(Generator.randomInt());
        tenant.addFavorite(property);

        assertEquals(1, tenant.getFavorites().size());
    }

    @Test
    public void addFavoriteWorksForTenantWithExistingFavorites() {
        final Tenant tenant = Generator.generateTenant();
        final Property propertyOne = Generator.generateProperty();

        propertyOne.setId(Generator.randomInt());
        tenant.setFavorites(new HashSet<>(Collections.singletonList(propertyOne)));

        final Property propertyTwo = Generator.generateProperty();
        propertyTwo.setId(Generator.randomInt());

        tenant.addFavorite(propertyTwo);
        assertEquals(2, tenant.getFavorites().size());
    }

    @Test
    public void afterAddingExistingFavoriteQuantityDoesNotChange() {
        final Tenant tenant = Generator.generateTenant();
        final Property property = Generator.generateProperty();

        property.setId(Generator.randomInt());
        tenant.addFavorite(property);
        tenant.addFavorite(property);

        assertEquals(1, tenant.getFavorites().size());
    }

    @Test
    public void afterDeletingFavoriteDoesNotExist() {
        final Tenant tenant = Generator.generateTenant();
        final Property property = Generator.generateProperty();

        property.setId(Generator.randomInt());
        tenant.addFavorite(property);
        tenant.removeFavorite(property);

        assertEquals(0, tenant.getFavorites().size());
    }
}
