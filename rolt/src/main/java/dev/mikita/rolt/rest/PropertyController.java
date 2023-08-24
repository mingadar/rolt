package dev.mikita.rolt.rest;

import dev.mikita.rolt.dto.property.RequestCreatePropertyDto;
import dev.mikita.rolt.dto.property.RequestUpdatePropertyDto;
import dev.mikita.rolt.dto.property.ResponsePublicPropertyDto;
import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.exception.NotFoundException;
import dev.mikita.rolt.exception.ValidationException;
import dev.mikita.rolt.rest.util.RestUtils;
import dev.mikita.rolt.security.model.CustomUserDetails;
import dev.mikita.rolt.service.CityService;
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
 * The type Property controller.
 */
@RestController
@RequestMapping("/rest/v1/properties")
public class PropertyController {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyController.class);

    private final PropertyService propertyService;
    private final LandlordService landlordService;
    private final CityService cityService;

    /**
     * Instantiates a new Property controller.
     *
     * @param propertyService the property service
     * @param landlordService the landlord service
     * @param cityService     the city service
     */
    @Autowired
    public PropertyController(PropertyService propertyService,
                              LandlordService landlordService,
                              CityService cityService) {
        this.propertyService = propertyService;
        this.landlordService = landlordService;
        this.cityService = cityService;
    }

    /**
     * Gets properties.
     *
     * @param page         the page
     * @param size         the size
     * @param cityId       the city id
     * @param propertyType the property type
     * @param minSquare    the min square
     * @param maxSquare    the max square
     * @param isAvailable  the is available
     * @return the properties
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) Double minSquare,
            @RequestParam(required = false) Double maxSquare,
            @RequestParam(required = false) Boolean isAvailable) {

        ModelMapper modelMapper = new ModelMapper();

        // Filters
        Map<String, Object> filters = new HashMap<>();
        if (cityId != null) filters.put("cityId", cityId);
        if (propertyType != null) filters.put("propertyType", propertyType);
        if (minSquare != null) filters.put("minSquare", minSquare);
        if (maxSquare != null) filters.put("maxSquare", maxSquare);
        if (isAvailable != null) filters.put("isAvailable", isAvailable);
        filters.put("status", PublicationStatus.PUBLISHED);

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

    /**
     * Gets property.
     *
     * @param id the id
     * @return the property
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponsePublicPropertyDto getProperty(@PathVariable Integer id) {
        final Property property = propertyService.find(id);
        if (property == null || property.getStatus() != PublicationStatus.PUBLISHED) {
            throw NotFoundException.create("Property", id);
        }
        return new ModelMapper().map(property, ResponsePublicPropertyDto.class);
    }

    /**
     * Create property response entity.
     *
     * @param principal the principal
     * @param propertyDto the property dto
     * @return the response entity
     */
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createProperty(Principal principal, @RequestBody @Valid RequestCreatePropertyDto propertyDto) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN || user.getRole() != Role.MODERATOR)
                && !user.getId().equals(propertyDto.getOwnerId())) {
            throw new AccessDeniedException("You cannot create properties for other users.");
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        final Landlord landlord = landlordService.find(propertyDto.getOwnerId());
        if (landlord == null)
            throw NotFoundException.create("Landlord", propertyDto.getOwnerId());

        final City city = cityService.find(propertyDto.getCityId());
        if (city == null)
            throw NotFoundException.create("City", propertyDto.getCityId());

        Property property = modelMapper.map(propertyDto, Property.class);
        property.setOwner(landlord);
        property.setCity(city);
        propertyService.persist(property);

        LOG.debug("Created property {}.", property);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", property.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Update property.
     *
     * @param principal the principal
     * @param id          the id
     * @param propertyDto the property dto
     */
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_LANDLORD', 'ROLE_MODERATOR')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProperty(
            Principal principal,
            @PathVariable Integer id,
            @RequestBody @Valid RequestUpdatePropertyDto propertyDto) {

        final Property original = propertyService.find(id);
        if (original == null) {
            throw NotFoundException.create("Property", id);
        }

        if (!original.getId().equals(propertyDto.getId())) {
            throw new ValidationException("Property identifier in the data does not match the one in the request URL.");
        }

        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN || user.getRole() != Role.MODERATOR)
                && !user.getId().equals(original.getOwner().getId())) {
            throw new AccessDeniedException("You cannot update properties for other users.");
        }

        final City city = cityService.find(propertyDto.getCityId());
        if (city == null)
            throw NotFoundException.create("City", propertyDto.getCityId());

        Property property = new ModelMapper().map(propertyDto, Property.class);
        property.setCity(city);
        property.setOwner(original.getOwner());
        property.setStatus(original.getStatus());

        propertyService.update(property);

        LOG.debug("Updated property {}.", propertyDto);
    }

    /**
     * Delete property.
     *
     * @param principal the principal
     * @param id the id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_LANDLORD', 'ROLE_MODERATOR')")
    public void deleteProperty(Principal principal, @PathVariable Integer id) {
        final Property toRemove = propertyService.find(id);
        if (toRemove == null) {
            return;
        }

        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN || user.getRole() != Role.MODERATOR)
                && !user.getId().equals(toRemove.getOwner().getId())) {
            throw new AccessDeniedException("You cannot delete properties for other users.");
        }

        propertyService.remove(toRemove);
        LOG.debug("Removed property {}.", toRemove);
    }
}
