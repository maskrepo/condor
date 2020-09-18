package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.lib.service.KbisCache
import fr.convergence.proddoc.lib.util.CondorUtils.creeFichierTempBinaire
import fr.convergence.proddoc.lib.util.CondorUtils.creeURLKbisLocale
import fr.convergence.proddoc.model.lib.MaskMessage
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.model.metier.KbisRetour
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
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
     * 3) publie un message de type MaskMessage également "OK" + l'url du kbis récupérable (Outgoing)
     *  ou bien "KO" + exception
     **/

    @Incoming("kbis")
    @Outgoing("kbis_fini")
    fun traitementEvenementtReceptionKbis(message: MaskMessage): MaskMessage {

        try {

            requireNotNull(message.entete.typeDemande) { "message.entete.typeDemande est null" }
            requireNotNull(message.objetMetier) { "message.objectMetier est null" }
            val demandeKbis = message.recupererObjetMetier<KbisDemande>()
            val numeroDeGestion = demandeKbis.numeroGestion
            LOG.debug("Réception demande Kbis : $numeroDeGestion")

            val kbisPDF = kbisSrv.getPDFbyNumGestion(numeroDeGestion, demandeKbis.avecApostille,
                                                    demandeKbis.avecSceau, demandeKbis.avecSignature)

            KbisCache.deposeFichierCache(creeFichierTempBinaire(kbisPDF), numeroDeGestion)
            val urlKbis = creeURLKbisLocale(numeroDeGestion)

            return MaskMessage.of(
                KbisRetour("OK", urlKbis),
                message.entete.idEmetteur,
                message.entete.idGreffe,
                message.entete.typeDemande
            )
        }
        catch (e :Exception) {
            LOG.error("Problème sur le traitement de l'évènement de réception du Kbis", e)
            return MaskMessage.of(
                KbisRetour("KO", e.toString()),
                message.entete.idEmetteur,
                message.entete.idGreffe,
                message.entete.typeDemande)
        }
    }
}