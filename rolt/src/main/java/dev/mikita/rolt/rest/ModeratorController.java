package dev.mikita.rolt.rest;

import dev.mikita.rolt.entity.Moderator;
import dev.mikita.rolt.rest.util.RestUtils;
import dev.mikita.rolt.service.ModeratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Moderator controller.
 */
@RestController
@RequestMapping("/rest/v1/moderators")
public class ModeratorController {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerController.class);

    private ModeratorService moderatorService;

    /**
     * Instantiates a new Moderator controller.
     *
     * @param moderatorService the moderator service
     */
    @Autowired
    public ModeratorController(ModeratorService moderatorService) {
        this.moderatorService = moderatorService;
    }

    /**
     * Create moderator response entity.
     *
     * @param moderator the moderator
     * @return the response entity
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createModerator(@RequestBody Moderator moderator) {
        moderatorService.persist(moderator);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
