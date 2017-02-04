package com.irunninglog.vertx.endpoint;

import com.irunninglog.api.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestResponseVerticle<Q extends IRequest, S extends IResponse> extends AbstractVerticle {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final IFactory factory;
    private final Class<S> responseClass;
    private final Class<Q> requestClass;

    protected AbstractRequestResponseVerticle(IFactory factory, Class<Q> requestClass, Class<S> responseClass) {
        super();

        this.factory = factory;
        this.responseClass = responseClass;
        this.requestClass = requestClass;
    }

    @Override
    public final void start() throws Exception {
        logger.info("start:start");

        super.start();

        vertx.eventBus().<String>consumer(address()).handler(handler());

        logger.info("start:end");
    }

    private Handler<Message<String>> handler() {
        return msg -> vertx.<String>executeBlocking(future -> {
                    long start = System.currentTimeMillis();

                    logger.info("handler:{}:{}", address(), msg.body());

                    try {
                        logger.info("handler:start");

                        Q request = Json.decodeValue(msg.body(), requestClass);

                        logger.info("handler:request:{}", request);

                        S response = factory.get(responseClass);
                        handle(request, response);

                        logger.info("handler:response:{}", response);

                        future.complete(Json.encode(response));
                    } catch (Exception ex) {
                        logger.error("handler:exception:{}", ex);

                        S response = fromException(ex);

                        logger.error("handler:exception:{}", response);

                        future.complete(Json.encode(response));
                    } finally {
                        logger.info("handler:{}:{}ms", address(), System.currentTimeMillis() - start);
                    }
                },
                result -> msg.reply(result.result()));
    }

    protected abstract String address();

    protected abstract void handle(Q request, S response);

    private S fromException(Exception ex) {
        S response = factory.get(responseClass);

        ResponseStatus status;
        boolean statusException = ex instanceof ResponseStatusException;

        logger.info("fromException:{}:{}", statusException, ex.getClass());

        if (statusException) {
            status = ((ResponseStatusException) ex).getResponseStatus();
        } else {
            status = ResponseStatus.Error;
        }

        logger.info("fromException:{}", status);

        response.setStatus(status);

        return response;
    }

}
