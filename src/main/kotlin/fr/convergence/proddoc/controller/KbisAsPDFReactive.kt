package fr.convergence.proddoc.controller

import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.axle.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
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

    @Route(path = "/pdfnumgestionreactive", methods = [HttpMethod.GET], produces = arrayOf("application/pdf"))
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
                ex.response().putHeader(HttpHeaders.CONTENT_LENGTH, (pdfbyNumGestion?.size).toString())
                ex.response().setChunked(true)
                ex.response().write(io.vertx.core.buffer.Buffer.buffer(pdfbyNumGestion))
                ex.ok()
                ex.response().end()
//                ex.response().close() pas de close sinon on a un souci côté affichage du PDF ça affiche le pdf précédant
                // autre chose pour singlaer la fin du stream ?
                //                ex.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/pdf")
            } else ex.ok("Numero de gestion invalide")
            /*
        return (xmLbyNumGestion) */
            //@TODO retourner une response http je crois ?
    }
}
