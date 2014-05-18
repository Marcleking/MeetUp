

# AppEngine imports.
from google.appengine.ext import db

class Utilisateur(db.Model):
    username = db.StringProperty()
    password = db.StringProperty()
    nom = db.StringProperty()
    prenom = db.StringProperty()
    listAmi = db.StringListProperty()
    listDemande = db.StringListProperty()
    listMeetUp = db.StringListProperty()
    listDemandeMeetUp = db.StringListProperty()
    listNotification = db.StringListProperty()
    listCalendar = db.StringListProperty()
    
class MeetUp(db.Model):
    nom = db.StringProperty()
    lieu = db.StringProperty()
    duree = db.IntegerProperty()
    heureMin = db.IntegerProperty()
    heureMax = db.IntegerProperty()
    dateMin = db.DateProperty()
    dateMax = db.DateProperty()
    supprimer = db.StringProperty()
    listParticipant = db.StringListProperty()
    

    
