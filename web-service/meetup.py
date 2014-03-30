# -*- coding: utf-8 -*-

# Base python imports.
from datetime import date
from google.appengine.ext import db, webapp
from google.appengine.ext.webapp import util
from models import Utilisateur, MeetUp

import logging

# Lors du d�ploiement sur Google App Engine (en ligne), changer le ligne ci-dessous par :
import simplejson as json
#import json

MSG_KEY     = "key"
MSG_RESULT  = "result"
MSG_SUCCESS = "success"
MSG_ERROR   = "error"


class MainPageHandler(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/html; charset=utf-8'
        self.response.out.write(
            "<html><body><h1>Api de MeetUp</h1></body></html>")

class AddUser(webapp.RequestHandler):
    def get(self):
        try:
            #On inisialise l'utilisateur
            user = Utilisateur()
            
            user.username = self.request.get("username")
            user.password = self.request.get("password")
            user.nom = self.request.get("nom")
            user.prenom = self.request.get("prenom")
            user.listAmi = []
            user.listDemande = []
            user.listMeetUp = []
            
            
            q = Utilisateur.all()
            q.filter('username =', user.username)
            result = q.get(keys_only=True)
            
            if result is not None:
                response = {
                    MSG_RESULT : MSG_ERROR,
                    "message" : "L'utilisateur existe"
                }
            else: #S'il n'y a pas d'utilisateur avec le même username on l'ajout
                user.put()
                
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    MSG_KEY : str(user.key())
                }
                
            self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
            self.response.out.write(json.dumps(response))
            
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class AskFriend(webapp.RedirectHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            me = listUser.get()
            
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("demande"))
            theFriend = listUser.get()
            
            #On s'assure que c'est les bonnes personnes
            if me is not None and theFriend is not None and me.password == self.request.get("password"):
                #On s'assure qu'il n'essaye pas de se demander lui même
                if me.username != theFriend.username: 

                    listeDemande = theFriend.listDemande
                    
                    #On fait la demande d'amitier uniquement si elle n'est pas déjà la
                    continuer = 1
                    for demande in listeDemande:
                        if demande == me.username:
                            continuer = 0
                    
                    if continuer:
                        listeDemande.append(me.username)
                    
                    theFriend.listDemande = listeDemande
                    
                    theFriend.put()
                    
                    response = {
                        MSG_RESULT : MSG_SUCCESS,
                        "message" : "Demande fait!"
                    }
            
            self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
            self.response.out.write(json.dumps(response))
            
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class AddFriend(webapp.RequestHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            me = listUser.get()
            
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("ajoute"))
            theFriend = listUser.get()
            
            #On s'assure que ce soit la bonne personne
            if me is not None and theFriend is not None and me.password == self.request.get("password"):
                #On s'assure qu'elle n'essaye pas de s'ajouté elle même
                if me.username != theFriend.username: 

                    listeDemande = me.listDemande
                    
                    nouvelleListDemande = []
                    nouvelAmi = None
                    
                    #On met à jour la liste des demandes et on va chercher le nouvel ami
                    for demande in listeDemande:
                        if demande == theFriend.username:
                            nouvelAmi = theFriend.username
                        else:
                            nouvelleListDemande.append(demande)
                    
                    #On ajoute le nouvel ami
                    listeAmi = me.listAmi
                    if nouvelAmi is not None:
                        listeAmi.append(nouvelAmi)
                    
                    me.listAmi = listeAmi
                    me.listDemande = nouvelleListDemande
                    me.put()
                    
                    response = {
                        MSG_RESULT : MSG_SUCCESS,
                        "message" : "Ami ajouté!"
                    }
            
            self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
            self.response.out.write(json.dumps(response))
            
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class DeleteFriend(webapp.RequestHandler):
    def get(self):
        try:
            
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            me = listUser.get()
            
            friendToDelete = self.request.get("supprime")
            
            if me is not None and friendToDelete is not None and me.password == self.request.get("password"):
            
                listeAmi = []
                #On met à jour la liste des amis
                if me.listAmi is not None:
                    for ami in me.listAmi:
                        if ami != friendToDelete:
                            listeAmi.append(ami)
                
                listeDemande = []
                #On met à jour la liste des demandes
                for ami in me.listDemande:
                    if ami != friendToDelete:
                        listeDemande.append(ami)
                
                me.listAmi = listeAmi
                me.listDemande = listeDemande
                
                me.put()
                
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    "message" : "Ami supprimé!"
                }
        
                self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
                self.response.out.write(json.dumps(response))
            
        except Exception, ex:
            logging.error(ex)
            self.error(500)

class AddMeetUp(webapp.RedirectHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            user = listUser.get()
            
            if user.password == self.request.get("password"):
                dateMin = self.request.get("dateMin")
                arrayDateMin = dateMin.split('-')
                
                dateMax = self.request.get("dateMax")
                arrayDateMax = dateMax.split('-')
                
                #On inisialise le meetUp
                meetUp = MeetUp(parent=user)
                
                #On entre les données
                meetUp.nom = self.request.get("nom")
                meetUp.lieu = self.request.get("lieu")
                meetUp.duree = int(self.request.get("duree"))
                meetUp.heureMin = int(self.request.get("heureMin"))
                meetUp.heureMax = int(self.request.get("heureMax"))
                meetUp.dateMin = date(int(arrayDateMin[0]), int(arrayDateMin[1]), int(arrayDateMin[2]))
                meetUp.dateMax = date(int(arrayDateMax[0]), int(arrayDateMax[1]), int(arrayDateMax[2]))
                
                meetUp.put()
                
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    MSG_KEY    : str(meetUp.key())
                }
                    
                self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
                self.response.out.write(json.dumps(response))
        
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class ListMeetUp(webapp.RequestHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            user = listUser.get()
            
            if user.password == self.request.get("password"):
            
                listMeetUp = MeetUp.all()
                listMeetUp.ancestor(user.key())
                
                for meetUp in listMeetUp.run():
                    unMeetUp = {
                        'key': str(meetUp.key()),
                        'nom': str(meetUp.nom),
                        'lieu': str(meetUp.lieu),
                        'duree': str(meetUp.duree),
                        'heureMin': str(meetUp.heureMin),
                        'heureMax': str(meetUp.heureMax),
                        'dateMax': str(meetUp.dateMax),
                        'participant': meetUp.listParticipant
                    }
                    
                    list.append(unMeetUp)
                
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    "listMeetUp"    : list
                }
                    
                self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
                self.response.out.write(response)
                
                        
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class DeleteMeetUp(webapp.RedirectHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            user = listUser.get()
            
            
            if user.password == self.request.get("password"):
                
                idMeetUp = self.request.get("supprime")
                
                listMeetUp = MeetUp.all()
                listMeetUp.ancestor(user.key())
                
                #On parcours les meetup de l'utilisateur, si le meetup est la on le supprime
                for meetUp in listMeetUp.run():
                    if str(meetUp.key()) == idMeetUp:
                        db.delete(meetUp)
                        
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    "message"    : "Le MeetUp à bien été supprimer!"
                }
                    
                self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
                self.response.out.write(response)

        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
            
class InviteUserAtMeetUp(webapp.RedirectHandler):
    def get(self):
        try:
            #On va chercher l'utilisateur
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            user = listUser.get()
            
            #On va chercher l'ami à ajouter
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("ami"))
            amiAjouter = listUser.get()
            
            
            idMeetUp = self.request.get("meetUp")
            motDePasse = self.request.get("password")
            
            response = {
                MSG_RESULT : MSG_ERROR,
                "message"    : "Vous n'avez pas les droits"
            }
            
            isFriend = 0
            
            #On vérifie si l'ami existe
            if amiAjouter is not None:
                #On vérifie que l'ami à ajouter est bel et bien un ami de l'utilisateur
                for ami in user.listAmi:
                    if ami == amiAjouter.nom:
                        isFriend = 1
            else:
                response = {
                    MSG_RESULT : MSG_ERROR,
                    "message"    : "Cet ami n'existe pas"
                }
               
            #On parcours tous les meetup de l'utilisateur
            listMeetUp = MeetUp.all()
            listMeetUp.ancestor(user.key())
            for meetUp in listMeetUp.run():
                #Si l'utilisateur à le droit d'invité l'ami
                if str(meetUp.key()) == idMeetUp and user.password == motDePasse and isFriend:
                    listeInvitation = amiAjouter.listDemandeMeetUp
                    
                    #On vérifie si l'ami est à ajouter
                    aAjouter = 1
                    for invitation in listeInvitation:
                        if invitation == idMeetUp:
                            aAjouter = 0
                    
                    #On ajoute l'ami
                    if aAjouter:
                        listeInvitation.append(idMeetUp)
                        
                    amiAjouter.listDemandeMeetUp = listeInvitation
                    amiAjouter.put()
                    
                    response = {
                        MSG_RESULT : MSG_SUCCESS,
                        "message"    : "L'ami à bien été ajouter"
                    }
                
            self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
            self.response.out.write(response)
            
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class AcceptInviteMeetUp(webapp.RequestHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            me = listUser.get()
            meetUp = db.get(self.request.get("meetUp"))
            
            #On vérifie que j'ai bien été invité au meetUp
            isInvite = 0
            for invitation in me.listDemandeMeetUp:
                if invitation == str(meetUp.key()):
                    isInvite = 1
            
            if me.password == self.request.get("password") and isInvite:
                #J'ajoute le meetup des mes meet up
                listeMeetUp = me.listMeetUp
                listeMeetUp.append(str(meetUp.key()))
                me.listMeetUp = listeMeetUp
                
                #J'enlève la demande d'invitation
                listeDemandeMeetUp = []
                for demandeMeetUp in me.listDemandeMeetUp:
                    if demandeMeetUp != str(meetUp.key()):
                        listeDemandeMeetUp.append(demandeMeetUp)
                        
                me.listDemandeMeetUp = listeDemandeMeetUp
                
                me.put()
                
                #Je me met comme participant dans le meetUp
                listeParticipant = meetUp.listParticipant
                listeParticipant.append(me.username)
                meetUp.listParticipant = listeParticipant
                meetUp.put()
                
            response = {
                MSG_RESULT : MSG_SUCCESS,
                "message"    : "Votre participation à bien été enregistré"
            }
                
            self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
            self.response.out.write(response)
                
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class removeUserFromMeetUp(webapp.RequestHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            me = listUser.get()
            
            idMeetUp = self.request.get("meetUp")
            meetUp = db.get(self.request.get("meetUp"))
            
            if me.password == self.request.get("password"):
                #J'enleve le MeetUp de mes MeetUp
                listeMeetUp = []
                for meetUp in me.listMeetUp:
                    if meetUp != idMeetUp:
                        listeMeetUp.append(meetUp)
                
                listeDemandeMeetUp = []
                for demande in me.listDemandeMeetUp:
                    if demande != idMeetUp:
                        listeDemandeMeetUp.append(demande)
                    
                me.listDemandeMeetUp = listeDemandeMeetUp
                me.listMeetUp = listeMeetUp
                me.put()
                
                #Je m'enlève des participants
                listeParticipant = []
                for participant in meetUp.listParticipant:
                    if participant != me.username:
                        listeParticipant.append(participant)
                
                meetUp.listParticipant = listeParticipant
                meetUp.put()
                
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    "message"    : "Votre participation à bien été supprimé"
                }
                    
                self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
                self.response.out.write(response)
         
        except Exception, ex:
            logging.error(ex)
            self.error(500)   
        
            
class AddNotif(webapp.RequestHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("username"))
            me = listUser.get()
            
            #On ajoute la notification à la liste des notifications
            listNotif = me.listNotification
            listNotif.append(self.request.get("notif"))
            me.listNotification = listNotif
            
            me.put()
            
            response = {
                MSG_RESULT : MSG_SUCCESS,
                "message" : "Notification envoyé!"
            }
    
            self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
            self.response.out.write(json.dumps(response))
            
            
        except Exception, ex:
            logging.error(ex)
            self.error(500)
            
class ReadNotif(webapp.RequestHandler):
    def get(self):
        try:
            listUser = Utilisateur.all()
            listUser.filter("username =", self.request.get("moi"))
            me = listUser.get()
            
            if me.password == self.resquest.get("password"):
                #On va chercher les notifications puis on les supprimes puisqu'on les a lu
                listeNotification = me.listNotification
                me.listNotification = []
                
                me.put()
                
                response = {
                    MSG_RESULT : MSG_SUCCESS,
                    "notif" : listeNotification
                }
        
                self.response.headers["Content-Type"] = 'application/json; charset=utf-8'
                self.response.out.write(json.dumps(response))
            
        
        except Exception, ex:
            logging.error(ex)
            self.error(500)



def configurerHandler():
    application = webapp.WSGIApplication([('/',              MainPageHandler),
                                          ('/add-user',      AddUser),
                                          ('/ask-friend',    AskFriend),
                                          ('/add-friend',    AddFriend),
                                          ('/list-meetUp',   ListMeetUp),
                                          ('/add-meetUp',    AddMeetUp),
                                          ('/invite-friend', InviteUserAtMeetUp),
                                          ('/accept-meetUp', AcceptInviteMeetUp),
                                          ('/delete-meetUp', DeleteMeetUp),
                                          ('/delete-user-meetUp', removeUserFromMeetUp),
                                          ('/add-notif',     AddNotif),
                                          ('/read-notif',    ReadNotif),
                                          ('/delete-friend', DeleteFriend)],
                                         debug=True)
    util.run_wsgi_app(application)
    

if __name__ == "__main__":
    configurerHandler()
    
    
    
    
