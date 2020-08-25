package fr.convergence.proddoc.services.rest.client

import fr.convergence.proddoc.controller.KbisAsXML
import fr.convergence.proddoc.controller.KbisAsXMLReactive
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Future
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient

class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsXMLReactive::class.java)
    }

    fun getXMLbyNumGestion(nogest: String) : String {

        LOG.info("entree dans KbisReactiveService.getXMLbyNumGestion")
        var xml :String = "noKbis"



            var client = WebClient.create(Vertx.vertx())
            client.get(80, "172.31.5.20", "/convergence-greffe-web/rest/kbis/search?numGestion=$nogest&format=xml")
                .send({ ar ->
                    if (ar.succeeded()) {
                        // Obtain response
                        var response = ar.result()
                        xml = response.bodyAsString()
                        LOG.info("Received response with status code ${response.statusCode()} and body ${response.bodyAsString()}")

                    } else {
                        xml = "KO"
                        LOG.info("Something went wrong ${ar.cause().message}")
                    }
                })
            LOG.info("sortie de KbisReactiveService.getXMLbyNumGestion")

        return (xml)
    }

}
