package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import fr.convergence.proddoc.util.stinger.StingerUtil
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.slf4j.LoggerFactory.getLogger
import java.io.InputStream
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class KbisDemande(
    @Inject var stingerUtil: StingerUtil,
    @Inject var kbisReactiveService: KbisReactiveService,
    @Inject var kbisReponse: KbisReponse
) {

    companion object {
        private val LOG = getLogger(KbisDemande::class.java)
    }

    /**
     * si un MaskMessage arrive sur le topic "KBIS_DEMANDE" (Incoming) :
     * fait le "passe-plat" et publie un message de demande de stockage de fichier a Stinger
     **/
    @Incoming("kbis_demande")
    fun traiterEvenementDemandeKbis(messageIn: MaskMessage) {
        LOG.info("Réception d'une demande de génération de KBIS : $messageIn")

        stingerUtil.stockerResultatSurStinger(
            messageIn,
            this::getPDFbyMaskMessage,
            kbisReponse::traitementEvenementKbisDansCache,
            "application/pdf",
            messageIn.recupererObjetMetier<KbisDemande>().numeroGestion
        )
    }

    private fun getPDFbyMaskMessage(maskMessage: MaskMessage): InputStream {
        return with(maskMessage.recupererObjetMetier<KbisDemande>()) {
            kbisReactiveService.getPDFbyNumGestion(
                numeroGestion,
                avecApostille,
                avecSceau,
                avecSignature
            )
        }
    }
}