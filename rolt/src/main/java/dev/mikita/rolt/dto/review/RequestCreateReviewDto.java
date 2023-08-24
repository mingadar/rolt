package dev.mikita.rolt.dto.review;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The type Request create review dto.
 */
@Data
public class RequestCreateReviewDto {
    @NotNull(message = "Specify the contract id.")
    private Integer contractId;
    @NotNull(message = "Specify the author id.")
    private Integer authorId;
    @Size(min = 10, max = 1000, message
            = "Review must be between 10 and 1000 characters")
    private String description;
    @Min(value = 1, message = "The rating cannot be lower than one.")
    @Max(value = 5, message = "The rating cannot be higher than five.")
    private Integer rating;
}
