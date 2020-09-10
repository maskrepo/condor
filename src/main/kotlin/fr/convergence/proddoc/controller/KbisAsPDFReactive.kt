package fr.convergence.proddoc.controller

import fr.convergence.proddoc.lib.service.KbisCache
import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class KbisAsPDFReactive (@Inject var KbisMap: KbisCache) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsPDFReactive::class.java)
    }

    var kbisSrv = KbisReactiveService()

    @Route(path = "/pdfnumgestionreactive", methods = [HttpMethod.GET], produces = arrayOf("application/pdf"))
    fun numGestionKbis(ex :RoutingExchange)  {

        val numgestion :String = ex.getParam("numgestion").orElse("empty")
        LOG.info("Recupération paramètre numgestion : $numgestion")
            if (numgestion != "empty") {
                var pdfbyNumGestion : ByteArray? = null
                runBlocking {
                    launch {
                        LOG.info("Appel de getPDFbyNumGestion")
                        pdfbyNumGestion = kbisSrv.getPDFbyNumGestion(numgestion)
                    }
                }
                putPDFintoFS(pdfbyNumGestion, numgestion)
                ex.response().putHeader(HttpHeaders.CONTENT_LENGTH, (pdfbyNumGestion?.size).toString())
//              ex.response().setChunked(true) PAS UTILE
                ex.response().write(io.vertx.core.buffer.Buffer.buffer(pdfbyNumGestion))
                ex.ok()
                ex.response().end() // si on enlève cette ligne l'appel suivant n'est pas pris en compte par quarkus (?!)
                // vérifier le end() en confjonction avec le keep-alive ?
            } else ex.ok("Numero de gestion invalide")
    }

    fun putPDFintoFS(pdfFile: ByteArray?, noGestion :String) {

        val fichierTemp = createTempFile(suffix = ".pdf")
        if (pdfFile != null) {
            fichierTemp.writeBytes(pdfFile)
        }
        LOG.info("Fichier créé : ${fichierTemp.absoluteFile}")
        if (KbisMap.putFileInCache(fichierTemp, noGestion)) LOG.info("Fichier mis en cache avec succès")
        else LOG.error("Echec de la mise en cache du fichier")
    }



}

