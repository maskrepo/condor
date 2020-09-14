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
    fun trtEvtReceptionKbis(produit: Produit): String {
        LOG.info("Réception demande Kbis : ${produit.valeur}")

        try {
            val kbisPDF = produit.valeur?.let { kbisSrv.getPDFbyNumGestion(it) }
            LOG.debug("Taille du Kbis : ${kbisPDF?.size}")
            if (kbisPDF != null) {
                KbisCache.deposeFichierCache(creeFichierTempBinaire(kbisPDF), produit.valeur!!)
                return (creeURLKbisLocale(produit.valeur!!))
            } else {
                LOG.error("Taille du Kbis = 0")
                return "Kbis_KO"
            }
        }
        catch (e :Exception){
            LOG.error("Problème sur le traitement de l'évènement de réception du Kbis", e)
            return "Kbis_KO"
        }
    }
}