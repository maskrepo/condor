package fr.convergence.proddoc.services.rest.client

import fr.convergence.proddoc.util.WSUtils
import io.vertx.core.logging.LoggerFactory.getLogger
import java.io.InputStream
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.MediaType


@ApplicationScoped
class KbisReactiveService {

    companion object {
        private val LOG = getLogger(KbisReactiveService::class.java)
    }

    /**
     * récupère un kbis PDF auprès de myGreffe
     * retourne un ByteArrayInputStream ou lève une exception
     */
    fun getPDFbyNumGestion(
        nogest: String, avecApostille: Boolean = false,
        avecSceau: Boolean = false, avecSignature: Boolean = false
    ): InputStream {

        LOG.debug("Kbis demandé : $nogest / apostille=$avecApostille / sceau=$avecSceau / signature=$avecSignature")

        // fabrication de l'URI à appeler
        val uriCible = WSUtils.fabriqueURI(
            "/kbis", WSUtils.TypeRetourWS.PDF,
            mapOf(
                "numeroGestion" to nogest, "apostille" to avecApostille.toString(),
                "sceau" to avecSceau.toString(), "signature" to avecSignature.toString()
            )
        )

        // Appel de l'URI
        return WSUtils.appelleURI(uriCible, 10000, MediaType.APPLICATION_OCTET_STREAM).readEntity(InputStream::class.java)
    }

}