package fr.convergence.proddoc.reactive

import fr.convergence.proddoc.lib.service.KbisCache
import fr.convergence.proddoc.libs.model.Produit
import fr.convergence.proddoc.libs.service.ProduitCache
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory.getLogger
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
public class KbisR(@Inject var cache: ProduitCache) {

    companion object {
        private val LOG: Logger = getLogger(Produit::class.java)
    }

    val kbisSrv= KbisReactiveService()

    @Incoming("kbisR")
    @Outgoing("kbisR_fini")
    fun ecouteKbis(produit: Produit): String {
        LOG.info("Réception demande KbisR : ${produit.valeur}")

        try {
            val kbisPDF = produit.valeur?.let { kbisSrv.getPDFbyNumGestion(it) }
            LOG.info("Taille du Kbis : ${kbisPDF?.size}")
            if (kbisPDF != null) {
                KbisCache.putPDFintoFS(kbisPDF, produit.valeur!!)
                return ("http://127.0.0.1:8080/kbis/pdfidx/${produit.valeur}")
            } else {
                LOG.error("Taille du Kbis = 0")
                return "Kbis_KO"
            }
        }
        catch (e :Exception){
            LOG.error("Problème sur la récupération du Kbis")
            LOG.error(e.message)
            return "Kbis_KO"
        }
        finally {
            //rien de spécial on a déjà retourné soit la taille du Kbis soit 0
        }
    }
}