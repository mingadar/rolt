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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DataJpaTest
@ComponentScan(basePackageClasses = App.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class ReviewDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ReviewDao reviewDao;

    @Test
    public void findByAuthorReturnsReviewsFromCertainConsumer() {
        final Tenant author = Generator.generateTenant();
        em.persist(author);
        final Landlord notAuthor = Generator.generateLandlord();
        em.persist(notAuthor);
        final City city = Generator.generateCity();
        em.persist(city);
        final Property property = Generator.generateProperty();
        property.setCity(city);
        property.setOwner(notAuthor);
        em.persist(property);
        final Contract contract = Generator.generateContract();
        contract.setProperty(property);
        contract.setTenant(author);
        em.persist(contract);

        List<Review> reviews = new ArrayList<>();

        final Review reviewOne = Generator.generateReview();
        reviewOne.setContract(contract);
        reviewOne.setAuthor(author);
        reviewDao.persist(reviewOne);

        final Review reviewTwo = Generator.generateReview();
        reviewTwo.setContract(contract);
        reviewTwo.setAuthor(notAuthor);
        reviewDao.persist(reviewTwo);

        reviews.add(reviewOne);
        reviews.add(reviewTwo);

        em.flush();

        Pageable pageable = PageRequest.of(1, 10);
        Map<String, Object> filters = new HashMap<>();
        filters.put("authorId", author.getId());

        Page<Review> result = reviewDao.findAll(pageable, filters);
        assertEquals(reviews.stream().filter(r -> r.getAuthor() == author).count(), result.getTotalElements());
        result.forEach(r -> assertSame(r.getAuthor(), author));
    }
}
