package com.irunninglog.vertx.route;

import com.irunninglog.api.IFactory;
import com.irunninglog.api.IRequest;
import com.irunninglog.api.IResponse;
import com.irunninglog.security.AuthnRequest;
import com.irunninglog.security.AuthnResponse;
import com.irunninglog.service.*;
import com.irunninglog.vertx.security.AuthnVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRouteHandler<Q extends IRequest, S extends IResponse> implements Handler<RoutingContext> {

    private final Vertx vertx;
    private final IFactory factory;
    private final Class<Q> requestClass;
    private final Class<S> responseClass;
    private final Endpoint endpoint;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractRouteHandler(Vertx vertx, IFactory factory, Class<Q> requestClass, Class<S> responseClass) {
        this.vertx = vertx;
        this.factory = factory;
        this.requestClass = requestClass;
        this.responseClass = responseClass;

        RouteHandler routeHandler = this.getClass().getAnnotation(RouteHandler.class);
        this.endpoint = routeHandler.endpoint();
    }

    @Override
    public final void handle(RoutingContext routingContext) {
        long start = System.currentTimeMillis();

        try {
            logger.info("handle:start:{}", routingContext.normalisedPath());

            AuthnRequest authnRequest = new AuthnRequest()
                    .setToken(routingContext.request().getHeader("Authorization"))
                    .setPath(routingContext.normalisedPath())
                    .setEndpoint(endpoint());

            logger.info("handle:authn:{}", authnRequest);

            vertx.eventBus().<String>send(AuthnVerticle.ADDRESS, Json.encode(authnRequest), result -> {
                        if (result.succeeded()) {
                            String resultString = result.result().body();

                            logger.info("handle:{}:{}", AuthnVerticle.ADDRESS, resultString);

                            AuthnResponse authnResponse = Json.decodeValue(resultString, AuthnResponse.class);

                            logger.info("handle:{}:{}", AuthnVerticle.ADDRESS, authnResponse.getStatus());

                            if (authnResponse.getStatus() == ResponseStatus.Ok) {
                                routingContext.put("user", authnResponse.getBody());
                                handleAuthenticated(routingContext);
                            } else {
                                fail(routingContext, authnResponse.getStatus());
                            }
                        } else {
                            logger.error("handle:authn:failure", result.cause());
                            logger.error("handle:authn:failure{}", routingContext.normalisedPath());

                            fail(routingContext, ResponseStatus.Error);
                        }
                    });
        } finally {
            logger.info("handle:end:{}:{}ms", routingContext.normalisedPath(), System.currentTimeMillis() - start);
        }
    }

    private void handleAuthenticated(RoutingContext routingContext) {
        logger.info("handleAuthenticated:start:{}", routingContext.normalisedPath());

        try {
            Q request = factory.get(requestClass);

            request(request, routingContext);

            handle(routingContext, request);
        } catch (ResponseStatusException ex) {
            fail(routingContext, ResponseStatus.NotFound);
        }
    }

    protected abstract void request(Q request, RoutingContext routingContext);

    private void handle(RoutingContext routingContext, Q request) {
        String offsetString = routingContext.request().getHeader("iRunningLog-Utc-Offset");
        int offset = offsetString == null ? 0 : Integer.parseInt(offsetString);
        request.setOffset(offset);

        String requestString = Json.encode(request);

        logger.info("handleAuthenticated:{}:{}", endpoint.getAddress(), requestString);

        vertx.eventBus().<String>send(endpoint.getAddress(), requestString, result -> {
            if (result.succeeded()) {
                String resultString = result.result().body();

                logger.info("handleAuthenticated:{}:{}", endpoint.getAddress(), resultString);

                S response = Json.decodeValue(resultString, responseClass);

                logger.info("handle:{}:{}", endpoint.getAddress(), response);

                if (response.getStatus() == ResponseStatus.Ok) {
                    succeed(routingContext, response);
                } else {
                    fail(routingContext, response.getStatus());
                }
            } else {
                logger.error("handleAuthenticated:failure", result.cause());
                logger.error("handleAuthenticated:failure{}", routingContext.normalisedPath());

                fail(routingContext, ResponseStatus.Error);
            }
        });
    }

    private void succeed(RoutingContext routingContext, IResponse response) {
        routingContext.request().response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json")
                .setStatusCode(response.getStatus().getCode())
                .write(Json.encode(response.getBody()))
                .end();
    }

    private void fail(RoutingContext routingContext, ResponseStatus error) {
        logger.error("fail:{}:{}", routingContext.normalisedPath(), error);

        routingContext.request().response().setChunked(true)
                .setStatusCode(error.getCode())
                .write(error.getMessage())
                .end();
    }

    public final Endpoint endpoint() {
        return endpoint;
    }

}
