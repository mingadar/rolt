package dev.mikita.rolt.entity;

import dev.mikita.rolt.environment.Generator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContractTest {
    @Test
    public void setDateWorksProperly() {
        final Contract contract = Generator.generateContract();
        contract.setStartDate(LocalDate.of(2030, 11, 15));
        contract.setEndDate(LocalDate.of(2030, 11, 20));

        assertEquals(LocalDate.of(2030, 11, 15), contract.getStartDate());
        assertEquals(LocalDate.of(2030, 11, 20), contract.getEndDate());
    }

    @Test
    public void addPropertyWorksProperly() {
        final City city = Generator.generateCity();
        final Landlord landlord = Generator.generateLandlord();
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);

        final Contract contract = Generator.generateContract();
        contract.setProperty(property);

        assertEquals(property, contract.getProperty());
    }

    @Test
    public void setTenantWorksProperly() {
        final City city = Generator.generateCity();
        final Landlord landlord = Generator.generateLandlord();
        final Tenant tenant = Generator.generateTenant();
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);

        final Contract contract = Generator.generateContract();
        contract.setProperty(property);
        contract.setTenant(tenant);

        assertEquals(tenant, contract.getTenant());
    }

    @Test
    public void prePersistContractWithIncorrectDateRangeReturnRuntimeException() {
        final Tenant tenant = Generator.generateTenant();
        final Landlord landlord = Generator.generateLandlord();
        final City city = Generator.generateCity();
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(landlord);

        final Contract contract = Generator.generateContract();
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().minusDays(1));
        contract.setTenant(tenant);
        contract.setProperty(property);

        assertThrows(RuntimeException.class, () -> {
            contract.prePersist();
        });
    }
}
