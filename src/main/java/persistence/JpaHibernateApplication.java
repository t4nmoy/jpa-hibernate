package persistence;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import persistence.config.AppProperties;
import persistence.config.AsyncSpringLiquibase;
import persistence.config.ExceptionHandlingAsyncTaskExecutor;
import persistence.config.H2TCPServer;
import persistence.repository.ExtendedBaseRepositoryImpl;

import javax.sql.DataSource;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableJpaRepositories(repositoryBaseClass = ExtendedBaseRepositoryImpl.class)
@EnableConfigurationProperties({AppProperties.class, LiquibaseProperties.class})
public class JpaHibernateApplication {

	private final Logger logger = LoggerFactory.getLogger(JpaHibernateApplication.class);

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(JpaHibernateApplication.class, args);
	}


	@Bean(name = "asyncTaskExecutor")
	public TaskExecutor getAsyncExecutor() {
		logger.debug("creating async task executor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(appProperties.getCorePoolSize());
		executor.setMaxPoolSize(appProperties.getMaxPoolSize());
		executor.setQueueCapacity(appProperties.getQueueCapacity());
		executor.setThreadNamePrefix("persistence-app-executor-");
		return new ExceptionHandlingAsyncTaskExecutor(executor);
	}

	@Profile({"dev", "default"})
	@Bean
	public Object h2TCPServer() {
		return new H2TCPServer();
	}

	@Bean
	public SpringLiquibase liquibase(@Qualifier("asyncTaskExecutor")TaskExecutor taskExecutor, DataSource dataSource,
									 LiquibaseProperties liquibaseProperties) {
		SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor, env);
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog("classpath:config/liquibase/master.xml");
		liquibase.setContexts(liquibaseProperties.getContexts());
		liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		if (env.acceptsProfiles(Profiles.of("no-liquibase"))) {
			liquibase.setShouldRun(false);
		} else {
			liquibase.setShouldRun(liquibaseProperties.isEnabled());
			logger.debug("configuring liquibase");
		}
		return liquibase;
	}

}
