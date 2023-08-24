package dev.mikita.rolt.service;

import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.environment.Generator;
import dev.mikita.rolt.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ContractServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ContractService contractService;

    @Test
    public void persistWithIntersectedDatesThrowsValidationException() {
        final Tenant tenant = Generator.generateTenant();
        em.persist(tenant);
        final Landlord landlord = Generator.generateLandlord();
        em.persist(landlord);
        final City city = Generator.generateCity();
        em.persist(city);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);
        em.persist(property);
        final Contract contract = Generator.generateContract();
        contract.setStartDate(LocalDate.of(2030, 11, 15));
        contract.setEndDate(LocalDate.of(2030, 11, 20));
        contract.setProperty(property);
        contract.setTenant(tenant);
        em.persist(contract);
        em.flush();

        final Contract newContract = Generator.generateContract();
        newContract.setStartDate(LocalDate.of(2030, 11, 15));
        newContract.setEndDate(LocalDate.of(2030, 11, 24));
        newContract.setProperty(property);
        newContract.setTenant(tenant);

        assertThrows(ValidationException.class, () -> {
            contractService.persist(newContract);
        });
    }
}
