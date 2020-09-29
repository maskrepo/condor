package fr.convergence.proddoc.controller

import fr.convergence.proddoc.services.rest.client.KbisReactiveService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/kbis/kbisarecuperer/")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@ApplicationScoped
class KbisARecuperer(@Inject val kbisSrv: KbisReactiveService) {

//   si appel sur le path, retourne le kbis pdf en le récupérant dans le cache
    @GET
    fun recupererKbis(@QueryParam("numeroGestion") numgestion: String,
                        @QueryParam("apostille") avecApostille : Boolean = false,
                        @QueryParam("sceau") avecSceau :Boolean =false,
                        @QueryParam("signature") avecSignature :Boolean = false)
        :Response {

        requireNotNull(numgestion, {"L'identifiant reçu est null"})
        val kbisPDF = kbisSrv.getPDFbyNumGestion(numgestion, avecApostille, avecSceau, avecSignature)

        if (kbisPDF==null) { // A REGARDER AVEC RENAUD
            return Response
                .status(Response.Status.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .entity("fichier non trouvé")
                .build()
        }
        else {
            return Response
                .status(Response.Status.OK)
                .entity(kbisPDF.readBytes())
                .build()
        }

    }
}