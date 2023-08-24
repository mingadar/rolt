package dev.mikita.rolt.dto.property;

import dev.mikita.rolt.entity.PropertyType;
import lombok.Data;
import javax.validation.constraints.*;

/**
 * The type Request update property dto.
 */
@Data
public class RequestUpdatePropertyDto {
    @NotNull(message = "Specify the property id.")
    private Integer id;
    @NotNull(message = "Specify the property type.")
    private PropertyType propertyType;
    private Boolean isAvailable;
    @Positive
    @Max(value = 10000, message = "Square should not be greater than 10000")
    @NotNull(message = "Specify the square.")
    private Double square;
    @Size(min = 10, max = 2000, message
            = "Description must be between 10 and 2000 characters")
    private String description;
    @Size(min = 2, max = 64, message = "The street must be between 2 and 64 characters.")
    @NotBlank(message = "The street cannot be empty.")
    private String street;
    @Size(min = 2, max = 24, message = "The postalCode must be between 2 and 24 characters.")
    @NotBlank(message = "The street cannot be empty.")
    private String postalCode;
    @NotNull(message = "Specify the city id.")
    private Integer cityId;
}
