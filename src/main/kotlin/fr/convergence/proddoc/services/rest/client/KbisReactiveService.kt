package fr.convergence.proddoc.services.rest.client

import fr.convergence.proddoc.util.WSUtils
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.streams.ReadStream
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType


@ApplicationScoped
class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisReactiveService::class.java)
    }

    /**
     * récupère un kbis PDF auprès de myGreffe
     * retourne un ByteArrayInputStream ou lève une exception
     */
    fun getPDFbyNumGestion(
        nogest: String, avecApostille: Boolean = false,
        avecSceau: Boolean = false, avecSignature: Boolean = false
    ): ByteArrayInputStream {

        LOG.debug("Kbis demandé : $nogest / apostille=$avecApostille / sceau=$avecSceau / signature=$avecSignature")

        // fabrication de l'URI à appeler
        val uriCible = WSUtils.fabriqueURI(
            "/kbis", WSUtils.TypeRetourWS.PDF,
            mapOf(
                "numeroGestion" to nogest, "apostille" to avecApostille.toString(),
                "sceau" to avecSceau.toString(), "signature" to avecSignature.toString()
            )
        )

        // appel à l'URI : en retour récupération d'une http Response qui doit contenir notre PDF
        return WSUtils.appelleURI(uriCible, 10000) {
            when (it.getHeader(HttpHeaders.CONTENT_TYPE)) {
                "application/pdf", MediaType.APPLICATION_OCTET_STREAM ->  it.bodyAsBuffer().bytes.inputStream()
                else -> throw(IllegalStateException("Erreur : le Kbis récupéré n'est pas au format binaire / pdf"))
            }
        }

    }

}