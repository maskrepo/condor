package fr.convergence.proddoc.services.rest.client

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import java.io.ByteArrayInputStream
import java.io.UnsupportedEncodingException
import java.util.concurrent.TimeoutException
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

@ApplicationScoped
class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisReactiveService::class.java)
    }

    fun getPDFbyNumGestion(
        nogest: String, avecApostille: Boolean = false,
        avecSceau: Boolean = false, avecSignature: Boolean = false
    ): ByteArrayInputStream {

        LOG.debug("Kbis demandé : $nogest / apostille=$avecApostille / sceau=$avecSceau / signature=$avecSignature")

        //Appel non-bloquant au WS myGreffe
        val client = WebClient.create(Vertx.vertx())

        //@TODO appliquer recommandation un client unique pour toute l'appli au lieu de l'instancier à chaque appel de la fonction
        val protocol = "http"
        val port = 8880
        val timeOut :Long = 10000
        val host = "172.31.4.97"
        val urlPath = "convergence-greffe-web/rest/kbis/recupererPdf"

        val uriWSKbis = UriBuilder.fromUri("$protocol://$host:$port/$urlPath")
            .queryParam("numeroGestion", nogest)
            .queryParam("apostille", avecApostille.toString())
            .queryParam("sceau", avecSceau.toString())
            .queryParam("signature", avecSignature.toString())
            .build()
        LOG.debug("uriWSKbis : $uriWSKbis")

        val pdf = try {
            client
                .getAbs(uriWSKbis.toASCIIString())
                .timeout(timeOut)
                .rxSend()
                .map {
                    // gestion spécifique des éventuelles erreurs qui nous intéressent
                    // (404, 500, TimeOut...)
                    if (it.statusCode()==Response.Status.NOT_FOUND.statusCode) {
                        throw NotFoundException(it.statusMessage())
                    }
                    else if (it.statusCode()==Response.Status.INTERNAL_SERVER_ERROR.statusCode) {
                        throw IllegalStateException(it.statusMessage())
                    }
                    else {
                        // si on reçoit de l'octet-stream on récupère le strema sous forme de bytes
                        if (it.getHeader(HttpHeaders.CONTENT_TYPE) == MediaType.APPLICATION_OCTET_STREAM){
                            it.bodyAsBuffer().bytes
                        }
                        else {
                            throw UnsupportedEncodingException("WS Kbis répond avec un Content-Type non géré")
                        }
                    }
                }
                .blockingGet()  // je ne trouve pas comment *ne pas* utiliser blockingGet qui semblerait bloquer le thread complet
        } catch (e: Exception) {
            if (e.cause is TimeoutException) {
                LOG.error("Timeout sur l'appel au WS Kbis myGreffe")
                throw TimeoutException(e.message)
            }
            if (e is NotFoundException) {
                LOG.error("Kbis non trouvé lors de l'appel au WS Kbis myGreffe")
                throw NotFoundException(e.message)
            }
            if (e is IllegalStateException) {
                LOG.error("Kbis impossible à télécharger le WS Kbis est en erreur.")
                throw NotFoundException(e.message)
            }else {
                throw (e)
            }
        }
        LOG.debug("getPDFbyNumGestion - sortie avec pdf de taille ${pdf.size}")
        val pdfStream = ByteArrayInputStream(pdf)
        return (pdfStream)
    }
}