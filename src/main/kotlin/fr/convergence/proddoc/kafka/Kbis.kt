package fr.convergence.proddoc.kafka

import fr.convergence.proddoc.lib.service.KbisCache
import fr.convergence.proddoc.lib.util.CondorUtils.creeFichierTempBinaire
import fr.convergence.proddoc.lib.util.CondorUtils.creeURLKbisLocale
import fr.convergence.proddoc.libs.model.Produit
import fr.convergence.proddoc.libs.service.ProduitCache
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
public class Kbis(@Inject var cache :ProduitCache, @Inject val kbisSrv :KbisReactiveService) {

    companion object {
        private val LOG: Logger = getLogger(Produit::class.java)
    }

//    si quelque chose arrive sur le topic "kbis" (Incoming) :
//    1) récupère le kbis auprès de myGreffe
//    2) demande le stockage du fichier dans cache
//    3) publie un message de type "OK" + l'url du kbis récupérable (Outgoing)
    @Incoming("kbis")
    @Outgoing("kbis_fini")
    fun traitementEvenementtReceptionKbis(produit: Produit): String {

        // faire des assertions pour bien s'assurer que produit n'est pas null
        // puis enchaîner sur le code métier
        // require / check / assert

        try {
            requireNotNull(produit) { "Produit est null"}
            requireNotNull(produit.valeur) {  "Produit.valeur est null"}
            val numeroDeGestion = produit.valeur!!

            LOG.info("Réception demande Kbis : $numeroDeGestion")
            val kbisPDF =  kbisSrv.getPDFbyNumGestion(numeroDeGestion)
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