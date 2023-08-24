package dev.mikita.rolt.service;

import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.environment.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class TenantServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TenantService tenantService;

    @Test
    public void addFavorites() {
        final City city = Generator.generateCity();
        em.persist(city);
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);
        em.persist(property);
        final Tenant tenant = Generator.generateTenant();
        em.persist(tenant);
        em.flush();

        tenantService.addFavorite(property, tenant);
        tenantService.update(tenant);

        assertTrue(tenant.getFavorites().contains(property));
        assertEquals(1, tenant.getFavorites().size());
    }

    @Test
    public void blockedTenantSetsBannedStatus() {
        final Tenant tenant = Generator.generateTenant();
        em.persist(tenant);
        em.flush();

        tenantService.block(tenant);
        tenantService.update(tenant);

        assertEquals(ConsumerStatus.BANNED, tenant.getStatus());
    }

    @Test
    public void activeTenantSetsActiveStatus() {
        final Tenant blockedTenant = Generator.generateTenant();
        blockedTenant.setStatus(ConsumerStatus.BANNED);
        em.persist(blockedTenant);
        em.flush();

        tenantService.active(blockedTenant);
        tenantService.update(blockedTenant);

        assertEquals(ConsumerStatus.ACTIVE, blockedTenant.getStatus());
    }

    @Test
    public void removedTenantSetsDeletedStatus() {
        final Tenant tenant = Generator.generateTenant();
        em.persist(tenant);
        em.flush();

        tenantService.remove(tenant);
        tenantService.update(tenant);

        assertEquals(ConsumerStatus.DELETED, tenant.getStatus());
    }

}