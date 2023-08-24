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
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class PropertyServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PropertyService propertyService;

    @Test
    public void publishedPropertySetsPublishedStatus() {
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        final City city = Generator.generateCity();
        em.persist(city);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);
        em.persist(property);
        em.flush();

        propertyService.publish(property);
        propertyService.update(property);

        assertEquals(PublicationStatus.PUBLISHED, property.getStatus());
    }

    @Test
    public void moderatedPropertySetsModerationStatus() {
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        final City city = Generator.generateCity();
        em.persist(city);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);
        em.persist(property);
        em.flush();

        propertyService.moderate(property);
        propertyService.update(property);

        assertEquals(PublicationStatus.MODERATION, property.getStatus());
    }

    @Test
    public void removedPropertySetsDeletedStatus() {
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        final City city = Generator.generateCity();
        em.persist(city);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);
        em.persist(property);
        em.flush();

        propertyService.remove(property);
        propertyService.update(property);

        assertEquals(PublicationStatus.DELETED, property.getStatus());
    }
}
