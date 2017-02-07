package com.irunninglog.vertx.route.report;

import com.irunninglog.api.factory.IFactory;
import com.irunninglog.api.mapping.IMapper;
import com.irunninglog.api.report.IGetDataSetResponse;
import com.irunninglog.api.Endpoint;
import com.irunninglog.vertx.route.RouteHandler;
import io.vertx.core.Vertx;

@RouteHandler(endpoint = Endpoint.GetMileageByRoute)
public final class GetMileageByRouteHandler extends AbstractGetReportHandler<IGetDataSetResponse> {

    public GetMileageByRouteHandler(Vertx vertx, IFactory factory, IMapper mapper) {
        super(vertx, factory, mapper, IGetDataSetResponse.class);
    }

}
