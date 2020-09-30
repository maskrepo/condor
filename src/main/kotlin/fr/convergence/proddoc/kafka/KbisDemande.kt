package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.model.metier.StockageFichier
import fr.convergence.proddoc.util.WSUtils
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ApplicationScoped
class KbisDemande() {

    companion object {
        private val LOG: Logger = getLogger(KbisDemande::class.java)
    }

    @Inject
    @field: Channel("stocker_fichier_demande")
    val retourEmitter: Emitter<MaskMessage>? = null

    /**
     * si un MaskMessage arrive sur le topic "KBIS_DEMANDE" (Incoming) :
     * fait le "passe-plat" et publie un message de demande de stockage de fichier
     * sur le topic "STOCKER_FICHIER_DEMANDE"
     **/

    @Incoming("kbis_demande")
    fun traiterEvenementDemandeKbis(messageIn: MaskMessage) {

        //@TODO ces requires sont à basculer dans le maskIOHadler
        requireNotNull(messageIn.entete.typeDemande) { "message.entete.typeDemande est null" }
        requireNotNull(messageIn.objetMetier) { "message.objectMetier est null" }

        var messageOut: MaskMessage = messageIn
        GlobalScope.launch {
            try {
                val demandeKbis = messageIn.recupererObjetMetier<KbisDemande>()
                val numeroDeGestion = demandeKbis.numeroGestion
                LOG.debug("Réception évènement demande Kbis n° : $numeroDeGestion")

                val avecApostille = demandeKbis.avecApostille
                val avecSceau = demandeKbis.avecSceau
                val avecSignature = demandeKbis.avecSignature
                val mapDeParametres = mapOf(
                    "numeroGestion" to numeroDeGestion, "apostille" to avecApostille.toString(),
                    "sceau" to avecSceau.toString(), "signature" to avecSignature.toString()
                )
                val uriCible = WSUtils.fabriqueURIServiceProdDoc(
                    "/kbis/kbisarecuperer", mapDeParametres)
                messageOut = MaskMessage.reponseOk(
                    StockageFichier(numeroDeGestion, uriCible.toASCIIString(), null, mapDeParametres, null), messageIn
                )

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