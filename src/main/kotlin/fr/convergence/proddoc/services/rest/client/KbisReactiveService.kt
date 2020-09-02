package fr.convergence.proddoc.services.rest.client

import fr.convergence.proddoc.controller.KbisAsXMLReactive
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.impl.XMLResponseParser
import org.apache.solr.common.SolrInputDocument


class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsXMLReactive::class.java)
    }

    fun getPDFbyNumGestion(nogest: String) : ByteArray{
        LOG.info("entree dans KbisReactiveService.getPDFbyNumGestion")
        //Appel non-bloquant au WS myGreffe
        val client = WebClient.create(Vertx.vertx())
        //@TODO applicquer recommandation un client unique pour toute l'appli au lieu de l'instancier à chaque appel de la fonction
        val pdf = client
            .get(80, "172.31.5.20", "/convergence-greffe-web/rest/kbis/search?numGestion=$nogest")
            .rxSend()
            .map { it.bodyAsBuffer().bytes }
            .blockingGet()  // je ne trouve pas comment *ne pas* utiliser blockingGet qui semblerait bloquer le thread complet
        // il y a subscribe mais je n'arrive pas à l'utilise

        // insertion du kbis PDF dans solr
        // création d'un client solr http --> @TODO à mettre en commun quelque part
        val urlString = "http://localhost:8983/solr/kbispdf"
        val solr: HttpSolrClient = HttpSolrClient.Builder(urlString).build()

        LOG.info("recherche si kbis pdf existe déjà pour ce dossier")
        try {
            val query = SolrQuery()
            query.set("q", "kbisid:0101$nogest")
            val response = solr.query(query)

            val docList = response.results
            when (docList.numFound.toInt()) {
                1 -> {
                    // si un seul document est bien trouvé on le remplace
                    for (doc in docList) {
                        LOG.info("insertion dans solr du kbis PDF 0101$nogest")
                        solr.setParser(XMLResponseParser())
                        val document = SolrInputDocument()
                        document.addField("kbisid", "0101$nogest")
                        document.addField("kbispdf", pdf)
                        document.addField("version", "1")
                        document.addField("numerolot", "1")
                        solr.add(document)
                        solr.commit()
                        LOG.info("insertion dans solr terminée")
                    }

                }
                0 -> {
                    LOG.info("insertion dans solr du kbis PDF 0101$nogest")
                    solr.setParser(XMLResponseParser())
                    val document = SolrInputDocument()
                    document.addField("kbisid", "0101$nogest")
                    document.addField("kbispdf", pdf)
                    document.addField("version", "1")
                    document.addField("numerolot", "1")
                    solr.add(document)
                    solr.commit()
                    LOG.info("insertion dans solr terminée")
                }
                else -> {
                    // sinon on lève une erreur
                    throw Exception("kbis 0101$nogest impossible à ajouter dan solr")

                }
            }
        }
        catch (e :Exception){
            LOG.error(e.message)
        }
        finally {
            LOG.info("getPDFbyNumGestion : sortie")
            return (pdf)
        }

        //@TODO rappel kafka pour indiquer que kbis récupéré et déposé dans solr OK
    }

    fun getXMLbyNumGestion(nogest: String): String {

        LOG.info("entree dans KbisReactiveService.getXMLbyNumGestion")

        //Appel non-bloquant au WS myGreffe
        val client = WebClient.create(Vertx.vertx())
        //@TODO applicquer recommandation un client unique pour toute l'appli au lieu de l'instancier à chaque appel de la fonction
        var xml = client
                .get(80, "172.31.5.20", "/convergence-greffe-web/rest/kbis/search?numGestion=$nogest&format=xml")
                .rxSend()
                .map { it.bodyAsString() }
                .blockingGet()  // je ne trouve pas comment *ne pas* utiliser blockingGet qui semblerait bloquer le thread complet
                                // il y a subscribe mais je n'arrive pas à l'utilise


        // insertion du kbis XML dans solr
        // création d'un client solr http --> @TODO à mettre en commun quelque part
        val urlString = "http://localhost:8983/solr/kbisxml"
        val solr: HttpSolrClient = HttpSolrClient.Builder(urlString).build()

        LOG.info("recherche si kbis xml existe déjà pour ce dossier")
        try {
            val query = SolrQuery()
            query.set("q", "kbisid:0101$nogest")
            val response = solr.query(query)

            val docList = response.results
            when (docList.numFound.toInt()) {
                0 -> {
                    // si un seul document est bien trouvé on le remplace
                    for (doc in docList) {
                        LOG.info("insertion dans solr du kbisxml 0101$nogest")
                        solr.setParser(XMLResponseParser())
                        val document = SolrInputDocument()
                        document.addField("kbisid", "0101$nogest")
                        document.addField("kbiscontent", xml)
                        solr.add(document)
                        solr.commit()
                        LOG.info("insertion dans solr terminée")
                    }

                }
                1 -> {
                    // sinon on lève une erreur
                    throw Exception("kbis 0101$nogest existe déjà")
                    LOG.error("Un kbisXML avec ce numéro de gestion $nogest existe déjà dans solr")
                }
            }
        }
        catch (e :Exception){
            LOG.error(e.message)
        }
        finally {
            LOG.info("getXMLbyNumGestion : sortie")
            return (xml)
        }

        //@TODO rappel kafka pour indiquer que kbis récupéré et déposé dans solr OK


    }

}
