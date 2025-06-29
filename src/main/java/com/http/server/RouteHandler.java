package com.http.server;

import com.http.HttpMethod;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class RouteHandler {
    private final Map<RequestMap, Function<HttpServerExchange, Object>> routeMaps = new HashMap<>();
    private Function<HttpServerExchange, Object> notFoundHandler;

    public Map<RequestMap, Function<HttpServerExchange, Object>> getRouteMaps() {
        return routeMaps;
    }

    public RouteHandler get(String route, Function<HttpServerExchange, Object> handler) {
        routeMaps.put(new RequestMap(HttpMethod.GET, route), handler);
        return this;
    }

    public RouteHandler put(String route, Function<HttpServerExchange, Object> handler) {
        routeMaps.put(new RequestMap(HttpMethod.PUT, route), handler);
        return this;
    }

    public RouteHandler post(String route, Function<HttpServerExchange, Object> handler) {
        routeMaps.put(new RequestMap(HttpMethod.POST, route), handler);
        return this;
    }

    public RouteHandler patch(String route, Function<HttpServerExchange, Object> handler) {
        routeMaps.put(new RequestMap(HttpMethod.PATCH, route), handler);
        return this;
    }

    public RouteHandler delete(String route, Function<HttpServerExchange, Object> handler) {
        routeMaps.put(new RequestMap(HttpMethod.DELETE, route), handler);
        return this;
    }

    public RouteHandler setNotFoundHandler(Function<HttpServerExchange, Object> handler) {
        this.notFoundHandler = handler;
        return this;
    }

    public Optional<Function<HttpServerExchange, Object>> matchRoute(HttpServerExchange request) {
        RequestMap req = new RequestMap(HttpMethod.valueOf(request.getRequestMethod().toString().toUpperCase()), request.getRequestPath());
        return Optional.ofNullable(routeMaps.getOrDefault(req, this.notFoundHandler));
    }


    class RequestMap {
        HttpMethod method;
        String route;

        public RequestMap(HttpMethod method, String route) {
            this.method = method;
            this.route = route;
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, route);
        }

        @Override
        public boolean equals(Object o) {
            return Objects.equals(method, ((RequestMap) o).method) && Objects.equals(route, ((RequestMap) o).route);
        }
    }
}
