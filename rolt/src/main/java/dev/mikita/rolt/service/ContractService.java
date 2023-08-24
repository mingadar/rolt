package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.ContractDao;
import dev.mikita.rolt.entity.*;
import dev.mikita.rolt.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * The type Contract service.
 */
@Service
public class ContractService {
    private final ContractDao contractDao;

    /**
     * Instantiates a new Contract service.
     *
     * @param contractDao the contract dao
     */
    @Autowired
    public ContractService(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<Contract> findAll(Pageable pageable, Map<String, Object> filters) {
        return contractDao.findAll(pageable, filters);
    }

    /**
     * Find contract.
     *
     * @param id the id
     * @return the contract
     */
    @Transactional(readOnly = true)
    public Contract find(Integer id) {
        return contractDao.find(id);
    }

    /**
     * Persist.
     *
     * @param contract the contract
     */
    @Transactional
    public void persist(Contract contract) {
        List<Contract> intersections = contractDao.findIntersectionsByDateRange(contract.getProperty(), contract.getStartDate(), contract.getEndDate());

        if (intersections.size() > 0) {
            throw new ValidationException("Contracts already exist in this date range.");
        }

        contractDao.persist(contract);
    }

    /**
     * Update.
     *
     * @param contract the contract
     */
    @Transactional
    public void update(Contract contract) {
        contractDao.update(contract);
    }

    /**
     * Remove.
     *
     * @param contract the contract
     */
    @Transactional
    public void remove(Contract contract) {
        contractDao.remove(contract);
    }
}
