package persistence.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {

    static final String EXCEPTION_MESSAGE = "caught async exception";

    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor.class);

    private final AsyncTaskExecutor taskExecutor;

    public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void execute(Runnable task) {
        taskExecutor.execute(createWrappedRunnable(task));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        taskExecutor.execute(createWrappedRunnable(task), startTimeout);
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception ex) {
                handle(ex);
            }
        };
    }
    protected void handle(Exception ex) {
        logger.error(EXCEPTION_MESSAGE, ex);
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception ex) {
                handle(ex);
                throw ex;
            }
        };
    }

    @Override
    public Future<?> submit(Runnable task) {
        return taskExecutor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return taskExecutor.submit(task);
    }

    @Override
    public void destroy() throws Exception {
        if (taskExecutor instanceof DisposableBean) {
            DisposableBean bean = (DisposableBean) taskExecutor;
            bean.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (taskExecutor instanceof  InitializingBean) {
            InitializingBean bean = (InitializingBean) taskExecutor;
            bean.afterPropertiesSet();
        }
    }
}
