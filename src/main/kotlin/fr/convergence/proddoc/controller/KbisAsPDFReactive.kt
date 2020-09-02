package fr.convergence.proddoc.controller

import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.core.http.HttpMethod
import javax.enterprise.context.ApplicationScoped
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import kotlinx.coroutines.*

@ApplicationScoped
class KbisAsPDFReactive {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsPDFReactive::class.java)
    }

    var kbisSrv = KbisReactiveService()

    @Route(path = "/pdfnumgestionreactive", methods = [HttpMethod.GET])
    fun numGestionKbis(ex :RoutingExchange)  {

        val numgestion :String = ex.getParam("numgestion").orElse("empty")
        LOG.info("Recupération paramètre numgestion : $numgestion")
            if (numgestion != "empty") {
                var pdfbyNumGestion : ByteArray? = null
                runBlocking {
                    launch {
                        LOG.info("Appel de getPDFbyNumGestion")
                        pdfbyNumGestion = kbisSrv.getPDFbyNumGestion(numgestion)
                    }
                }
                ex.ok("$pdfbyNumGestion")
            } else ex.ok("Numero de gestion invalide")
            /*
        return (xmLbyNumGestion) */
            //@TODO retourner une response http je crois ?
    }
}