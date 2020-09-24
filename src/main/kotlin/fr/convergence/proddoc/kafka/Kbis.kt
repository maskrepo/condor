package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.model.lib.obj.MaskMessage
import fr.convergence.proddoc.model.metier.FichierEcrit
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import fr.convergence.proddoc.util.FichiersUtils
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
     * si un MaskMessage arrive sur le topic "kbis" (Incoming) :
     * 1) récupère le kbis auprès de myGreffe, selon les paramètres transmis dans le MaskMessage reçu
     * 2) demande le stockage du fichier dans cache
     * 3) c'est tout : c'est KbisReponse qui écoute si un Kbis est mis en cache pour répondre sur le
     * topic KBIS_REPONSE
     **/

    @Incoming("kbis")
    @Outgoing("fichier_info")
    fun traitementEvenementReceptionKbis(message: MaskMessage) : MaskMessage = maskIOHandler(message) {

        //@TODO ces requires sont à basculer dans le maskIOHadler
        requireNotNull(message.entete.typeDemande) { "message.entete.typeDemande est null" }
        requireNotNull(message.objetMetier) { "message.objectMetier est null" }

        val demandeKbis = message.recupererObjetMetier<KbisDemande>()
        val numeroDeGestion = demandeKbis.numeroGestion
        LOG.debug("Réception évènement demande Kbis n° : $numeroDeGestion")

        val kbisPDF = kbisSrv.getPDFbyNumGestion(
            numeroDeGestion, demandeKbis.avecApostille,
            demandeKbis.avecSceau, demandeKbis.avecSignature)

        val kbisEcrit = FichiersUtils.creeFichierTempBinaire(kbisPDF)
        FichierEcrit(kbisEcrit.absolutePath, kbisEcrit.name, numeroDeGestion)

    }
}