package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.FichierStocke
import fr.convergence.proddoc.model.metier.KbisRetour
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.slf4j.LoggerFactory.getLogger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class KbisReponse {

    companion object {
        private val LOG = getLogger(KbisReponse::class.java)
    }

    @Inject
    @Channel("kbis_reponse")
    var kbisReponseEmitter: Emitter<MaskMessage>? = null

    /**
     * traite l'évènement de statut de mise dans le cache du fichier
     * et répond à la demande initiale du Kbis en publiant un message qui contient son URL
     */
    fun traitementEvenementKbisDansCache(messageIn: MaskMessage) {

        var messageOut: MaskMessage = messageIn
        GlobalScope.launch {
            try {
                // on ve va traiter le message qui passe sur le topic STOCKER_FICHIER_REPONSE
                // que s'il s'agit d'un message qui nous est adressé à nous c-a-d typeDemande = KBIS
                val typeDemande = messageIn.entete.typeDemande
                val idMessage = messageIn.entete.idUnique
                if (messageIn.entete.typeDemande == "KBIS") {
                    val fichierEnCache = messageIn.recupererObjetMetier<FichierStocke>()
                    val urlKbis = fichierEnCache.urlAcces
                    LOG.debug("Réception évènement Kbis stocké : ${fichierEnCache.urlAccesNavigateur}")
                    messageOut =
                        MaskMessage.reponseOk(KbisRetour(urlKbis).toString(), messageIn, messageIn.entete.idReference)
                } else {
                    // pour l'instant répondre "non traitable par Condor"
                    LOG.warn("Message $idMessage de type $typeDemande non traitable par service Kbis (Condor n'est probablement pas le destinataire)")
                    messageOut = MaskMessage.reponseOk(
                        KbisRetour("Message $idMessage de type $typeDemande non traitable par service Kbis (Condor)"),
                        messageIn,
                        messageIn.entete.idReference
                    )
                }
            } catch (ex: Exception) {
                messageOut = MaskMessage.reponseKo<Exception>(ex, messageIn, messageIn.entete.idReference)
            } finally {
                retour(messageOut)
            }
        }
    }

    private suspend fun retour(message: MaskMessage) {
        LOG.info("Reponse asynchrone = $message")
        kbisReponseEmitter!!.send(message)
    }
}