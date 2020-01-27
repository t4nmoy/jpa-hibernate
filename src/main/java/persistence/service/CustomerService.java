package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import persistence.entity.Customer;
import persistence.repository.CustomerRepository;

import javax.validation.Valid;

@Service
@Validated
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository repository;

    private final CompanyService companyService;

    public CustomerService(CustomerRepository repository, CompanyService companyService) {
        this.repository = repository;
        this.companyService = companyService;
    }

    public Customer create(@Valid Customer customer) {
        Assert.isNull(customer.getId(), "customer id must be null");
        Assert.isTrue(customer.getCompany().getId() != null, "customer company id is required");

        companyService.findMust(customer.getCompany().getId());
        customer = repository.save(customer);
        logger.debug("new customer is created : {}", customer);
        return customer;
    }


    public Customer update(@Valid Customer customer) {
        Assert.isTrue(customer.getId() != null, "customer id is required");
        Assert.isTrue(customer.getCompany().getId() != null, "customer company id is required");

        companyService.findMust(customer.getCompany().getId());
        customer = repository.save(customer);
        return customer;
    }

    public Customer findMust(Long id) {
        return repository.findMust(id);
    }

    public Customer findByName(String name) {
        return repository.findByName(name);
    }
}
