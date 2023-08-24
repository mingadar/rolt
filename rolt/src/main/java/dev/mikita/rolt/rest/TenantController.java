package dev.mikita.rolt.rest;

import dev.mikita.rolt.dto.property.ResponsePublicPropertyDto;
import dev.mikita.rolt.dto.tenant.RequestCreateTenantDto;
import dev.mikita.rolt.dto.tenant.RequestUpdateTenantDto;
import dev.mikita.rolt.dto.tenant.ResponsePublicTenantDto;
import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.exception.NotFoundException;
import dev.mikita.rolt.exception.ValidationException;
import dev.mikita.rolt.rest.util.RestUtils;
import dev.mikita.rolt.security.model.CustomUserDetails;
import dev.mikita.rolt.service.PropertyService;
import dev.mikita.rolt.service.TenantService;
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
 * The type Tenant controller.
 */
@RestController
@RequestMapping("/rest/v1/tenants")
public class TenantController {
    private static final Logger LOG = LoggerFactory.getLogger(TenantController.class);

    private final TenantService tenantService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Tenant controller.
     *
     * @param tenantService   the tenant service
     * @param propertyService the property service
     */
    @Autowired
    public TenantController(TenantService tenantService,
                            PropertyService propertyService
                            ) {
        this.tenantService = tenantService;
        this.propertyService = propertyService;
    }

    /**
     * Create response entity.
     *
     * @param tenantDto the tenant dto
     * @return the response entity
     */
    @PreAuthorize("(anonymous || hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN'))")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid RequestCreateTenantDto tenantDto) {
        Tenant tenant = new ModelMapper().map(tenantDto, Tenant.class);
        tenantService.persist(tenant);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Gets tenants.
     *
     * @param page     the page
     * @param size     the size
     * @param gender   the gender
     * @param inSearch the in search
     * @return the tenants
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getTenants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ConsumerGender gender,
            @RequestParam(required = false) Boolean inSearch) {

        ModelMapper modelMapper = new ModelMapper();

        // Filters
        Map<String, Object> filters = new HashMap<>();
        if (inSearch != null) filters.put("inSearch", inSearch);
        if (gender != null) filters.put("gender", gender);
        filters.put("status", ConsumerStatus.ACTIVE);

        // Pagination and sorting
        Pageable pageable = PageRequest.of(page, size);
        Page<Tenant> pageTenants = tenantService.findAll(pageable, filters);
        List<Tenant> tenants = pageTenants.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("tenants", tenants.stream()
                .map(landlord -> modelMapper.map(landlord, ResponsePublicTenantDto.class))
                .collect(Collectors.toList()));
        response.put("currentPage", pageTenants.getNumber());
        response.put("totalItems", pageTenants.getTotalElements());
        response.put("totalPages", pageTenants.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets tenant.
     *
     * @param id the id
     * @return the tenant
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponsePublicTenantDto getTenant(@PathVariable Integer id) {
        final Tenant tenant = tenantService.find(id);
        if (tenant == null || tenant.getStatus() != ConsumerStatus.ACTIVE) {
            throw NotFoundException.create("Tenant", id);
        }
        return new ModelMapper().map(tenant, ResponsePublicTenantDto.class);
    }

    /**
     * Update tenant.
     *
     * @param principal the principal
     * @param id        the id
     * @param tenantDto the tenant dto
     */
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTenant(Principal principal, @PathVariable Integer id, @RequestBody @Valid RequestUpdateTenantDto tenantDto) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN || user.getRole() != Role.MODERATOR) &&
                !user.getId().equals(id)) {
            throw new AccessDeniedException("Cannot update another tenant.");
        }

        final Tenant original = tenantService.find(id);
        if (original == null)
            throw NotFoundException.create("Tenant", id);

        if (!original.getId().equals(tenantDto.getId())) {
            throw new ValidationException("Tenant identifier in the data does not match the one in the request URL.");
        }

        Tenant tenant = new ModelMapper().map(tenantDto, Tenant.class);
        tenantService.update(tenant);

        LOG.debug("Updated tenant {}.", tenant);
    }

    /**
     * Delete tenant.
     *
     * @param principal the principal
     * @param id        the id
     */
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(Principal principal, @PathVariable Integer id) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN || user.getRole() != Role.MODERATOR)
                && !user.getId().equals(id)) {
            throw new AccessDeniedException("Cannot delete another tenant.");
        }

        final Tenant toRemove = tenantService.find(id);
        if (toRemove == null) {
            return;
        }

        tenantService.remove(toRemove);
        LOG.debug("Removed tenant {}.", toRemove);
    }

    /**
     * Gets favorites.
     *
     * @param principal the principal
     * @param id        the id
     * @return the favorites
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_TENANT')")
    @GetMapping(value = "/{id}/favorites", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ResponsePublicPropertyDto> getFavorites(
            Principal principal,
            @PathVariable Integer id) {

        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if ((user.getRole() != Role.ADMIN || user.getRole() != Role.MODERATOR) &&
                !user.getId().equals(id)) {
            throw new AccessDeniedException("Cannot access favorites of another tenant.");
        }

        ModelMapper modelMapper = new ModelMapper();

        final Tenant tenant = tenantService.find(id);
        if (tenant == null)
            throw NotFoundException.create("Tenant", id);

        return tenantService.getFavorites(tenant).stream()
                .map(property -> modelMapper.map(property, ResponsePublicPropertyDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Add favorite.
     *
     * @param principal   the principal
     * @param user_id     the user id
     * @param property_id the property id
     */
    @PreAuthorize("hasRole('ROLE_TENANT')")
    @PutMapping(value = "/{user_id}/favorites/{property_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFavorite(Principal principal, @PathVariable Integer user_id, @PathVariable Integer property_id) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if (!user.getId().equals(user_id)) {
            throw new AccessDeniedException("Cannot access favorites of another tenant.");
        }

        final Tenant tenant = tenantService.find(user_id);
        if (tenant == null)
            throw NotFoundException.create("Tenant", user_id);

        final Property property = propertyService.find(property_id);
        if (property == null) {
            throw NotFoundException.create("Property", property_id);
        }

        tenantService.addFavorite(property, tenant);
    }

    /**
     * Remove favorite.
     *
     * @param principal   the principal
     * @param user_id     the user id
     * @param property_id the property id
     */
    @PreAuthorize("hasRole('ROLE_TENANT')")
    @DeleteMapping(value = "/{user_id}/favorites/{property_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeFavorite(Principal principal, @PathVariable Integer user_id, @PathVariable Integer property_id) {
        final CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        final User user = userDetails.getUser();

        if (!user.getId().equals(user_id)) {
            throw new AccessDeniedException("Cannot access favorites of another tenant.");
        }

        final Tenant tenant = tenantService.find(user_id);
        if (tenant == null)
            throw NotFoundException.create("Tenant", user_id);

        final Property property = propertyService.find(property_id);
        if (property == null) {
            throw NotFoundException.create("Property", property_id);
        }

        tenantService.removeFavorite(property, tenant);
    }
}
