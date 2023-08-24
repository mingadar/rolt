package dev.mikita.rolt.dto.landlord;

import dev.mikita.rolt.entity.ConsumerGender;
import lombok.Data;
import javax.validation.constraints.*;

/**
 * The type Request update landlord dto.
 */
@Data
public class RequestUpdateLandlordDto {
    @NotNull(message = "Specify the landlord id.")
    private Integer id;
    @Email(message = "Email should be valid.")
    @NotEmpty(message = "Email cannot be empty.")
    private String email;
    @Size(min = 2, max = 16, message = "The password must be between 2 and 16 characters.")
    private String password;
    @Size(min = 2, max = 16, message = "The firstname must be between 2 and 16 characters.")
    private String firstName;
    @Size(min = 2, max = 16, message = "The lastname must be between 2 and 16 characters.")
    private String lastName;
    @NotBlank(message = "The phone cannot be empty.")
    private String phone;
    @NotNull(message = "Set the gender.")
    private ConsumerGender gender;
}
