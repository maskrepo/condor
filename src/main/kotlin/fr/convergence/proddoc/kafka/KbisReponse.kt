package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.FichierAccessible
import fr.convergence.proddoc.model.metier.KbisRetour
import fr.convergence.proddoc.util.maskIOHandler
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KbisReponse {

    companion object {
        private val LOG: Logger = getLogger(KbisReponse::class.java)
    }

    @Incoming("stocker_fichier_reponse")
    @Outgoing("kbis_reponse")
    fun traitementEvenementKbisDansCache(message: MaskMessage): MaskMessage = maskIOHandler(message) {

        //@TODO ces requires sont à basculer dans le maskIOHadler
        requireNotNull(message.entete.typeDemande) { "message.entete.typeDemande est null" }
        requireNotNull(message.objetMetier) { "message.objectMetier est null" }

        val fichierCache = message.recupererObjetMetier<FichierAccessible>()
        val urlKbis = fichierCache.messageRetour
        LOG.debug("Réception évènement Kbis dans cache : $urlKbis")

        KbisRetour(urlKbis).toString()
    }
}