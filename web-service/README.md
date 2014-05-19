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
* nom
* lieu
* duree
* heureMin
* heureMax
* dateMin (2011-12-28)
* dateMax (2011-12-31)

**Retourne**
* key

##/invite-friend
Invite un utilisateur à être ami
**Paramètre**
* moi
* password
* ami
* meetUp (key)

**Retourne**
* message

##/accept-meetUp
Accepte un demande de participation à un meetUp
**Paramètre**
* moi
* password
* meetUp (key)

**Retourne**
* message

##/delete-meetUp
Supprime un MeetUp (fonctionnel uniquement pour le créateur du MeetUp)
**Paramètre**
* moi
* password
* supprime

**Retourne**
* message

##/delete-user-meetUp
Pour supprimer sa participation à un meetUp
**Paramètre**
* moi
* password
* meetUp

**Retourne**
* message

##/add-notif
Ajoute une notification à un utilisateur
**Paramètre**
* username
* notif

**Retourne**
*message

##/read-notif
Lit les notifications d'un utilisateur (Les notifications sont supprimer après)
**Paramètre**
* moi
* password

**Retourne**
* notif (liste des notifications)

##/delete-friend
Supprime un ami
**Paramètre**
* moi
* password
* supprime

**Retourne**
* message

##/get-users
Retourne la liste de tout les utilisateurs
**Paramètre**
* username

**Retourne**
* personnes

##/get-friends
Retourne la liste d'ami pour un utilisateur
**Paramètre**
* username

**Retourne**
* amis

##/get-demandes
Retourne la liste des demandes d'un utilisateur
**Paramètre**
* username

**Retourne**
* demandes
