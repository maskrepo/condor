package fr.convergence.proddoc.controller

import fr.convergence.proddoc.services.rest.client.KbisService
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/kbis/xml/numgestion")
@Produces("application/xml")
@ApplicationScoped
class KbisAsXML {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(KbisAsXML::class.java)
    }

    @Inject
    @RestClient
    var kbisSrv: KbisService? = null

    @GET
    @Path("{numgestion}")
    fun numGestionKbis(@PathParam("numgestion") numgestion: String?): String? {

        var xmLbyNumGestion : String? = null

        runBlocking {
            launch {
                xmLbyNumGestion = kbisSrv?.getXMLbyNumGestion(numgestion)
            }
        }
        return (xmLbyNumGestion)
        //@TODO retourner une response http je crois ?
    }
}