package fr.convergence.proddoc.services.rest.client

import fr.convergence.proddoc.controller.KbisAsXMLReactive
import io.reactivex.Single
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.xml.crypto.dsig.XMLObject


class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsXMLReactive::class.java)
    }

    fun getXMLbyNumGestion(nogest: String): String {

        LOG.info("entree dans KbisReactiveService.getXMLbyNumGestion")
        val client = WebClient.create(Vertx.vertx())
        var xml = client
                .get(80, "172.31.5.20", "/convergence-greffe-web/rest/kbis/search?numGestion=$nogest&format=xml")
                .rxSend()
                .map { it.bodyAsString() }
                .blockingGet()  // je ne trouve pas comment *ne pas* utiliser blockingGet qui semblerait bloquer le thread complet
                                // il y a subscribe mais je n'arrive pas à l'utilise

        //@TODO kbis récupéré à déposer dans dans solr avec sol4j
        //@TODO rappel kafka pour indiquer que kbis récupéré et déposé dans solr OK

        LOG.info("sortie de KbisReactiveService.getXMLbyNumGestion avec xml : $xml")
        return (xml)
    }

}
