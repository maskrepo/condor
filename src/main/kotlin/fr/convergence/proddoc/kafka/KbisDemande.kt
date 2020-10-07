package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import fr.convergence.proddoc.util.stinger.StingerUtil
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.io.InputStream
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class KbisDemande(
    @Inject val stingerUtil: StingerUtil,
    @Inject val kbisReactiveService: KbisReactiveService,
    @Inject val kbisReponse: KbisReponse
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

        //@TODO ces requires sont à basculer dans le maskIOHadler
        requireNotNull(messageIn.entete.typeDemande) { "message.entete.typeDemande est null" }
        requireNotNull(messageIn.objetMetier) { "message.objectMetier est null" }

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