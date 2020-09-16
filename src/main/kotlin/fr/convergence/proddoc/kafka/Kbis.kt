package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.lib.service.KbisCache
import fr.convergence.proddoc.lib.util.CondorUtils.creeFichierTempBinaire
import fr.convergence.proddoc.lib.util.CondorUtils.creeURLKbisLocale
import fr.convergence.proddoc.model.lib.MaskMessage
import fr.convergence.proddoc.model.metier.KbisDemande
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
public class Kbis(@Inject val kbisSrv :KbisReactiveService) {

    companion object {
        private val LOG: Logger = getLogger(Kbis::class.java)
    }

//    si un MaskMessage arrive sur le topic "kbis" (Incoming) :
//    1) récupère le kbis auprès de myGreffe
//    2) demande le stockage du fichier dans cache
//    3) publie un message de type "OK" + l'url du kbis récupérable (Outgoing)
    @Incoming("kbis")
    @Outgoing("kbis_fini")
    fun traitementEvenementtReceptionKbis(message: MaskMessage): String {

        // faire des assertions pour bien s'assurer que produit n'est pas null
        // puis enchaîner sur le code métier
        // require / check / assert

        try {
            requireNotNull(message) { "message est null"}
            requireNotNull(message.entete.typeDemande) {  "message.entete.typeDemande est null"}
            requireNotNull(message.objetMetier) {"message.objectMetier est null"}

            val demandeKbis = message.recupererObjetMetier<KbisDemande>()
            val numeroDeGestion = demandeKbis.numeroGestion
            requireNotNull(numeroDeGestion) {"numéro de gestion null : demande de kbis invalide"}

            val apostille = demandeKbis.apostille
            val sceau = demandeKbis.sceau
            val signature = demandeKbis.signature

            LOG.info("Réception demande Kbis : $numeroDeGestion")
            val kbisPDF =  kbisSrv.getPDFbyNumGestion(numeroDeGestion, apostille, sceau, signature)
            LOG.debug("Taille du Kbis : ${kbisPDF.size}")
            KbisCache.deposeFichierCache(creeFichierTempBinaire(kbisPDF), numeroDeGestion)

            return (creeURLKbisLocale(numeroDeGestion))
        }
        catch (e :Exception){
            LOG.error("Problème sur le traitement de l'évènement de réception du Kbis", e)
            return "Kbis_KO"
        }
    }
}