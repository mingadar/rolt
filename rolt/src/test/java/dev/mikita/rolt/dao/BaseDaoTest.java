package dev.mikita.rolt.dao;

import dev.mikita.rolt.App;
import dev.mikita.rolt.entity.City;
import dev.mikita.rolt.environment.Generator;
import dev.mikita.rolt.environment.TestConfiguration;
import dev.mikita.rolt.exception.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ComponentScan(basePackageClasses = App.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class BaseDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CityDao cityDao;

    @Test
    public void persistSavesSpecifiedInstance() {
        final City city = Generator.generateCity();
        cityDao.persist(city);
        assertNotNull(city.getId());

        final City result = em.find(City.class, city.getId());
        assertNotNull(result);
        assertEquals(city.getId(), result.getId());
        assertEquals(city.getName(), result.getName());
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        final City city = Generator.generateCity();
        em.persistAndFlush(city);
        assertNotNull(city.getId());

        final City result = cityDao.find(city.getId());
        assertNotNull(result);
        assertEquals(city.getId(), result.getId());
        assertEquals(city.getName(), result.getName());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        final City cityOne = Generator.generateCity();
        em.persistAndFlush(cityOne);
        final City cityTwo = Generator.generateCity();
        em.persistAndFlush(cityTwo);

        final List<City> result = cityDao.findAll();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(cityOne.getId())));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(cityTwo.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        final City city = Generator.generateCity();
        em.persistAndFlush(city);

        final City update = new City();
        update.setId(city.getId());
        final String newName = "New city name";
        update.setName(newName);
        cityDao.update(update);

        final City result = cityDao.find(city.getId());
        assertNotNull(result);
        assertEquals(city.getName(), result.getName());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        final City city = Generator.generateCity();
        em.persistAndFlush(city);
        assertNotNull(em.find(City.class, city.getId()));
        em.detach(city);

        cityDao.remove(city);
        assertNull(em.find(City.class, city.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        final City city = Generator.generateCity();
        city.setId(123);
        assertNull(em.find(City.class, city.getId()));

        cityDao.remove(city);
        assertNull(em.find(City.class, city.getId()));
    }

    @Test
    public void exceptionOnPersistInWrappedInPersistenceException() {
        final City city = Generator.generateCity();
        em.persistAndFlush(city);
        em.remove(city);
        assertThrows(PersistenceException.class, () -> cityDao.update(city));
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        final City city = Generator.generateCity();
        em.persistAndFlush(city);
        assertTrue(cityDao.exists(city.getId()));
        assertFalse(cityDao.exists(-1));
    }
}
