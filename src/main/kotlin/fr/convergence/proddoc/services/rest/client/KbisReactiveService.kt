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
import java.io.IOException
import java.lang.IllegalArgumentException


class KbisReactiveService {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsXMLReactive::class.java)
    }

    private val urlSolrCore = "http://172.31.4.237:8983/solr/kbispdf" //@TODO faut-il que cette variable soit sortie quelquepart d'autre dans le projet ?
//    private val urlSolrCore = "http://localhost:8983/solr/kbispdf"
    private val gr_nume = "0101"

    fun getPDFbyNumGestion(nogest: String) : ByteArray{

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
        val documentID="$gr_nume$nogest"
        if (putPDFintoSolr(documentID, pdf) ) return(pdf)
        else throw IOException("Impossible d'insérer document $documentID dans solr")
        //@TODO rappel kafka pour indiquer que kbis récupéré et déposé dans solr OK
    }

    private fun putPDFintoSolr(docID: String, pdfFile: ByteArray): Boolean {

        try {


            val urlString = urlSolrCore
            val solr: HttpSolrClient = HttpSolrClient.Builder(urlString).build()

            LOG.info("recherche si le document existe déjà dans le core")

            val query = SolrQuery()
            query.set("q", "kbisid:$docID")
            val response = solr.query(query)
            val docList = response.results
            if (docList.numFound >= 1) {
                LOG.error("Insertion dans solr impossible : le document kbisid:$docID existe déjà")
                throw IllegalArgumentException("Insertion dans solr impossible : le document kbisid:$docID existe déjà\"")
            } else {
                LOG.info("insertion dans solr du kbis PDF $docID")
                solr.setParser(XMLResponseParser())
                val document = SolrInputDocument()
                document.addField("kbisid", "$docID")
                document.addField("kbispdf", pdfFile)
                document.addField("version", "1")
                document.addField("numerolot", "1")
                solr.add(document)
                solr.commit()
                LOG.info("insertion dans solr terminée")
            }
        }
        catch (e: Exception){
            // en cas de KO solr on catch et on retourne un KO
            LOG.error(e.message)
            return false
        }
        finally {
            // je ne sais pas quoi faire de plus :-)
        }
        return true
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
