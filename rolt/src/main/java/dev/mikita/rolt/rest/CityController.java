package dev.mikita.rolt.rest;

import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.exception.NotFoundException;
import dev.mikita.rolt.exception.ValidationException;
import dev.mikita.rolt.rest.util.RestUtils;
import dev.mikita.rolt.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type City controller.
 */
@RestController
@RequestMapping("/rest/v1/cities")
public class CityController {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyController.class);

    private final CityService cityService;

    /**
     * Instantiates a new City controller.
     *
     * @param cityService the city service
     */
    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * Gets cities.
     *
     * @param page the page
     * @param size the size
     * @param name the name
     * @return the cities
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("name")));
        Page<City> pageCities = cityService.findAll(pageable, name);
        List<City> cities = pageCities.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("cities", cities);
        response.put("currentPage", pageCities.getNumber());
        response.put("totalItems", pageCities.getTotalElements());
        response.put("totalPages", pageCities.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets city.
     *
     * @param id the id
     * @return the city
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public City getCity(@PathVariable Integer id) {
        final City city = cityService.find(id);

        if (city == null) {
            throw NotFoundException.create("City", id);
        }

        return city;
    }

    /**
     * Create city response entity.
     *
     * @param city the city
     * @return the response entity
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCity(@RequestBody City city) {
        cityService.persist(city);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", city.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Update city.
     * @param id the id
     * @param city the city
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateCity(@PathVariable Integer id, @RequestBody City city) {
        final City original = getCity(id);
        if (!original.getId().equals(city.getId())) {
            throw new ValidationException("City identifier in the data does not match the one in the request URL.");
        }
        cityService.update(city);
        LOG.debug("Updated product {}.", city);
    }

    /**
     * Delete city.
     *
     * @param id the id
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Integer id) {
        final City toRemove = cityService.find(id);
        if (toRemove == null) {
            return;
        }
        cityService.remove(toRemove);
        LOG.debug("Removed city {}.", toRemove);
    }
}
