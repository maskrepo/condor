package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.StockageFichier
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import fr.convergence.proddoc.util.WSUtils
import fr.convergence.proddoc.util.maskIOHandler
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class Kbis(@Inject val kbisSrv: KbisReactiveService) {

    companion object {
        private val LOG: Logger = getLogger(Kbis::class.java)
    }

    /**
     * si un MaskMessage arrive sur le topic "KBIS_DEMANDE" (Incoming) :
     * fait le "passe-plat" et publie un message de demande de stockage de fichier
     * sur le topic "STOCKER_FICHIER_DEMANDE"
     **/

    @Incoming("kbis_demande")
    @Outgoing("stocker_fichier_demande")
    fun traitementEvenementReceptionKbis(message: MaskMessage) : MaskMessage = maskIOHandler(message) {

        //@TODO ces requires sont à basculer dans le maskIOHadler
        requireNotNull(message.entete.typeDemande) { "message.entete.typeDemande est null" }
        requireNotNull(message.objetMetier) { "message.objectMetier est null" }

        val demandeKbis = message.recupererObjetMetier<KbisDemande>()
        val numeroDeGestion = demandeKbis.numeroGestion
        LOG.debug("Réception évènement demande Kbis n° : $numeroDeGestion")

        val avecApostille = demandeKbis.avecApostille
        val avecSceau = demandeKbis.avecSceau
        val avecSignature = demandeKbis.avecSignature
        val mapDeParametres =  mapOf(
            "numeroGestion" to numeroDeGestion, "apostille" to avecApostille.toString(),
            "sceau" to avecSceau.toString(), "signature" to avecSignature.toString()
        )
        val uriCible = WSUtils.fabriqueURIServiceProdDoc(
            "/kbis/kbisarecuperer", mapDeParametres)

        StockageFichier(numeroDeGestion,uriCible.toASCIIString(),null, mapDeParametres, null)

    }
}