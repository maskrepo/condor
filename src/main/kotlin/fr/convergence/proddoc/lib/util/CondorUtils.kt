package fr.convergence.proddoc.lib.util

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
object CondorUtils {

    private val LOG: Logger = LoggerFactory.getLogger(CondorUtils::class.java)

    /**
     *    crée un fichier temporaire à partir d'un tableau de bytes
     *    et retourne un objet fichier
     */
    fun creeFichierTempBinaire(fichier: ByteArrayInputStream): File {
        try {
            LOG.debug("Début création du fichier binaire temporaire")
            val fichierTemp = createTempFile(suffix = ".pdf")
            fichierTemp.writeBytes(fichier.readBytes())
            LOG.debug("Fin création du fichier binaire temporaire")
            return (fichierTemp)
        } catch (e: java.lang.Exception) {
            if (e is IOException) {
                throw (IllegalStateException("Problème d'écriture sur disque", e))
            } else {
                throw e
            }
        }
    }

    /**
     *  crée et retourne l'URL de récupération du Kbis à partir d'un identifiant du cache
     *  l'URL de base est un paramètre "applicatif"...
     */
    fun creeURLKbisLocale(numGestion: String): String {

        val baseURL = "http://127.0.0.1:8080/"
        val pathURL = "kbis/pdfnumgestion/$numGestion"

        return (baseURL + pathURL)
    }
}
