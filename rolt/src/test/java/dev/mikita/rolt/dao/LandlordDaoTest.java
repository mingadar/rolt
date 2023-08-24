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
public class LandlordDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private LandlordDao landlordDao;

    @Test
    public void findAllReturnsOnlyActiveLandlords() {
        final List<Landlord> landlords = IntStream.range(0, 10).mapToObj(i -> Generator.generateLandlord())
                .collect(Collectors.toList());

        Landlord bannedLandlord = Generator.generateLandlord();
        bannedLandlord.setStatus(ConsumerStatus.BANNED);
        landlords.add(bannedLandlord);

        landlords.forEach(em::persist);

        Pageable pageable = PageRequest.of(1, 10);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", ConsumerStatus.ACTIVE);

        final Page<Landlord> result = landlordDao.findAll(pageable, filters);
        assertEquals(landlords.stream().filter(t -> t.getStatus() == ConsumerStatus.ACTIVE).count(), result.getTotalElements());
//        assertEquals(landlords.stream().filter(t -> t.getStatus() == ConsumerStatus.ACTIVE).count(), result.size());
        result.forEach(t -> assertSame(t.getStatus(), ConsumerStatus.ACTIVE));
    }

    @Test
    public void afterAddingPropertyPropertyExists() {
        final Landlord owner = Generator.generateLandlord();
        final Property property = Generator.generateProperty();

        owner.addProperty(property);
        landlordDao.persist(owner);

        assertNotNull(em.find(Property.class, property.getId()));
    }

    @Test
    public void afterRemovingPropertyPropertyDoesNotExists() {
        final Landlord owner = Generator.generateLandlord();
        landlordDao.persist(owner);

        final City city = Generator.generateCity();
        em.persist(city);

        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setId(1);

        owner.addProperty(property);
        landlordDao.update(owner);
        em.flush();

        final Property propertyForDeleting = em.find(Property.class, 1);
        owner.removeProperty(propertyForDeleting);
        landlordDao.update(owner);

        em.flush();

        assertNull(em.find(Property.class, 1));
    }
}
