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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = App.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class ContractDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ContractDao contractDao;

    @Test
    public void findAllByPropertyReturnsContractsOnlySpecificProperty() {
        final List<Landlord> landlords = IntStream.range(0, 10).mapToObj(i -> Generator.generateLandlord())
                .collect(Collectors.toList());
        landlords.forEach(em::persist);

        final List<Tenant> tenants = IntStream.range(0, 10).mapToObj(i -> Generator.generateTenant())
                .collect(Collectors.toList());
        tenants.forEach(em::persist);

        final List<City> cities = IntStream.range(0, 10).mapToObj(i -> Generator.generateCity()).collect(Collectors.toList());
        cities.forEach(em::persist);

        final List<Property> properties = IntStream.range(0, 10).mapToObj(i -> {
            Property p = Generator.generateProperty();
            p.setOwner(landlords.get(Generator.randomInt(0, landlords.size() - 1)));
            p.setCity(cities.get(Generator.randomInt(0, cities.size() - 1)));

            return p;
        }).collect(Collectors.toList());
        properties.forEach(em::persist);

        Pageable pageable = PageRequest.of(1, 10);
        Map<String, Object> filters = new HashMap<>();

        final List<Contract> contracts = IntStream.range(0, 10).mapToObj(i -> {
            final Property p = properties.get(Generator.randomInt(0, properties.size() - 1));
            Contract contract = new Contract();

            contract.setStartDate(LocalDate.now().plusDays(i));
            contract.setEndDate(LocalDate.now().plusDays(i + 1));
            contract.setTenant(tenants.get(Generator.randomInt(0, tenants.size() - 1)));
            contract.setProperty(p);

            filters.put("property", p);

            return contract;
        }).collect(Collectors.toList());

        contracts.forEach(em::persist);

        final Property randomProperty = properties.get(Generator.randomInt(0, properties.size() - 1));
        final Page<Contract> result = contractDao.findAll(pageable, filters);
        result.forEach(c -> assertEquals(randomProperty, c.getProperty()));
    }

    @Test
    public void persistContractWithIncorrectDateRangeReturnRuntimeException() {
        final Tenant tenant = Generator.generateTenant();
        final Landlord landlord = Generator.generateLandlord();
        final City city = Generator.generateCity();
        final Property property = Generator.generateProperty();
        em.persist(tenant);
        em.persist(landlord);
        em.persist(city);

        property.setOwner(landlord);
        property.setCity(city);
        em.persist(property);

        final Contract contract = new Contract();

        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().minusDays(1));
        contract.setTenant(tenant);
        contract.setProperty(property);

        assertThrows(RuntimeException.class, () -> {
            em.persist(contract);
        });
    }
}
