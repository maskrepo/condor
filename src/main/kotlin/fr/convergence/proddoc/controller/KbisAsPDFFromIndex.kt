package fr.convergence.proddoc.controller

import fr.convergence.proddoc.lib.service.KbisCache
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import java.io.File
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/kbis/pdfidx/")
@Produces("application/pdf")
@ApplicationScoped
class KbisAsPDFFromIndex {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsPDFFromIndex::class.java)
    }

    @GET
    @Path("{idx}")
    fun numGestionKbis(@PathParam("idx") idx: String?): ByteArray? {
        var myPDF: File? = null
        myPDF = KbisCache.getFileFromIndex(idx!!)
        return (myPDF!!.readBytes())
    }
}