package fr.convergence.proddoc.services.rest.client

import fr.convergence.proddoc.util.WSUtils
import org.slf4j.LoggerFactory.getLogger
import java.io.InputStream
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.MediaType


@ApplicationScoped
class KbisReactiveService {

    companion object {
        private val LOG = getLogger(KbisReactiveService::class.java)
    }

    /**
     * récupère un kbis PDF auprès de myGreffe qui comprend
     * la demande avec un numéro de gestion
     * retourne un InputStream ou lève une exception
     */
    fun getPDFbyNumGestion(
        nogest: String, avecApostille: Boolean = false,
        avecSceau: Boolean = false, avecSignature: Boolean = false
    ): InputStream {

        LOG.debug("Kbis demandé : $nogest / apostille=$avecApostille / sceau=$avecSceau / signature=$avecSignature")

        // appel à myGreffe avec tous les paramètres qui vont bien
        return WSUtils.demandeRestURLmyGreffe("/kbis/recupererPdf",
            mapOf(
                "numeroGestion" to nogest, "apostille" to avecApostille,
                "sceau" to avecSceau, "signature" to avecSignature
            ),5000,MediaType.APPLICATION_OCTET_STREAM)
            .readEntity(InputStream::class.java)

    }

    /**
     * Récupère un kbis PDF auprès du service myGreffe qui comprend
     * la demande avec un identifiant regitre
     * retoure un InpuStream ou lèvre une exception
     */
    fun getPDFbyIdentifiantRegistre(
        identifiantRegistre: Long, avecApostille: Boolean = false,
        avecSceau: Boolean = false, avecSignature: Boolean = false
    ): InputStream {

        LOG.debug("Kbis demandé : $identifiantRegistre / apostille=$avecApostille / sceau=$avecSceau / signature=$avecSignature")

        // appel à myGreffe avec tous les paramètres qui vont bien
        return WSUtils.demandeRestURLmyGreffe("/kbis/recupererPdf",
            mapOf(
                "identifiantRegistre" to identifiantRegistre, "apostille" to avecApostille,
                "sceau" to avecSceau, "signature" to avecSignature
            ),5000,MediaType.APPLICATION_OCTET_STREAM)
            .readEntity(InputStream::class.java)

    }


}