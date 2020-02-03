package persistence.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class H2TCPServer implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(H2TCPServer.class);

    private Object server;

    @Override
    public void destroy() throws Exception {

        if (server == null) {
            logger.info("[H2TCPServer] is not running, ignoring stop operation!");
            return;
        }

        ReflectionUtils.findMethod(server.getClass(), "stop").invoke(server, new Object[] {});

        logger.info("[H2TCPServer] destroyed ......");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> serverClass = Class.forName("org.h2.tools.Server", true, loader);
            Method createServer = serverClass.getMethod("createTcpServer", String[].class);
            server =  createServer.invoke(null,
                    new Object[] { new String[] { "-tcp", "-tcpAllowOthers"} });

            ReflectionUtils.findMethod(server.getClass(), "start").invoke(server, new Object[] {});
            logger.info("[H2TCPServer] started ......");

        } catch (ClassNotFoundException | LinkageError  e) {
            throw new RuntimeException("Failed to load and initialize org.h2.tools.Server", e);

        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to get method org.h2.tools.Server.createTcpServer()", e);

        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to invoke org.h2.tools.Server.createTcpServer()", e);

        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof SQLException) {
                throw (SQLException) t;
            }
            throw new RuntimeException("Unchecked exception in org.h2.tools.Server.createTcpServer()", t);
        }
    }
}
