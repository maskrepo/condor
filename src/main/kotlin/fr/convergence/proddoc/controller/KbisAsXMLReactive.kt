package fr.convergence.proddoc.controller

import fr.convergence.proddoc.libs.model.Kbis
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;
import javax.enterprise.context.ApplicationScoped;
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import fr.convergence.proddoc.services.rest.client.KbisService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ApplicationScoped
class KbisAsXMLReactive {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsXMLReactive::class.java)
    }

    var kbisSrv = KbisReactiveService()

    @Route(path = "/numgestionreactive", methods = arrayOf(HttpMethod.GET))
    fun numGestionKbis(ex :RoutingExchange)  {

        val numgestion :String = ex.getParam("numgestion").orElse("empty")
        LOG.info("Recuperation paramètre numgestion : $numgestion")

        if (!numgestion.equals("empty")) {
            var xmLbyNumGestion : String? = null
            runBlocking {
                launch {
                    LOG.info("Appel de getXMLbyNumGestion")
                    xmLbyNumGestion = kbisSrv?.getXMLbyNumGestion(numgestion)
                    LOG.info("Valeur de XMLbyNumGestion : $xmLbyNumGestion")
                }
            }
            ex.ok("$xmLbyNumGestion")
        }
        else ex.ok("Numero de gestion invalide")
       /*
        return (xmLbyNumGestion) */
        //@TODO retourner une response http je crois ?
    }
}