Documentation : web-service
======

Le service est disponible ici : http://appmeetup.appspot.com/

##/add-user
Ajoute un nouvel utilisateur dans la BD
**Paramètre**
* username
* password
* nom
* prenom

**Retourne**
* key

##/ask-friend
Fait un demande à un utilisateur
**Paramètre**
* moi
* password
* demande

**Retourne**
* message

##/add-friend
Ajoute un utilisateur comme ami (il faut que l'utilisateur soit préalablement dans les demandes d'amitié)
**Paramètre**
* moi
* password
* ajoute

**Retourne**
* message

##/list-meetUp
Liste toute les MeetUp d'un utilisateur
**Paramètre**
* moi
* password

**Retourne**
* listMeetUp

##/add-meetUp
Anjoute un nouveau MeetUp
**Paramètre**
* moi
* password
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
