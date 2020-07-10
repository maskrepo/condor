package fr.convergence.proddoc.services.rest

data class Kbis (val datasetId :String, val recordId :String, val fields :KbisfieldObject,
                val record_timestamp :String)

data class KbisfieldObject (val categorie_juridique_insee :Int, val libelle :String)