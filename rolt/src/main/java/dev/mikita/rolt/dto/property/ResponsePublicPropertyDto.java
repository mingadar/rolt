package dev.mikita.rolt.dto.property;

import dev.mikita.rolt.entity.PropertyType;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * The type Response public property dto.
 */
@Data
public class ResponsePublicPropertyDto {
    private Integer id;
    private Integer ownerId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private PropertyType propertyType;
    private Boolean isAvailable;
    private Double square;
    private String description;
    private String street;
    private String postalCode;
    private Integer cityId;
}
