package fr.convergence.proddoc.controller

import fr.convergence.proddoc.libs.model.Kbis
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.annotations.jaxrs.PathParam
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces


// @Path("/kbis")
@RegisterRestClient
interface KbisService {
    @GET
    @Path("/explore/dataset/forme-juridique/api/")
    @Produces("application/json")
    fun getFJ(@PathParam q: String?) : Set<Kbis>
}