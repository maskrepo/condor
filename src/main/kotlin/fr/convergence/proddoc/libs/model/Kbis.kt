// classe à sortir dans librairie "MaskModel"?
package fr.convergence.proddoc.libs.model

data class EnveloppeKbis(val nhits :String? = null, val parameters :IFGParametres? = null, val records :List<Kbis>? = null)

data class IFGParametres(val dataset :String? = null, val timezone :String? = null, val rows :Int? = null, val format :String? = null)

data class Kbis (val datasetid :String? = null, val recordid :String? = null, val fields : KbisfieldObject? = null,
                 val record_timestamp :String? = null)

data class KbisfieldObject (val categorie_juridique_insee :Int? = null, val libelle :String? = null)
