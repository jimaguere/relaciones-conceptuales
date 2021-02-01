#!/usr/bin/env python
# coding: utf-8

# In[28]:


#referencia https://medium.com/towards-artificial-intelligence/multi-class-text-classification-using-pyspark-mllib-doc2vec-dbfcee5b39f2
import findspark
findspark.init() 
from pyspark.sql import SparkSession
import os


MAX_MEMORY = "8g"
spark = SparkSession.builder                     .master("local")                     .appName('multi_class_text_classifiter')                    .config("spark.executor.memory", MAX_MEMORY)                     .config("spark.driver.memory", MAX_MEMORY)                     .config("spark.jars", "C:\\Users\\JBARCO\\Documents\\docs mateo\\ML_text\\Servicios_web\\driverdb\\postgresql-42.2.5.jre6.jar")                     .getOrCreate()


#https://docs.anaconda.com/anaconda-scale/howto/spark-basic/


# In[30]:


spark


# In[3]:




# In[22]:


total_df  = spark.read     .format("jdbc")     .option("url", "jdbc:postgresql://localhost:5433/bd_biblioteca")     .option("dbtable", "doc_resumen")     .option("user", "postgres")     .option("password", "postgres1")     .option("driver", "org.postgresql.Driver")     .load()

total_df .printSchema()


# In[23]:


total_df.count()


# In[5]:


input_rdd = total_df.rdd
input_df = input_rdd.toDF()
input_df.show()


# In[6]:


#total_df.show()
import gensim.parsing.preprocessing as gsp
from pyspark.sql.functions import udf
from pyspark.sql.types import StringType
from gensim import utils


filters = [
           gsp.strip_tags, 
           gsp.strip_punctuation,
           gsp.strip_multiple_whitespaces,
           gsp.strip_numeric,
           gsp.remove_stopwords, 
           gsp.strip_short
           #gsp.stem_text
          ]

def clean_text(x):
    s = x[1]
    s = s.lower()
    s = utils.to_unicode(s)
    for f in filters:
        s = f(s)
    return (x[0],s)


# In[7]:


clean_text(input_rdd.take(1)[0])[1]


# In[8]:


cleaned_rdd = input_rdd.map(lambda x : clean_text(x))
cleaned_df = cleaned_rdd.toDF()
cleaned_df.show()


# In[9]:


import urllib
import nltk
import gensim
import nltk
from nltk.corpus import stopwords
import es_core_news_sm
from ftfy import fix_encoding
import spacy
import spacy.cli
from string import punctuation
# Importa los metodos a trabajar desde NLTK:
# stopwords = palabras reservadas.
from nltk.corpus import stopwords
# word_tokenize = valorizador de palabras.
# sent_tokenize = valorizador de oraciones.
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.stem import WordNetLemmatizer,PorterStemmer
from gensim.models.phrases import Phrases, Phraser
from collections import defaultdict  # For word frequency
from gensim.models import Word2Vec
import multiprocessing
from time import time
import re 
from ftfy import fix_encoding
from spacy import displacy

from sklearn.manifold import TSNE
import matplotlib.pyplot as plt
import networkx as nx

def tokenize(self,text):
        text=text.lower()
        doc1 = self.nlp(text)
        lemmas = [t.norm_ for t in doc1 if not t.is_punct | t.is_stop]
        #lemmas = [t.lemma_ for t in doc1 if not t.is_punct | t.is_stop]
        words = [t.lower() for t in lemmas if len(t) > 1 and t.isalpha()]
        return words

def my_model_wordwvec(texto):
        nlp = spacy.load('es_core_news_sm')
        doc=nlp(texto[1])
        sent=[]
        sent = [t.norm_ for t in doc if not t.is_punct | t.is_stop]
        lemmas = [t.lemma_ for t in doc if not t.is_punct | t.is_stop]
        sent = [t.lower() for t in lemmas if len(t) > 1 and t.isalpha()]
       
        #Crea las frases relevantes de la lista de oraciones:
        phrases = Phrases(sent, min_count=30, progress_per=10000)
        #Transforme el corpus en función de las bigramas detectadas:
        bigram = Phraser(phrases)
        sentences = bigram[sent]
                
        #Entrenamiento del modelo
        cores = multiprocessing.cpu_count() #cuenta el nro de nucles de la pc

        w2v_model = Word2Vec(min_count=2,
                     window=3,
                     size=300,
                     sample=6e-5, 
                     alpha=0.03, 
                     min_alpha=0.0007, 
                     negative=20,
                     workers=cores-1,
                    )
        t = time()
        w2v_model.build_vocab(sentences,progress_per=10000)  # prepare the model vocabulary
        print('Time to build vocab: {} mins'.format(round((time() - t) / 60, 2)))
        t = time()
        w2v_model.train(sentences, total_examples=w2v_model.corpus_count, epochs=6000, report_delay=1)
        print('Time to train the model: {} mins'.format(round((time() - t) / 60, 2)))
        w2v_model.save("./Modelos/"+str(texto[0])+".dat")
        #archivo-salida.py
        f = open ('./proceso.txt','a',encoding="utf-8")
        f.write(""+str(texto[0])+"\n")
        f.close()
        return (texto[0],"ok")



# In[10]:


word2vec_df = input_rdd.map(lambda x : my_model_wordwvec(x))


# In[11]:


word2vec_df = word2vec_df.toDF()
#word2vec_df.show()


# In[ ]:


word2vec_df.show()

