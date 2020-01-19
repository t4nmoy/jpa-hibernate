package persistence.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.entity.Company;
import persistence.entity.CompanyType;
import persistence.repository.CompanyRepository;

@Service
@Transactional
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    private Company save(String code, String name, CompanyType companyType) {
        Company company = Company.of(code, name, companyType);
        company = companyRepository.save(company);
        logger.debug("new company saved: {}", company);
        return company;
    }


    private Company update(Company company) {
        logger.debug("request to update company: {}", company);
        return companyRepository.save(company);
    }

}
