package com.irunninglog.vertx.http;

import com.irunninglog.vertx.routehandler.GetDashboardHandler;
import com.irunninglog.vertx.routehandler.GetProfileHandler;
import com.irunninglog.vertx.routehandler.IRouteHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

    private final int listenPort;

    public ServerVerticle(int listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public void start() throws Exception {
        LOG.info("start:start");

        super.start();

        httpServer();

        LOG.info("start:end");
    }

    private void httpServer() {
        LOG.info("httpServer:start");
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        install(router, new GetProfileHandler(vertx));
        install(router, new GetDashboardHandler(vertx));

        LOG.info("httpServer:listen:before:{}", listenPort);
        server.requestHandler(router::accept).listen(listenPort);
        LOG.info("httpServer:listen:after");

        LOG.info("httpServer:end");
    }

    private void install(Router router, IRouteHandler handler) {
        LOG.info("httpServer:{}:before", handler.getClass());
        router.route(handler.method(), handler.path()).handler(handler);
        LOG.info("httpServer:{}:after", handler.getClass());
    }

}