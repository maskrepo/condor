package fr.convergence.proddoc.controller

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyDeclarativeRoutes {
    // neither path nor regex is set - match a path derived from the method name

    @Route(methods = arrayOf(HttpMethod.GET))
    fun hello(rc :RoutingContext) {
        rc.response().end("hello c'est la classe")
    }

  /*  @Route(path = "/world")
    fun helloWorld():String? {
        //return ("Hello world!")
        return (null)
    }
*/
    @Route(path = "/greetings", methods = arrayOf(HttpMethod.GET))
    fun greetings(ex :RoutingExchange)
    {
        ex.ok("hello " + ex.getParam("name").orElse("world"))
    }
}