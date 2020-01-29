package persistence.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryAspectConfiguration {

    @Bean
    public RepositoryAspect repositoryAspect() {
        return new RepositoryAspect();
    }
}
