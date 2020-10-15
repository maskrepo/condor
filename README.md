# Condor (service Kbis)
Lancement : ./gradlew quarkusdev <br>
(port 8090)

## Rôle du service
Fournir un Kbis :
1) Traiter une demande de Kbis (en provenance de Rhino)
2) Faire demande de stockage (auprès de Stinger)
3) Sur la demande de Stinger, récupérer le Kbis auprès du WS myGreffe
4) Retourner le fichier à Stinger
5) Traiter la réponse de Stinger 
5) Répondre (à Rhino) avec URL du Kbis demandé initialement

## Topics utilisés
KBIS_DEMANDE<br>
STOCKER_FICHIER_DEMANDE<br>
STOCKER_FICHIER_REPONSE<br>
KBIS_REPONSE<br>

## Diagramme de séquence
Sous Confluence : https://zedreamteam.atlassian.net/wiki/spaces/MASK/pages/164167681/Service%2BKbis%2Bcondor

## Exemple de message avec numéro de gestion
``
{
  "entete": {
    "idUnique": "659039e688c23ff08b4f905be07294ab66d600d4",
    "idLot": "12345",
    "dateHeureDemande": "2020-08-25T09:08:07",
    "idEmetteur": "L20057",
    "idReference": "zerealref123",
    "idGreffe": "0101",
    "typeDemande": "KBIS"
  },
  "objetMetier": {
    "numeroGestion": "2012B00051",
    "identifiantRegistre" : null,
    "avecApostille": false,
    "avecSceau": false,
    "avecSignature": true
  },
 "reponse": {
   "estReponseOk": false,
   "messageErreur": "noerror",
   "stackTrace": "nostracktrace"
 }
}
``
## Exemple de message avec identifiant registre
``
{
  "entete": {
    "idUnique": "659039e688c23ff08b4f905be07294ab66d600d4",
    "idLot": "12345",
    "dateHeureDemande": "2020-08-25T09:08:07",
    "idEmetteur": "L20057",
    "idReference": "zerealref123",
    "idGreffe": "0101",
    "typeDemande": "KBIS"
  },
  "objetMetier": {
    "numeroGestion": null,
    "identifiantRegistre" : 12345678910,
    "avecApostille": false,
    "avecSceau": false,
    "avecSignature": true
  },
 "reponse": {
   "estReponseOk": false,
   "messageErreur": "noerror",
   "stackTrace": "nostracktrace"
 }
}
``
