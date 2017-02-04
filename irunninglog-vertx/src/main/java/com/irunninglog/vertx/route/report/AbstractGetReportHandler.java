package com.irunninglog.vertx.route.report;

import com.irunninglog.api.IFactory;
import com.irunninglog.api.IResponse;
import com.irunninglog.api.report.IGetReportRequest;
import com.irunninglog.vertx.route.AbstractProfileIdRouteHandler;
import io.vertx.core.Vertx;

abstract class AbstractGetReportHandler<T extends IResponse> extends AbstractProfileIdRouteHandler<IGetReportRequest, T> {

    AbstractGetReportHandler(Vertx vertx, IFactory factory, Class<T> responseClass) {
        super(vertx, factory, IGetReportRequest.class, responseClass);
    }

}
