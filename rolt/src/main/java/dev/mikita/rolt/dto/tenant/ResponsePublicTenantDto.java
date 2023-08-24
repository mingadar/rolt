package dev.mikita.rolt.dto.tenant;

import dev.mikita.rolt.entity.ConsumerGender;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * The type Response public tenant dto.
 */
@Data
public class ResponsePublicTenantDto {
    private Integer id;
    private LocalDateTime createdOn;
    private String firstName;
    private String lastName;
    private ConsumerGender gender;
    private Boolean inSearch;
}
