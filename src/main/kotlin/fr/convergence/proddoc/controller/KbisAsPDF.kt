package fr.convergence.proddoc.controller

import fr.convergence.proddoc.services.rest.client.KbisService
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/kbis/pdf/numgestion")
@Produces("application/pdf")
@ApplicationScoped
class KbisAsPDF {

    @Inject
    @RestClient
    var kbisSrv: KbisService? = null

    @GET
    @Path("{numgestion}")
    fun numGestionKbis(@PathParam("numgestion") numgestion :String?) : ByteArray? {
        println("momo- $numgestion")
        return kbisSrv?.getPDFbyNumGestion(numgestion)
    }
}