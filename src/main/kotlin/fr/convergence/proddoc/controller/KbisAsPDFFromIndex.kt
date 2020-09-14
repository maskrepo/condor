package fr.convergence.proddoc.controller

import fr.convergence.proddoc.lib.service.KbisCache
import java.io.File
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/kbis/pdfidx/")
@Produces("application/pdf")
@ApplicationScoped
class KbisAsPDFFromIndex {

//   si appel sur le path, retourne le kbis pdf en le récupérant dans le cache
    @GET
    @Path("{idx}")
    fun numGestionKbis(@PathParam("idx") identifiant: String): ByteArray? {

        requireNotNull(identifiant, {"L'identifiant reçu est null"})
        val myPDF: File?
        myPDF = KbisCache.recupFichierCache(identifiant)

        return (myPDF!!.readBytes())
    }
}