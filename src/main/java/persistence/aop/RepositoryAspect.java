package persistence.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import persistence.entity.TenantEntityBase;
import persistence.exception.PersistenceException;
import persistence.utils.TenantContext;

import javax.persistence.EntityManager;

@Aspect
public class RepositoryAspect {

    private final Logger logger = LoggerFactory.getLogger(RepositoryAspect.class);

    @Autowired
    private EntityManager entityManager;

    @Pointcut("execution(public !void org.springframework.data.repository.Repository+.save*(..)) && args(tenantEntity,..)")
    public void publicSaveRepositoryMethodPointcut(TenantEntityBase tenantEntity) {
    }

    @Before(value = "publicSaveRepositoryMethodPointcut(tenantEntity)", argNames = "tenantEntity")
    public void publicSaveRepositoryMethod(TenantEntityBase tenantEntity) {
        if (TenantContext.getTenantId() != null){
            tenantEntity.setTenantId(TenantContext.getTenantId());
            logger.debug("tenant id {} inserted into entity", TenantContext.getTenantId());
        }
    }

    @Pointcut("execution(public * org.springframework.data.repository.Repository+.find*(..))")
    public void publicFindRepositoryMethodPointcut() {
    }

    @Around("publicFindRepositoryMethodPointcut()")
    public Object publicFindEntityRepositoryMethod(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Session session = entityManager.unwrap(Session.class);
            if (session != null && TenantContext.getTenantId() != null){
                Filter filter = session.enableFilter("tenantFilter");
                if (filter != null){
                    filter.setParameter("tenantId", TenantContext.getTenantId());
                }
            }
        }
        catch (Exception ex){
            throw new PersistenceException(ex.getMessage(), ex);
        }

        return pjp.proceed();
    }
}
