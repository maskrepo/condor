package fr.convergence.proddoc.lib.service

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import java.io.File
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
object KbisCache {

    private val LOG: Logger = LoggerFactory.getLogger(KbisCache::class.java)

    private var KbisMap: MutableMap<String, File> = mutableMapOf()

//    met le fichier transmis reçu dans une map
//    ne retourne rien ; si ça pète, ça lève une exception à gérer par l'appelant
    fun deposeFichierCache(fi: File, identifiant :String) {
        KbisMap.put(identifiant, fi)
        LOG.info("putFileInCache - taille de la map en sortie: ${KbisMap.size}")
    }

//    récupère un fichier dans le cache à partir de son identifiant
//    retourne le fichier ou null si rien trouvé
    fun recupFichierCache(identifant: String): File? {
        if (KbisMap.isNotEmpty()) {
            return KbisMap.get(identifant)
        } else {
            return (null)
        }
    }
}
