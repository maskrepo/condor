package fr.convergence.proddoc.controller

import fr.convergence.proddoc.libs.model.KbisPDF
import fr.convergence.proddoc.services.rest.client.KbisService
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.annotations.jaxrs.PathParam
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/kbispdf/{nogestion}")
// @Produces("application/pdf")
@ApplicationScoped
class KbisPDF {

    @Inject
    @RestClient
    var kbisService: KbisService? = null

    @GET
    fun KbisAsPDF(@PathParam nogestion :String?):KbisPDF? {
        //return kbisService?.getPDFbyNumGestion(nogestion)
        return kbisService?.getPDFbyNumGestion()
    }
}