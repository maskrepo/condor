# Condor (service Kbis)

## Rôle du service
Fournir un Kbis :
1) Traiter une demande de Kbis
2) Récupérer Kbis auprès du WS myGreffe
3) Demander le stockage du Kbis dans le cache applicatif
4) Réponde avec URL du Kbis demandé initialement

## Topics utilisés
KBIS_DEMANDE<br>
STOCKER_FICHIER_DEMANDE<br>
STOCKER_FICHIER_REPONSE<br>
KBIS_REPONSE<br>

## Diagramme de séquence

## Exemple de message reçu
``
{
  "entete": {
    "idUnique": "659039e688c23ff08b4f905be07294ab66d600d4",
    "idLot": "12345",
    "dateHeureDemande": "2020-08-25T09:08:07",
    "idEmetteur": "L20057",
    "idGreffe": "0101",
    "typeDemande": "KBIS"
  },
  "objetMetier": {
    "numeroGestion": "2012B00021",
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