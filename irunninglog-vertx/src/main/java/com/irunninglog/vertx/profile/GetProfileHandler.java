package com.irunninglog.vertx.profile;

import com.irunninglog.api.Endpoint;
import com.irunninglog.api.IRequest;
import com.irunninglog.api.factory.IFactory;
import com.irunninglog.api.mapping.IMapper;
import com.irunninglog.vertx.AbstractRouteHandler;
import com.irunninglog.vertx.RouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

@RouteHandler(endpoint = Endpoint.GET_PROFILE)
public final class GetProfileHandler extends AbstractRouteHandler {

    public GetProfileHandler(Vertx vertx, IFactory factory, IMapper mapper) {
        super(vertx, factory, mapper);
    }

    @Override
    protected void request(IRequest request, RoutingContext routingContext) {
        super.request(request, routingContext);

        request.getMap().put("token", routingContext.request().getHeader("Authorization"));
    }

}
