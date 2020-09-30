package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.FichierAccessible
import fr.convergence.proddoc.model.metier.KbisRetour
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class KbisReponse {

    companion object {
        private val LOG: Logger = getLogger(KbisReponse::class.java)
    }

    @Inject
    @field: Channel("kbis_reponse")
    val retourEmitter: Emitter<MaskMessage>? = null

    /**
     * traite l'évènement de statut de mise dans le cache du fichier
     * et répond à la demande initiale du Kbis en publiant un message qui contient son URL
     */
    @Incoming("stocker_fichier_reponse")
    fun traitementEvenementKbisDansCache(messageIn: MaskMessage) {

        //@TODO ces requires sont à basculer dans le maskIOHadler
        requireNotNull(messageIn.entete.typeDemande) { "messageIn.entete.typeDemande est null" }
        requireNotNull(messageIn.objetMetier) { "messageIn.objectMetier est null" }

        var messageOut: MaskMessage = messageIn
        GlobalScope.launch {
            try {
                // on ve va traiter le message qui passe sur le topic STOCKER_FICHIER_REPONSE
                // que s'il s'agit d'un message qui nous est adressé à nous c-a-d typeDemande = KBIS
                val typeDemande = messageIn.entete.typeDemande
                val idMessage = messageIn.entete.idUnique
                if (messageIn.entete.typeDemande == "KBIS") {
                    val fichierEnCache = messageIn.recupererObjetMetier<FichierAccessible>()
                    val urlKbis = fichierEnCache.messageRetour
                    LOG.debug("Réception évènement Kbis stocké : $urlKbis")
                    messageOut = MaskMessage.reponseOk(KbisRetour(urlKbis).toString(), messageIn)
                } else {
                    // pour l'instant répondre "non traitable par Condor"
                    LOG.warn("Message $idMessage de type $typeDemande non traitable par service Kbis (Condor n'est probablement pas le destinataire)")
                    messageOut = MaskMessage.reponseOk(KbisRetour("Message $idMessage de type $typeDemande non traitable par service Kbis (Condor)"), messageIn)
                }
            } catch (ex: Exception) {
                messageOut = MaskMessage.reponseKo<Exception>(ex, messageIn)
            } finally {
                retour(messageOut)
            }
        }
    }

    private suspend fun retour(message: MaskMessage) {
        LOG.info("Reponse asynchrone = $message")
        retourEmitter?.send(message)
    }
}