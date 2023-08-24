package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.CityDao;
import dev.mikita.rolt.entity.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * The type City service.
 */
@Service
public class CityService {
    private final CityDao cityDao;

    /**
     * Instantiates a new City service.
     *
     * @param cityDao the city dao
     */
    @Autowired
    public CityService(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param name     the name
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<City> findAll(Pageable pageable, String name) {
        return cityDao.findAll(pageable, name);
    }

    /**
     * Find city.
     *
     * @param id the id
     * @return the city
     */
    @Transactional(readOnly = true)
    public City find(Integer id) {
        return cityDao.find(id);
    }

    /**
     * Persist.
     *
     * @param city the city
     */
    @Transactional
    public void persist(City city) {
        cityDao.persist(city);
    }

    /**
     * Update.
     *
     * @param city the city
     */
    @Transactional
    public void update(City city) {
        cityDao.update(city);
    }

    /**
     * Remove.
     *
     * @param city the city
     */
    @Transactional
    public void remove(City city) {
        cityDao.remove(city);
    }
}
