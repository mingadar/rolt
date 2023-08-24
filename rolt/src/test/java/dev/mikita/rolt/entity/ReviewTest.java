package dev.mikita.rolt.entity;

import dev.mikita.rolt.environment.Generator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReviewTest {
    @Test
    public void settingInvalidRainingThrowsException() {
        Review review = Generator.generateReview();

        assertThrows(RuntimeException.class, () -> {
            review.setRating(0);
        });

        assertThrows(RuntimeException.class, () -> {
            review.setRating(6);
        });
    }
}
