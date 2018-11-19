package com.rbkmoney.machinarium;

import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;

import javax.servlet.Servlet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class AbstractTest {

    private HandlerCollection handlerCollection;
    private Server server;

    protected int serverPort = 8080;

    protected void addServlets(Map<String, Servlet> servlets) {
        try {
            ServletContextHandler context = new ServletContextHandler();
            for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
                ServletHolder defaultServ = new ServletHolder(entry.getKey(), entry.getValue());
                context.addServlet(defaultServ, entry.getKey());
            }
            handlerCollection.addHandler(context);
            context.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Servlet createThriftRPCService(Class<T> iface, T handler) {
        THServiceBuilder serviceBuilder = new THServiceBuilder();
        return serviceBuilder.build(iface, handler);
    }

    protected URI buildURI(String path) throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + path);
    }

    protected void startServer() throws Exception {
        server = new Server(serverPort);
        HandlerCollection contextHandlerCollection = new HandlerCollection(true);
        this.handlerCollection = contextHandlerCollection;
        server.setHandler(contextHandlerCollection);

        server.start();
    }

    @After
    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
