package com.http.server;

import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;

public class HttpServer {
    public static void start(RouteHandler routes) {
        Undertow.builder().addHttpListener(8001, "0.0.0.0")
                .setHandler(new BlockingHandler(new ServerHandler(routes))).build().start();
    }
}
