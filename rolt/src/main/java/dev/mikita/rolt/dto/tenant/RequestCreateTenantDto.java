package dev.mikita.rolt.dto.tenant;

import dev.mikita.rolt.entity.ConsumerGender;
import lombok.Data;
import javax.validation.constraints.*;

/**
 * The type Request create tenant dto.
 */
@Data
public class RequestCreateTenantDto {
    @Email(message = "Email should be valid.")
    @NotEmpty(message = "Email cannot be empty.")
    private String email;
    @Size(min = 2, max = 16, message = "The firstname must be between 2 and 16 characters.")
    private String password;
    @Size(min = 2, max = 16, message = "The firstname must be between 2 and 16 characters.")
    private String firstName;
    @Size(min = 2, max = 16, message = "The lastname must be between 2 and 16 characters.")
    private String lastName;
    @NotBlank(message = "The phone cannot be empty.")
    private String phone;
    @NotNull(message = "Set the gender.")
    private ConsumerGender gender;
    private Boolean inSearch;
}
