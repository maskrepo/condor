package fr.convergence.proddoc.lib.service

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import java.io.File
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
object KbisCache {

    private val LOG: Logger = LoggerFactory.getLogger(KbisCache::class.java)

    private var KbisMap: MutableMap<String, File> = mutableMapOf()

    fun putFileInCache(fi: File, identifiant :String): Boolean {
        KbisMap.put(identifiant, fi)
        LOG.info("putFileInCache - taille de la map en sortie: ${KbisMap.size}")
        return true
    }

    fun getFileFromIndex(i: String): File? {
        if (KbisMap.isNotEmpty()) return KbisMap.get(i)
        else return (null)
    }

    fun putPDFintoFS(pdfFile: ByteArray?, noGestion :String) {

        val fichierTemp = createTempFile(suffix = ".pdf")
        if (pdfFile != null) {
            fichierTemp.writeBytes(pdfFile)
        }
        LOG.info("Fichier créé : ${fichierTemp.absoluteFile}")
        if (putFileInCache(fichierTemp, noGestion)) LOG.info("Fichier mis en cache avec succès")
        else LOG.error("Echec de la mise en cache du fichier")
    }

}
