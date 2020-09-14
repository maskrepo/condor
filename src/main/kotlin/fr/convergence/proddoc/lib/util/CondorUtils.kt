package fr.convergence.proddoc.lib.util

import java.io.File
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
object CondorUtils {

//    crée un fichier temporaire à partir d'un tableau de bytes
//    retourne le fichier
fun creeFichierTempBinaire(fichier: ByteArray): File {
        val fichierTemp = createTempFile(suffix = ".pdf")
        fichierTemp.writeBytes(fichier)
        return(fichierTemp)
    }

//    crée l'URL de récupération du Kbis à partir d'un identifiant du cache
//    l'URL de base est un paramètre "applicatif"....
    fun creeURLKbisLocale(identifiant :String) :String {
        val baseURL =  "http://127.0.0.1:8080/"
        val pathURL = "kbis/pdfidx/$identifiant"

        return (baseURL+pathURL)
    }
}
