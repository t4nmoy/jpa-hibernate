package persistence.config;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.StopWatch;

public class AsyncSpringLiquibase extends SpringLiquibase {

    public static final String DISABLED_MESSAGE = "Liquibase is disabled";
    public static final String STARTING_ASYNC_MESSAGE =
            "Starting Liquibase asynchronously, your database might not be ready at startup!";
    public static final String STARTING_SYNC_MESSAGE = "Starting Liquibase synchronously";
    public static final String STARTED_MESSAGE = "Liquibase has updated your database in {} ms";
    public static final String EXCEPTION_MESSAGE = "Liquibase could not start correctly, your database is NOT ready: " +
            "{}";

    public static final long SLOWNESS_THRESHOLD = 5; // seconds
    public static final String SLOWNESS_MESSAGE = "Warning, Liquibase took more than {} seconds to start up!";

    // named "logger" because there is already a field called "log" in "SpringLiquibase"
    private final Logger logger = LoggerFactory.getLogger(AsyncSpringLiquibase.class);

    private final TaskExecutor taskExecutor;

    private final Environment env;

    public AsyncSpringLiquibase(@Qualifier("taskExecutor") TaskExecutor taskExecutor, Environment env) {
        this.taskExecutor = taskExecutor;
        this.env = env;
    }

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        if (!env.acceptsProfiles(Profiles.of("no-liquibase"))) {
            if (env.acceptsProfiles(Profiles.of("dev", "default"))) {
                taskExecutor.execute(() -> {
                    try {
                        logger.warn(STARTING_ASYNC_MESSAGE);
                        initDb();
                    } catch (LiquibaseException e) {
                        logger.error(EXCEPTION_MESSAGE, e.getMessage(), e);
                    }
                });
            } else {
                logger.debug(STARTING_SYNC_MESSAGE);
                initDb();
            }
        } else {
            logger.debug(DISABLED_MESSAGE);
        }
    }

    protected void initDb() throws LiquibaseException {
        StopWatch watch = new StopWatch();
        watch.start();
        super.afterPropertiesSet();
        watch.stop();
        logger.debug(STARTED_MESSAGE, watch.getTotalTimeMillis());
        if (watch.getTotalTimeMillis() > SLOWNESS_THRESHOLD * 1000L) {
            logger.warn(SLOWNESS_MESSAGE, SLOWNESS_THRESHOLD);
        }
    }
}