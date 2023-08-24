package dev.mikita.rolt.dao;

import dev.mikita.rolt.App;
import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.environment.Generator;
import dev.mikita.rolt.environment.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = App.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class PropertyDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private PropertyDao propertyDao;

    @Test
    public void findAllReturnsOnlyPublishedProperties() {
        final City city = Generator.generateCity();
        final Landlord owner = Generator.generateLandlord();
        em.persist(owner);
        em.persist(city);
        em.flush();
        final List<Property> properties = IntStream.range(0, 10).mapToObj(i -> {
                    Property p = Generator.generateProperty();
                    p.setCity(city);
                    p.setOwner(owner);
                    return p;
                })
                .collect(Collectors.toList());

        Property deletedProperty = Generator.generateProperty();
        deletedProperty.setOwner(owner);
        deletedProperty.setCity(city);
        deletedProperty.setStatus(PublicationStatus.DELETED);
        properties.add(deletedProperty);

        Pageable pageable = PageRequest.of(1, 10);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", PublicationStatus.PUBLISHED);

        properties.forEach(em::persist);

        final Page<Property> result = propertyDao.findAll(pageable, filters);
        assertEquals(properties.stream().filter(p -> p.getStatus() == PublicationStatus.PUBLISHED).count(), result.getTotalElements());
        result.forEach(p -> assertSame(p.getStatus(), PublicationStatus.PUBLISHED));
    }

    @Test
    public void findAllAvailableReturnsOnlyPublishedAndAvailableProperties() {
        final City city = Generator.generateCity();
        final Landlord owner = Generator.generateLandlord();
        em.persist(owner);
        em.persist(city);
        em.flush();
        final List<Property> properties = IntStream.range(0, 10).mapToObj(i -> {
                    Property p = Generator.generateProperty();
                    p.setCity(city);
                    p.setOwner(owner);
                    return p;
                })
                .collect(Collectors.toList());

        Property deletedProperty = Generator.generateProperty();
        deletedProperty.setOwner(owner);
        deletedProperty.setCity(city);
        deletedProperty.setStatus(PublicationStatus.DELETED);
        properties.add(deletedProperty);

        Property unavailableProperty = Generator.generateProperty();
        unavailableProperty.setOwner(owner);
        unavailableProperty.setCity(city);
        unavailableProperty.setStatus(PublicationStatus.DELETED);
        unavailableProperty.setAvailable(false);
        properties.add(unavailableProperty);

        Pageable pageable = PageRequest.of(1, 10);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", PublicationStatus.PUBLISHED);
        filters.put("isAvailable", true);

        properties.forEach(em::persist);

        final Page<Property> result = propertyDao.findAll(pageable, filters);
        assertEquals(properties.stream().filter(p -> p.getStatus() == PublicationStatus.PUBLISHED && p.getAvailable()).count(), result.getTotalElements());
        result.forEach(p -> assertTrue(p.getStatus() == PublicationStatus.PUBLISHED && p.getAvailable()));
    }
}
