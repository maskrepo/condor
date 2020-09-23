package fr.convergence.proddoc.lib.service

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import java.io.File
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
object FichierCache {

    private val LOG: Logger = LoggerFactory.getLogger(FichierCache::class.java)

    private var FichierMap: MutableMap<String, File> = mutableMapOf()

//    met le fichier transmis reçu dans une map
//    ne retourne rien ; si ça pète, ça lève une exception à gérer par l'appelant
    fun deposeFichierCache(fi: File, identifiant :String) {
        FichierMap.put(identifiant, fi)
        LOG.debug("fichier $identifiant mis en cache, taille de la map en sortie: ${FichierMap.size}")
    }

    /**
     *  récupère un fichier dans le cache à partir de son identifiant
     *  retourne le fichier ou null si rien trouvé
     */
    fun recupFichierCache(identifant: String): File? {
        if (FichierMap.isNotEmpty()) {
            return FichierMap.get(identifant)
        } else {
            return (null)
        }
    }
}
