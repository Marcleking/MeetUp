Documentation : web-service
======


##/add-user
**Paramètre**
* username
* password
* nom
* prenom

**Retourne**
* key

##/ask-friend
**Paramètre**
* moi
* password
* demande

**Retourne**
* message

##/add-friend
**Paramètre**
* moi
* password
* ajoute

**Retourne**
* message

##/list-meetUp
**Paramètre**
* moi
* password

**Retourne**
* listMeetUp

##/add-meetUp
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
**Paramètre**
* moi
* password
* ami
* meetUp (key)

**Retourne**
* message

##/accept-meetUp
**Paramètre**
* moi
* password
* meetUp (key)

**Retourne**
* message

##/delete-meetUp
**Paramètre**
**Retourne**

##/delete-user-meetUp
(Se supprimer d'un meetUp)
**Paramètre**
* moi
* password
* meetUp

**Retourne**
* message

##/add-notif
**Paramètre**
* username
* notif

**Retourne**
*message

##/read-notif
**Paramètre**
* moi
* password

**Retourne**
* notif (liste des notifications)

##/delete-friend
**Paramètre**
* moi
* password
* supprime

**Retourne**
* message
