package fr.convergence.proddoc.controller

import fr.convergence.proddoc.libs.service.ProduitCache

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/kbistext")
@Produces(MediaType.TEXT_PLAIN)
@ApplicationScoped
class KbisText @Inject constructor(val cache: ProduitCache) {

    @GET
    @Path("/all")
    fun list(): List<fr.convergence.proddoc.libs.model.Produit> = cache.getAll().sortedByDescending { it.timestamp }

}