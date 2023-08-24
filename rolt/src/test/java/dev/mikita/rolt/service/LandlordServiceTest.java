package dev.mikita.rolt.service;

import dev.mikita.rolt.entity.City;
import dev.mikita.rolt.entity.ConsumerStatus;
import dev.mikita.rolt.entity.Landlord;
import dev.mikita.rolt.entity.Property;
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
public class LandlordServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LandlordService landlordService;

    @Test
    public void addPropertyToLandlord() {
        final City city = Generator.generateCity();
        em.persist(city);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        em.persist(property);

        final Landlord landlord = Generator.generateLandlord();
        landlord.addProperty(property);
        em.persist(landlord);
        em.flush();

        landlordService.update(landlord);

        assertEquals(landlord, property.getOwner());
        assertEquals(1, landlord.getProperties().size());
    }

    @Test
    public void blockedLandlordSetsBannedStatus() {
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        em.flush();

        landlordService.block(landlord);
        landlordService.update(landlord);

        assertEquals(ConsumerStatus.BANNED, landlord.getStatus());
    }

    @Test
    public void activeLandlordSetsActiveStatus() {
        final Landlord blockedLandlord = Generator.generateLandlord();
        blockedLandlord.setStatus(ConsumerStatus.BANNED);
        em.persist(blockedLandlord);
        em.flush();

        landlordService.active(blockedLandlord);
        landlordService.update(blockedLandlord);

        assertEquals(ConsumerStatus.ACTIVE, blockedLandlord.getStatus());
    }

    @Test
    public void removedLandlordSetsDeletedStatus() {
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        em.flush();

        landlordService.remove(landlord);
        landlordService.update(landlord);

        assertEquals(ConsumerStatus.DELETED, landlord.getStatus());
    }
}