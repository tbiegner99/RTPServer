package com.http.server;

import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;

public class HttpServer {
    public static void start(Integer port, RouteHandler routes) {
        var handler = new RoutingHandler();
        for (var route : routes.getRouteMaps().entrySet()) {
            handler = handler.add(route.getKey().method.name(), route.getKey().route, new BlockingHandler(exchange -> {
                var func = route.getValue();
                func.apply(exchange);
            }));

        }
        Undertow.builder().addHttpListener(port, "0.0.0.0")
                .setHandler(handler)
                .build().start();
    }
}
