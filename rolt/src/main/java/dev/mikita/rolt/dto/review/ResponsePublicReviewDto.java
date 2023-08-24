package dev.mikita.rolt.dto.review;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * The type Response public review dto.
 */
@Data
public class ResponsePublicReviewDto {
    private Integer id;
    private Integer authorId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Integer contractId;
    private String description;
    private Integer rating;
}
