package com.http.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ServerHandler implements HttpHandler {
    private RouteHandler routeHandler;

    public ServerHandler(RouteHandler routes) {
        routeHandler = routes;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        routeHandler.matchRoute(httpServerExchange).map(handler -> handler.apply(httpServerExchange));
    }
}
