import urllib
from sqlalchemy import create_engine
class Dao:
     def __init__(self):
            DATABASES = {
               'produccion':{
                'NAME': 'bd_biblioteca',
                'USER': 'postgres',
                'PASSWORD': 'postgres1',
                'HOST': 'localhost',
                'PORT': 5433
                }
            }
            db_produccion= DATABASES['produccion']
            prod_engine_string = "postgresql+psycopg2://{user}:{password}@{host}:{port}/{database}".format(
            user = db_produccion['USER'],
            password = db_produccion['PASSWORD'],
            host = db_produccion['HOST'],
            port = db_produccion['PORT'],
            database = db_produccion['NAME'])
            self.engine = create_engine(prod_engine_string)
