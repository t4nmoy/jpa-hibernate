package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Company;
import persistence.entity.CompanyType;
import persistence.repository.CompanyRepository;

import java.util.Optional;

@Service
@Transactional
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company create(String code, String name, CompanyType companyType) {
        Company company = Company.of(code, name, companyType);
        company = companyRepository.save(company);
        logger.debug("new company saved : {}", company);
        return company;
    }

    public Company update(Company company) {
        logger.debug("request to update company : {}", company);
        if (company.getId() == null) {
            throw new IllegalArgumentException("invalid company parameter");
        }
        return companyRepository.save(company);
    }

    @Transactional(readOnly = true)
    public Page<Company> findAll(Pageable pageable) {
        logger.debug("request to get all companies with paging: {}", pageable);
        return companyRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Company> findOne(Long id) {
        logger.debug("request to get company with id : {}", id);
        return companyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Company> findByCode(String code) {
        return companyRepository.findByCode(code);
    }

    public void delete(Long id) {
        logger.debug("request to delete company with id : {}", id);
        companyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Company findMust(Long id) {
        logger.debug("request to must get company with id : {}", id);
        return companyRepository.findMust(id);
    }
}
