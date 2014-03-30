import datetime


from google.appengine.ext import db

SIMPLE_TYPES = (int, long, float, bool, dict, basestring, list)

'''
Source : http://stackoverflow.com/questions/1531501/json-serialization-of-google-app-engine-models
'''    
def to_dict(model): 
    output = {}

    for key, prop in model.properties().iteritems():
        value = getattr(model, key)

        if value is None or isinstance(value, SIMPLE_TYPES):
            output[key] = value
            
        elif isinstance(value, datetime.date):
            # Using ISO 8601 format 'YYYY-MM-DD'
            output[key] = str(value)
            
        elif isinstance(value, db.GeoPt):
            output[key] = {'lat': value.lat, 'lon': value.lon}
            
        elif isinstance(value, db.Model):
            output[key] = to_dict(value)
            
        else:
            raise ValueError('cannot encode ' + repr(prop))

    return output