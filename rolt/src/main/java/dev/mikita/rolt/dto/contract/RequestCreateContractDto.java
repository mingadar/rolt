package dev.mikita.rolt.dto.contract;

import lombok.Data;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * The type Request create contract dto.
 */
@Data
public class RequestCreateContractDto {
    // TODO Cross field validation
    @Future(message = "The date must be in the future.")
    private LocalDate startDate;
    @Future(message = "The date must be in the future.")
    private LocalDate endDate;
    @NotNull(message = "Specify the property id.")
    private Integer propertyId;
    @NotNull(message = "Specify the tenant id.")
    private Integer tenantId;
}
