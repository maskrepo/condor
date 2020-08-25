package fr.convergence.proddoc.services.rest.client

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/search")
@RegisterRestClient
interface KbisService {

    @GET
    @Produces(MediaType.WILDCARD)
    fun getPDFbyNumGestion(@QueryParam("numGestion") nogest :String?) :ByteArray

    @GET
    @Produces(MediaType.WILDCARD)
    fun getXMLbyNumGestion(@QueryParam("numGestion") nogest :String?, @QueryParam("format") format :String = "xml") : String
}