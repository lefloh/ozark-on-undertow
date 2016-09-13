package de.utkast.ozark;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.ozark.servlet.OzarkContainerInitializer;
import org.jboss.weld.servlet.WeldInitialListener;
import org.jboss.weld.servlet.WeldTerminalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * Main App.
 *
 * @author Libor Kramoliš
 * @author Florian Hirsch
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private static CdiContainer init() throws ServletException, NoSuchMethodException {
        CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot();
        cdiContainer.getContextControl().startContext(ApplicationScoped.class);
        initServlet();
        return cdiContainer;
    }

    private static void destroy(CdiContainer cdiContainer) {
        cdiContainer.getContextControl().stopContext(ApplicationScoped.class);
        cdiContainer.shutdown();
    }

    public static void main(String[] args) throws InterruptedException, ServletException, NoSuchMethodException {
        long start = System.currentTimeMillis();
        CdiContainer cdiContainer = null;
        try {
            cdiContainer = init();
            LOG.info("MVC Application started in {}ms. Stop the application using CTRL+C", System.currentTimeMillis() - start);
            LOG.info("Entry point listening on http://{}:{}", HOST, PORT);
            Thread.currentThread().join();
        } catch (Exception ex) {
            LOG.error("Error running MVC Application", ex);
        } finally {
            destroy(cdiContainer);
        }
    }

    private static void initServlet() throws ServletException, NoSuchMethodException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        DeploymentInfo deploymentInfo = new DeploymentInfo()
                .addListener(Servlets.listener(WeldInitialListener.class))
                .addListener(Servlets.listener(WeldTerminalListener.class))
                .setContextPath("/")
                .setDeploymentName("ozark-on-undertow")
                .addServlet(
                        createServletInfo("/r/*", "JAX-RS Resources", org.glassfish.jersey.servlet.ServletContainer.class)
                )
                .addServletContextAttribute(OzarkContainerInitializer.APP_PATH_CONTEXT_KEY, "/r")
                .addWelcomePage("index.html")
                .setResourceManager(new ClassPathResourceManager(classLoader, "META-INF/webapp"))
                .setClassIntrospecter(CdiClassIntrospecter.INSTANCE)
                .setAllowNonStandardWrappers(true)
                .setClassLoader(classLoader);

        ServletContainer servletContainer = Servlets.defaultContainer();
        DeploymentManager deploymentManager = servletContainer.addDeployment(deploymentInfo);
        deploymentManager.deploy();

        Undertow server = Undertow.builder()
                .addHttpListener(PORT, HOST)
                .setHandler(deploymentManager.start())
                .build();
        server.start();
    }

    private static ServletInfo createServletInfo(String mapping, String servletName, Class<? extends Servlet> servlet)
            throws NoSuchMethodException {
        ServletInfo servletInfo = Servlets
                .servlet(servletName, servlet)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addMapping(mapping);
        servletInfo.setInstanceFactory(CdiClassIntrospecter.INSTANCE.createInstanceFactory(servlet));
        return servletInfo;
    }

    @Produces
    @ApplicationScoped
    public org.glassfish.jersey.servlet.ServletContainer createServletContainer() {
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property("javax.mvc.engine.ViewEngine.viewFolder", "META-INF/views/");
        return new org.glassfish.jersey.servlet.ServletContainer(resourceConfig);
    }

}
