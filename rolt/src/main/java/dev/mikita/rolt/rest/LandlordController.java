package dev.mikita.rolt.rest;

import dev.mikita.rolt.dto.landlord.RequestCreateLandlordDto;
import dev.mikita.rolt.dto.landlord.RequestUpdateLandlordDto;
import dev.mikita.rolt.dto.landlord.ResponsePublicLandlordDto;
import dev.mikita.rolt.dto.property.ResponsePublicPropertyDto;
import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.exception.NotFoundException;
import dev.mikita.rolt.exception.ValidationException;
import dev.mikita.rolt.rest.util.RestUtils;
import dev.mikita.rolt.security.model.CustomUserDetails;
import dev.mikita.rolt.service.LandlordService;
import dev.mikita.rolt.service.PropertyService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Landlord controller.
 */
@RestController
@RequestMapping("/rest/v1/landlords")
public class LandlordController {
    private static final Logger LOG = LoggerFactory.getLogger(LandlordController.class);

    private final LandlordService landlordService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Landlord controller.
     *
     * @param landlordService the landlord service
     * @param propertyService the property service
     */
    @Autowired
    public LandlordController(LandlordService landlordService,
                              PropertyService propertyService
    ) {
        this.landlordService = landlordService;
        this.propertyService = propertyService;
    }

    /**
     * Create landlord response entity.
     *
     * @param landlordDto the landlord dto
     * @return the response entity
     */
    @PreAuthorize("(anonymous || hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN'))")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createLandlord(@RequestBody @Valid RequestCreateLandlordDto landlordDto) {
        Landlord landlord = new ModelMapper().map(landlordDto, Landlord.class);
        landlordService.persist(landlord);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Gets landlord.
     *
     * @param id the id
     * @return the landlord
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponsePublicLandlordDto getLandlord(@PathVariable Integer id) {
        final Landlord landlord = landlordService.find(id);
        if (landlord == null || landlord.getStatus() != ConsumerStatus.ACTIVE) {
            throw NotFoundException.create("Landlord", id);
        }
        return new ModelMapper().map(landlord, ResponsePublicLandlordDto.class);
    }

    /**
     * Gets landlords.
     *
     * @param page the page
     * @param size the size
     * @param gender the gender
     * @return the landlords
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getLandlords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ConsumerGender gender) {

        ModelMapper modelMapper = new ModelMapper();

        // Filters
        Map<String, Object> filters = new HashMap<>();
        if (gender != null) filters.put("gender", gender);
        filters.put("status", ConsumerStatus.ACTIVE);

        // Pagination and sorting
        Pageable pageable = PageRequest.of(page, size);
        Page<Landlord> pageLandlords = landlordService.findAll(pageable, filters);
        List<Landlord> landlords = pageLandlords.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("landlords", landlords.stream()
                .map(landlord -> modelMapper.map(landlord, ResponsePublicLandlordDto.class))
                .collect(Collectors.toList()));
        response.put("currentPage", pageLandlords.getNumber());
        response.put("totalItems", pageLandlords.getTotalElements());
        response.put("totalPages", pageLandlords.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update landlord.
     *
     * @param principal   the principal
     * @param id          the id
     * @param landlordDto the landlord dto
     */
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLandlord(Principal principal, @PathVariable Integer id, @RequestBody @Valid RequestUpdateLandlordDto landlordDto) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN
                || user.getRole() != Role.MODERATOR) &&
                !user.getId().equals(id)) {
            throw new AccessDeniedException("Cannot update another landlord.");
        }

        final Landlord original = landlordService.find(id);
        if (original == null || original.getStatus() != ConsumerStatus.ACTIVE) {
            throw NotFoundException.create("Landlord", id);
        }

        if (!original.getId().equals(landlordDto.getId())) {
            throw new ValidationException("Landlord identifier in the data does not match the one in the request URL.");
        }

        Landlord landlord = new ModelMapper().map(landlordDto, Landlord.class);
        landlordService.update(landlord);

        LOG.debug("Updated property {}.", landlord);
    }

    /**
     * Delete landlord.
     *
     * @param principal the principal
     * @param id        the id
     */
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLandlord(Principal principal, @PathVariable Integer id) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN
                || user.getRole() != Role.MODERATOR) &&
                !user.getId().equals(id)) {
            throw new AccessDeniedException("Cannot delete another landlord.");
        }

        final Landlord toRemove = landlordService.find(id);
        if (toRemove == null) {
            return;
        }

        landlordService.remove(toRemove);
        LOG.debug("Removed landlord {}.", toRemove);
    }

    /**
     * Gets properties.
     *
     * @param id           the id
     * @param page         the page
     * @param size         the size
     * @param cityId       the city id
     * @param propertyType the property type
     * @param minSquare    the min square
     * @param maxSquare    the max square
     * @param isAvailable  the is available
     * @return the properties
     */
    @GetMapping(value = "/{id}/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getProperties(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) Double minSquare,
            @RequestParam(required = false) Double maxSquare,
            @RequestParam(required = false) Boolean isAvailable) {

        final Landlord landlord = landlordService.find(id);
        if (landlord == null)
            throw NotFoundException.create("Landlord", id);

        ModelMapper modelMapper = new ModelMapper();

        // Filters
        Map<String, Object> filters = new HashMap<>();
        if (cityId != null) filters.put("cityId", cityId);
        if (propertyType != null) filters.put("propertyType", propertyType);
        if (minSquare != null) filters.put("minSquare", minSquare);
        if (maxSquare != null) filters.put("maxSquare", maxSquare);
        if (isAvailable != null) filters.put("isAvailable", isAvailable);
        filters.put("status", PublicationStatus.PUBLISHED);
        filters.put("ownerId", landlord.getId());

        // Pagination and sorting
        Pageable pageable = PageRequest.of(page, size);
        Page<Property> pageProperties = propertyService.findAll(pageable, filters);
        List<Property> properties = pageProperties.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("properties", properties.stream()
                .map(property -> modelMapper.map(property, ResponsePublicPropertyDto.class))
                .collect(Collectors.toList()));
        response.put("currentPage", pageProperties.getNumber());
        response.put("totalItems", pageProperties.getTotalElements());
        response.put("totalPages", pageProperties.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
