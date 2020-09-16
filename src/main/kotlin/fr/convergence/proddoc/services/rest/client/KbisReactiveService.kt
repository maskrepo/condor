package fr.convergence.proddoc.services.rest.client

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisReactiveService::class.java)
    }

    fun getPDFbyNumGestion(nogest: String, apostille :String = "NON", sceau :String = "NON",
                            signature :String ="NON") : ByteArray{

        LOG.debug("Kbis demandé : $nogest / apostille=$apostille / sceau=$sceau / signature=$signature")
        require(apostille == "OUI" || apostille == "NON"){"valeur apostille non valide"}
        require(sceau == "OUI" || sceau == "NON"){"valeur sceau non valide"}
        require(signature == "OUI" || signature == "NON"){"valeur signature non valide"}

        //Appel non-bloquant au WS myGreffe
        val client = WebClient.create(Vertx.vertx())
        //@TODO applicquer recommandation un client unique pour toute l'appli au lieu de l'instancier à chaque appel de la fonction
        val pdf = client
            .get(80, "172.31.5.20", "/convergence-greffe-web/rest/kbis/search?numGestion=$nogest")
            .rxSend()
            .map { it.bodyAsBuffer().bytes }
            .blockingGet()  // je ne trouve pas comment *ne pas* utiliser blockingGet qui semblerait bloquer le thread complet
        // il y a subscribe mais je n'arrive pas à l'utiliser
        LOG.info("getPDFbyNumGestion - sortie")
        return(pdf)
    }
}
