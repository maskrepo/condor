package fr.convergence.proddoc.libs.model

data class DemandeKbis (var unique_id: String = "0000", var numgestion: String?, var init: init?, var parametres: parametres?)

data class init (var code_utilisateur: String?, var adresse_greffe: String?, var date_fraicheur: String?,
                 var date_edition: String?)

data class parametres (var mode_transmission: String, var apostille: String?, var signature: String?,
                       var qr_code: String? = null, var recto_verso: String?)