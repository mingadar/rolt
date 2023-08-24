package dev.mikita.rolt.dto.landlord;

import dev.mikita.rolt.entity.ConsumerGender;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * The type Response public landlord dto.
 */
@Data
public class ResponsePublicLandlordDto {
    private Integer id;
    private LocalDateTime createdOn;
    private String firstName;
    private String lastName;
    private ConsumerGender gender;
}
