package dev.mikita.rolt.environment;

import dev.mikita.rolt.entity.*;
import java.time.LocalDate;
import java.util.Random;

public class Generator {
    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static City generateCity() {
        final City city = new City();
        city.setName("Random City Name " + randomInt());

        return city;
    }

    public static Contract generateContract() {
        final Contract contract = new Contract();

        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusDays(randomInt(1, 60)));

        return contract;
    }

    public static Review generateReview() {
        final Review review = new Review();
        review.setDescription("randomDescription" + randomInt());
        review.setRating(randomInt(1, 5));

        return review;
    }

    public static Property generateProperty() {
        final Property property = new Property();

        property.setAvailable(true);
        property.setDescription("randomDescription" + randomInt());
        property.setType(PropertyType.APARTMENT);
        property.setSquare((double) randomInt(15, 100));
        property.setStreet("randomStreet" + randomInt());
        property.setPostalCode(String.valueOf(randomInt()));

        return property;
    }

    public static Landlord generateLandlord() {
        final Landlord landlord = new Landlord();

        landlord.setEmail("randomemail" + randomInt() + "@gmail.com");
        landlord.setPassword("randompassword" + randomInt());
        landlord.setFirstName("FirstName" + randomInt());
        landlord.setLastName("LastName" + randomInt());
        landlord.setPhone(String.valueOf(randomInt()));
        landlord.setGender(ConsumerGender.MALE);

        return landlord;
    }

    public static Tenant generateTenant() {
        final Tenant tenant = new Tenant();

        tenant.setEmail("randomemail" + randomInt() + "@gmail.com");
        tenant.setPassword("randompassword" + randomInt());
        tenant.setFirstName("FirstName" + randomInt());
        tenant.setLastName("LastName" + randomInt());
        tenant.setPhone(String.valueOf(randomInt()));
        tenant.setGender(ConsumerGender.MALE);

        return tenant;
    }
}