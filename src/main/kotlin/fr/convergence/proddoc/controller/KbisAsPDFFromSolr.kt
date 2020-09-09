package fr.convergence.proddoc.controller

import fr.convergence.proddoc.services.rest.client.KbisService
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/kbis/pdfsolr/numgestion")
@Produces("application/pdf")
@ApplicationScoped
class KbisAsPDFFromSolr {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsPDFFromSolr::class.java)
    }

    @Inject
    @RestClient
    var kbisSrv: KbisService? = null

    @GET
    @Path("{numgestion}")
    fun numGestionKbis(@PathParam("numgestion") numgestion: String?): ByteArray? {
        var myPDF: ByteArray? = null
        try {
//            val urlString = "http://localhost:8983/solr/kbispdf"
            val urlString = "http://172.31.4.237:8983/solr/kbispdf"
            val solr: HttpSolrClient = HttpSolrClient.Builder(urlString).build()
            val query = SolrQuery()
            query.set("q", "kbisid:0101$numgestion")
            LOG.info("solr query : ${query.query}")
            val response = solr.query(query)
            val docList = response.results
            if (docList.numFound > 0) {
                for (doc in docList) {
                    LOG.info("récupération dans solr du kbis PDF 0101$numgestion")
                    myPDF = doc.get("kbispdf") as ByteArray
                }
            } else {
                throw NoSuchElementException("document $numgestion non trouvé dans solr")
            }
        }
        catch (e :Exception){
            LOG.error(e.message)
            return ByteArray(0) // c'est un peu degoulache mais on renvoie un pdf bidon
        }
        finally {
        }
        return (myPDF)

    }
}