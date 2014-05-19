Documentation : web-service
======

Le service est disponible ici : http://appmeetup.appspot.com/

##/add-user
Ajoute un nouvel utilisateur dans la BD
**Paramètre**
* username (nom d'utilisateur)
* password (mot de passe de l'utilisateur)
* nom      (nom de l'utilisateur)
* prenom   (prénom de l'utilisateur)

**Retourne**
* key ou un message qui indique que l'utilisateur existe déjà

**Exemple**
http://appmeetup.appspot.com/add-user?username=testest&password=motDePasse&nom=userTest&prenom=prenomTest

**Note**
Si un champs n'est pas indiquer il sera ajouter dans le web service comme étant vide

##/ask-friend
Fait une demande à un utilisateur
**Paramètre**
* moi      (username de la personne qui demande en amitier un autre utilisateur)
* password (mot de passe de la personne qui demande en amitié un autre utilisateur)
* demande  (username de la personne demander en amitier)
**Retourne**
* message

**Exemple**
http://appmeetup.appspot.com/ask-friend?moi=testesdfst&password=motDePasse&demande=marcantoine.bouchardm@gmail.com

##/add-friend
Ajoute un utilisateur comme ami (il faut que l'utilisateur soit préalablement dans les demandes d'amitié)
**Paramètre**
* moi      (username qui ajoute un utilisateur)
* password (mot de passe de cet utilisateur)
* ajoute   (username de la personne à ajouter)

**Retourne**
* message

**Exemple**
http://appmeetup.appspot.com/add-friend?moi=marcantoine.bouchardm@gmail.com&password=b09397552e74f0888a9f368dc04f37ddd3565238&ajoute=testesdfst

##/list-meetUp
Liste toute les MeetUp d'un utilisateur
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)
* withInfo (facultatif : permet de retourner plus d'info à propos des utilisateurs qui participe au meetUp)

**Retourne**
* listMeetUp

**Exemple**
http://appmeetup.appspot.com/list-meetUp?moi=marcantoine.bouchardm@gmail.com&password=b09397552e74f0888a9f368dc04f37ddd3565238&withInfo=1

##/add-meetUp
Anjoute un nouveau MeetUp
**Paramètre**
* moi      (username de la personne qui rajoute un meetUp)
* password (mot de passe de cet personne)
* nom      (nom du meetUp)
* lieu     (lieu du meetUp)
* duree    (int : duree du meetUp)
* heureMin (int : heure minimum du meetUp)
* heureMax (int : heure maximum du meetUp)
* dateMin  (yyyy-mm-dd : date minimum du meetUp)
* dateMax  (yyyy-mm-dd : date maximum du meetUp)

**Retourne**
* key

**Exemple**
http://appmeetup.appspot.com/add-meetUp?moi=testesdfst&password=motDePasse&nom=rencontre&lieu=ici&duree=60&dateMin=2014-05-18&dateMax=2014-05-23&heureMin=10&heureMax=20

##/invite-friend
Invite un utilisateur à être ami
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)
* ami      (username de l'ami à ajouter)
* meetUp (key : key du meetUp)

**Retourne**
* message

**Exemple**
http://appmeetup.appspot.com/invite-friend?moi=testesdfst&password=motDePasse&ami=marcantoine.bouchardm@gmail.com&meetUp=agtzfmFwcG1lZXR1cHIrCxILVXRpbGlzYXRldXIYgICAgJ3ujwoMCxIGTWVldFVwGICAgICAgIAKDA

**Note**
L'utilisateur qui invite un ami doit être le créateur du meetUp.

##/accept-meetUp
Accepte un demande de participation à un meetUp
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)
* meetUp (key : key du meetUp à accepter)

**Retourne**
* message

**Exemple**
http://appmeetup.appspot.com/accept-meetUp?moi=marcantoine.bouchardm@gmail.com&password=b09397552e74f0888a9f368dc04f37ddd3565238&meetUp=agtzfmFwcG1lZXR1cHIrCxILVXRpbGlzYXRldXIYgICAgJ3ujwoMCxIGTWVldFVwGICAgICAgIAKDA

**Note**
L'utilisateur qui accepte un meetUp doit être inviter à ce meetUp

##/delete-meetUp
Supprime un MeetUp (fonctionnel uniquement pour le créateur du MeetUp)
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)
* supprime (key: key du meetUp à supprimer)

**Retourne**
* message

**Exemple** 
http://appmeetup.appspot.com/delete-meetUp?moi=testesdfst&password=motDePasse&supprime=agtzfmFwcG1lZXR1cHIrCxILVXRpbGlzYXRldXIYgICAgJ3ujwoMCxIGTWVldFVwGICAgICAgIAKDA

**Note**
L'utilisateur qui supprime un meetUp doit être inviter à ce meetUp

##/delete-user-meetUp
Pour supprimer sa participation à un meetUp
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)
* meetUp  (key:key du meetUp)

**Retourne**
* message

##/add-notif
Ajoute une notification à un utilisateur
**Paramètre**
* username (username de l'utilisateur)
* notif    (notification à envoyer)

**Retourne**
*message

##/read-notif
Lit les notifications d'un utilisateur (Les notifications sont supprimer après)
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)

**Retourne**
* notif (liste des notifications)

**Exemple**
http://appmeetup.appspot.com/read-notif?moi=marcantoine.bouchardm@gmail.com&password=b09397552e74f0888a9f368dc04f37ddd3565238

##/delete-friend
Supprime un ami
**Paramètre**
* moi      (username de l'utilisateur)
* password (mot de passe de cet utilisateur)
* supprime (username de l'utilisateur à supprimer)

**Retourne**
* message

##/get-users
Retourne la liste de tout les utilisateurs
**Paramètre**
* username (username de l'utilisateur)

**Retourne**
* personnes

##/get-friends
Retourne la liste d'ami pour un utilisateur
**Paramètre**
* username (username de l'utilisateur)

**Retourne**
* amis

##/get-demandes
Retourne la liste des demandes d'un utilisateur
**Paramètre**
* username (username de l'utilisateur)

**Retourne**
* demandes
