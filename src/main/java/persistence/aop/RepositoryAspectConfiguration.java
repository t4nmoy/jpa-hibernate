package persistence.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class RepositoryAspectConfiguration {

    @Bean
    public RepositoryAspect repositoryAspect() {
        return new RepositoryAspect();
    }
}
