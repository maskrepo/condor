package fr.convergence.proddoc.controller

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import javax.enterprise.context.ApplicationScoped;
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ApplicationScoped
public class MyDeclarativeRoutes {

    var kbisSrv: KbisReactiveService? = KbisReactiveService()

    @Route(methods = arrayOf(HttpMethod.GET))
    fun hello(rc :RoutingContext) {
        rc.response().end("hello c'est la classe")
    }

    @Route(path = "/greetings", methods = arrayOf(HttpMethod.GET))
    fun greetings(ex :RoutingExchange)
    {
        ex.ok("hello " + ex.getParam("name").orElse("world"))
    }

    @Route(path = "/kbis", methods = arrayOf(HttpMethod.GET))
    fun kbis(ex :RoutingExchange)
    {

        var numgestion = ex.getParam("numgestion").orElse("toto")
        println("appel de KbisReactiveService.getXMLbyNumGestion $numgestion")
        runBlocking {
            launch {
                var retour = kbisSrv?.getXMLbyNumGestion(numgestion)
                println("fin appel de KbisReactiveService.getXMLbyNumGestion avec retour = $retour")
                ex.ok(retour)
            }
        }
    }
}