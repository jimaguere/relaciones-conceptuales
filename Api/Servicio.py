import urllib
from sqlalchemy import create_engine
import sqlalchemy as sa
from Dao import Dao
import pandas as pd
from joblib import  load
import nltk
import psycopg2
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
import numpy  as np
import re 
from ftfy import fix_encoding
from spacy import displacy

from sklearn.manifold import TSNE
import matplotlib.pyplot as plt
import networkx as nx



class Service:
    dao=Dao()
    module_url = "./Modelos/modelo_dm0_20_2000.txt" 
    embed = gensim.models.doc2vec.Doc2Vec.load(module_url)
    nlp = es_core_news_sm.load()
    stop=set(stopwords.words('spanish'))
    non_words = list(punctuation)
    non_words.extend(['¿', '¡'])
    non_words.extend(stop)
    non_words.extend(map(str,range(10)))  
    
    
    def __init__(self):
        self.dao=Dao()
        self.module_url = "./Modelos/modelo_dm0_20_2000.txt" 
        self.embed = gensim.models.doc2vec.Doc2Vec.load(self.module_url)
        self.nlp = es_core_news_sm.load()
        self.stop=set(stopwords.words('spanish'))
        
        self.non_words = list(punctuation)
        self.non_words.extend(['¿', '¡'])
        self.non_words.extend(self.stop)
        self.non_words.extend(map(str,range(10)))  
        
    def getModelo(self,id_documento):
        rutaModelo="./Modelos/"+str(id_documento)+".dat"
        modelo = Word2Vec.load(rutaModelo)
        return modelo
        
    def limpiar(self,text):
        self.nlp.max_length = 10000000 
        text=text.lower()
        doc = self.nlp(text,disable = ['ner', 'parser'])
        #lemmas = [t.norm_ for t in doc if not t.is_punct | t.is_stop and t not in stop]
        lemmas = [t.norm_ for t in doc if not t.is_punct | t.is_stop ]
        words = [t.lower() for t in lemmas if len(t) > 3 and t.isalpha()]
        return words
    
    def GetResumenes(self)  :
        sql="select * from documento_resumen"
        df=pd.read_sql(sa.text(sql),self.dao.engine)
        self.dao.engine.dispose()
        return df
    
    def GetResumenesD(self)  :
        sql="select id_documento,resumen from documento_resumen"
        df=pd.read_sql(sa.text(sql),self.dao.engine)
        self.dao.engine.dispose()
        return df
    
    def proceso_lda(self,df):
        corpus=[]
        stem=PorterStemmer()
        lem=WordNetLemmatizer()
        for news in df['contenido'].dropna():
            words=[w for w in word_tokenize(news) if (w not in self.stop)]
        
            words=[lem.lemmatize(w) for w in words if len(w)>2]
        
            corpus.append(words)
        return corpus
    
    def GetDocumentoConResumen(self):
        sql="select id_documento,resumen from documento"
        df=pd.read_sql(sa.text(sql),self.dao.engine)
        self.dao.engine.dispose()
        return df

    

        
        
    def GetDocumentosRelacionados(self,idDocumento)  :
        sql="select * from public.fn_documentos_relacionados(:idDocumento)"
        df=pd.read_sql(sa.text(sql),self.dao.engine, params={'idDocumento': idDocumento})
        self.dao.engine.dispose()
        return df
    
    def GetResumenDocumentosRelacionados(self,idDocumento)  :
        sql="select * from public.fn_documentos_resumen_relacionados(:idDocumento)"
        df=pd.read_sql(sa.text(sql),self.dao.engine, params={'idDocumento': idDocumento})
        self.dao.engine.dispose()
        return df
    
    def GetDocumentos(self)  :
        sql="select * from documento"
        df=pd.read_sql(sa.text(sql),self.dao.engine)
        self.dao.engine.dispose()
        return df
    
    def GetVectors(self,texto):
        busqueda_limpia=self.limpiar(texto)
        vector=self.embed.infer_vector(busqueda_limpia)
        return vector
        
    
    def GetBusqueda(self,busqueda):
        busqueda_limpia=self.limpiar(busqueda)
        vector=self.embed.infer_vector(busqueda_limpia)
        sql="select * from public.fn_busqueda_documentos(:v0,:v1,:v2,:v3,:v4,:v5,:v6,:v7,:v8,:v9,:v10,:v11,:v12,:v13,:v14,:v15,:v16,:v17,:v18,:v19)"
        df=pd.read_sql(sa.text(sql),self.dao.engine, 
                       params={'v0': str(vector[0]),
                               'v1': str(vector[1]),
                               'v2': str(vector[2]),
                               'v3': str(vector[3]),
                               'v4': str(vector[4]),
                               'v5': str(vector[5]),
                               'v6': str(vector[6]),
                               'v7': str(vector[7]),
                               'v8': str(vector[8]),
                               'v9': str(vector[9]),
                               'v10': str(vector[10]),
                               'v11': str(vector[11]),
                               'v12': str(vector[12]),
                               'v13': str(vector[13]),
                               'v14': str(vector[14]),
                               'v15': str(vector[15]),
                               'v16': str(vector[16]),
                               'v17': str(vector[17]),
                               'v18': str(vector[18]),
                               'v19': str(vector[19])
                              })
        self.dao.engine.dispose()
        return df
    
    
    def reconocer_conceptos(self,texto):
        self.nlp = spacy.load('es_core_news_sm')
        self.doc=self.nlp(texto)
        conceptos=""
        for palabra in self.doc.ents:
            p=palabra.text.replace(" ","_")
            for t in palabra.text.split(' '):
                d1=self.nlp(t.lower())
                for ent in  d1:
                    if not ent.is_punct and not ent.is_stop and len(ent)>1:
                        conceptos=str(conceptos)+" "+str(ent)
        return conceptos
    
    def tokenize(self,text):
        text=text.lower()
        doc1 = self.nlp(text)
        lemmas = [t.norm_ for t in doc1 if not t.is_punct | t.is_stop]
        lemmas = [t.lemma_ for t in doc1 if not t.is_punct | t.is_stop]
        words = [t.lower() for t in lemmas if len(t) > 1 and t.isalpha()]
        return words
    
    def tokenizer(self,text):
        text=text.lower()
        doc1 = self.nlp(text)
        lemmas = [t.norm_ for t in doc1 if not t.is_punct | t.is_stop]
        lemmas = [t.lemma_ for t in doc1 if not t.is_punct | t.is_stop]
        words = [t.lower() for t in lemmas if len(t) > 1 and t.isalpha()]
        return ''.join(words)
    
    def replay(self,text):
        text=text.replace("algoritmos","algoritmo")
        text=text.replace("patrones","patrón")
        text=text.replace("acopladas","acoplada")
        text=text.replace("herramientas","herramienta")
        text=text.replace("tablas","tabla")
        text=text.replace("frecuentes","frecuente")
        text=text.replace("sistemas","sistema")
        text=text.replace("itemsets","itemset")
        text=text.replace("generar","genera")
        text=text.replace("tareas","tarea")
        text=text.replace("paquetes","paquete")
        text=text.replace("árboles","árbol")
        text=text.replace("artistas","artista")
        text=text.replace("maestros","maestro")
        text=text.replace("carrozas","carroza")
        text=text.replace("carnavales","carnaval")
        text=text.replace("bosques","bosque")
        text=text.replace("garrapatas","garrapata")
        text=text.replace("blood","sangre")
        text=text.replace("niños","niño")
        text=text.replace("niñas","niña")
        text=text.replace("juegos","juego")
        text=text.replace("clases","clase")
        text=text.replace("estudiantes","estudiante")
        text=text.replace("docentes","docente")
        text=text.replace("jugar","juego")
        text=text.replace("jugando","juego")
        text=text.replace("hombres","hombre")
        text=text.replace("mujeres","mujer")
        text=text.replace("relaciones","relación")
        text=text.replace("casos","caso")
        text=text.replace("suicidas","suicida")
        text=text.replace("fríjol","frijol")
        if text=='items':
            text=text.replace("items","items")
        return ''.join(text)
    
    def getTexto(self,id_documento):
        sql="select resumen from documento where id_documento=:v0"
        #sql="select contenido as resumen from documento where id_documento=:v0"
        df=pd.read_sql(sa.text(sql),self.dao.engine,params={'v0':id_documento})
        self.dao.engine.dispose()
        contenido=''
        for res in df.resumen:
            contenido=contenido+' '+res
        return contenido
        

    
    def modelo_wordwvec(self,texto):
        self.nlp = spacy.load('es_core_news_sm')
        self.doc=self.nlp(texto)
        sent=[]
        for num,oracion in enumerate(self.doc.sents):
            o=self.tokenize(str(oracion))
            sent.append(o) 
        #Crea las frases relevantes de la lista de oraciones:
        phrases = Phrases(sent, min_count=30, progress_per=10000)
        #Transforme el corpus en función de las bigramas detectadas:
        bigram = Phraser(phrases)
        sentences = bigram[sent]
        #palabras mas frecuentes
        word_freq = defaultdict(int)
        for sent in sentences:
            for i in sent:
                word_freq[i] += 1
                
        #Entrenamiento del modelo
        cores = multiprocessing.cpu_count() #cuenta el nro de nucles de la pc

        w2v_model = Word2Vec(min_count=2,
                     window=3,
                     size=300,
                     sample=6e-5, 
                     alpha=0.03, 
                     min_alpha=0.0007, 
                     negative=20,
                     workers=cores-1)
        t = time()
        w2v_model.build_vocab(sentences,progress_per=10000)  # prepare the model vocabulary
        print('Time to build vocab: {} mins'.format(round((time() - t) / 60, 2)))
        t = time()
        w2v_model.train(sentences, total_examples=w2v_model.corpus_count, epochs=6000, report_delay=1)
        print('Time to train the model: {} mins'.format(round((time() - t) / 60, 2)))
        return w2v_model
    
    
    def tsne_plot(self,model):
        "Creates and TSNE model and plots it"
        labels = []
        tokens = []

        for word in model.wv.vocab:
            tokens.append(model[word])
            labels.append(word)
        

        tsne_model = TSNE(perplexity=40, n_components=2, init='pca', n_iter=2500, random_state=23)
        new_values = tsne_model.fit_transform(tokens)

        x = []
        y = []
        for value in new_values:
            x.append(value[0])
            y.append(value[1])
        
        plt.figure(figsize=(16, 16)) 
        for i in range(len(x)):
            plt.scatter(x[i],y[i])
            plt.annotate(labels[i],
                     xy=(x[i], y[i]),
                     xytext=(5, 2),
                     textcoords='offset points',
                     ha='right',
                     va='bottom')
       
        return (plt.show())
    
    
    def mapa_conceptual(self,model,conceptos):
        "Creates and TSNE model and plots it"
        labels = []
        tokens = []

        for word in conceptos:
            try:
                tokens.append(model[word])
                labels.append(word)
            except KeyError:
                continue
        
        tsne_model = TSNE(perplexity=40, n_components=2, init='pca', n_iter=2500, random_state=23)
        new_values = tsne_model.fit_transform(tokens)

        x = []
        y = []
        for value in new_values:
            x.append(value[0])
            y.append(value[1])
        
        plt.figure(figsize=(7, 7)) 
        for i in range(len(x)):
            plt.scatter(x[i],y[i])
            plt.annotate(labels[i],
                     xy=(x[i], y[i]),
                     xytext=(5, 2),
                     textcoords='offset points',
                     ha='right',
                     va='bottom')
        return (plt.show())
    

    
    def df_conceptual(self,w2v_model,lista_concepos):
        df=pd.DataFrame()
        i=0
        while i<len(lista_concepos):
            a=[]
            s=[]
            try:
                result=w2v_model.wv.most_similar(positive=[lista_concepos[i].lower()],topn=2)
                for r in result:
                    a.append(r[0])
                    s.append(r[1])
                    df=df.append({'source':lista_concepos[i],'target':r[0],'score':r[1]},ignore_index=True)
                #df=df.append({'bigram':(lista_concepos[i],a[0]),'score':s[0]},ignore_index=True)
                i=i+1
            except  KeyError:
                    i=i+1
                    continue
        return df
        
    def df_conceptual_json(self,w2v_model,lista_concepos):
        df=pd.DataFrame()
        i=0
        while i<len(lista_concepos):
            a=[]
            s=[]
            try:
                result=w2v_model.wv.most_similar(positive=[lista_concepos[i].lower()],topn=4)
                
                for r in result:
                    a.append(r[0])
                    s.append(r[1])                                                                               
                    df=df.append({'source':self.replay(lista_concepos[i]),                                                                                       'target':self.replay(r[0]),'score':r[1]},ignore_index=True)
                    #df=df.append({'source':lista_concepos[i],'target':r[0],'score':r[1]},ignore_index=True)
      
                #df=df.append({'source':lista_concepos[i],'target':a[0],'score':s[0]},ignore_index=True)
                i=i+1
            except  KeyError:
                    i=i+1
                    continue
        return df 
    
    def df_conceptual_jsonF(self,w2v_model,lista_concepos,tope):
        df=pd.DataFrame()
        i=0
        while i<len(lista_concepos):
            a=[]
            s=[]
            try:
                result=w2v_model.wv.most_similar(positive=[lista_concepos[i].lower()],topn=tope)
                
                for r in result:
                    a.append(r[0])
                    s.append(r[1])                                                                               
                    df=df.append({'source':self.replay(lista_concepos[i]),                                                                                       'target':self.replay(r[0]),'score':r[1]},ignore_index=True)
                    #df=df.append({'source':lista_concepos[i],'target':r[0],'score':r[1]},ignore_index=True)
      
                #df=df.append({'source':lista_concepos[i],'target':a[0],'score':s[0]},ignore_index=True)
                i=i+1
            except  KeyError:
                    i=i+1
                    continue
        return df  
 
    
    
    
                
    def  grafo_conceptual(self,df):
        # Create network plot 
        d = df.set_index('bigram').T.to_dict('records')
        G = nx.Graph()
        # Create connections between nodes
        for k, v in d[0].items():
            G.add_edge(k[0], k[1], weight=(v * 100))
        
        fig, ax = plt.subplots(figsize=(100, 100))
        pos = nx.spring_layout(G, k=2)
        # Plot networks
        nx.draw_networkx(G, pos,
                 font_size=70,
                 width=3,
                 edge_color='grey',
                 node_color='purple',
                 with_labels = True,
                 ax=ax)
        # Create offset labels
        for key, value in pos.items():
            x, y = value[0]+.135, value[1]+.045
            ax.text(x, y,
                s=str(key),
                bbox=dict(facecolor='red', alpha=0.25),
                horizontalalignment='center', fontsize=13)
         
        return (plt.show())
        
                
         
         

        
    

        
        
    
    
    def ResumirDocumento(self,texto,id_documento):
        try:
            ##nltk.download('stopwords')
            ##nltk.download('punkt')
            SW = set(stopwords.words("spanish"))
            text =texto
            words = word_tokenize(text)
        
            # Se crea un diccionario para crear una tabla de frecuencias de las palabras.
            freqTable = dict()

            # Con un for se recorre el texto y se almacena en la tabla.
            for word in words:
                word = word.lower() # setea las palabras en minúscula y las almacena en word.
                if word in SW: 
                    continue # Si la palabra se encuentra en SW, continua con el ciclo.
                if word in freqTable: # Si la palabra ya se encuentra en la tabla frecuencia,
                    freqTable[word] += 1 # Suma 1 a la posición donde se encuentra la palabra.
                else:
                    freqTable[word] = 1 # Sino, la palabra en la TF va a ser igual a 1.
                
            # Crea una variable y un diccionario.

            # Variable sentences para almacenar las oraciones a valorizar del texto.
            sentences = sent_tokenize(text)

            # Diccionario sentenceValue para almacenar los valores de las oraciones.
            sentenceValue = dict()
        
            #Se crea un ciclo for para recorrer las oraciones que se encuentran en el texto.
            for sentence in sentences:
                # Se crea un segundo for para recorrer los items que se encuentran el la TF.
                for word, freq in freqTable.items():
                    if word in sentence.lower(): # Si la palabra se encuentra en las oraciones (en minuscula)
                        if sentence in sentenceValue: # Y si la oración está en el diccionario de las oraciones a valorizar.
                            sentenceValue[sentence] += freq # Entonces sume 1 al número de frecuencia en la posición de la oración del sV.
                        else:
                            sentenceValue[sentence] = freq # Sino, que el valor de la posición de la oración sea igual a la frecuencia.
                        
            # Se crea una variable donde se almacena la suma de los valores.
            sumValues = 0

            # Se crea un ciclo for para evaluar la oración dentro del Diccionario de las oraciones valorizadas.
            for sentence in sentenceValue:
                sumValues += sentenceValue[sentence] # Se suma 1 al valor de la Oración en su respectiva posición
            
            # Valor promedio de una oración desde un texto original
            average = int(sumValues/ len(sentenceValue)) # Divide la suma de valores en la total de oraciones valorizadas.
        
        
            # Se crea una variable para almacenar el resumen a imprimir.
            summary = ''

            # Se crea un for para recorrer las oraciones almacenadas
            for sentence in sentences:
    
                #Donde si, la oración está las oraciones Valorizadas y la posición de la oración es mayor que 1.2 veces el promedio:
                if (sentence in sentenceValue) and (sentenceValue[sentence] > (1.2 * average)):
                    # El resumen va a agregar un espacio más la oración que aprobó la condición.
                    summary += " " + sentence
                    
            df=pd.DataFrame(columns=('id_documento', 'resumen'))
            df=df.append({'id_documento':id_documento, 'resumen':summary}, ignore_index=True)
            df.to_sql('documento_resumen', con=self.dao.engine, if_exists='append',index=False)
            self.dao.engine.dispose()
            return (summary)
        except:
            df=pd.DataFrame(columns=('id_documento', 'resumen'))
            df=df.append({'id_documento':id_documento, 'resumen':'ERROR_RESUMEN'}, ignore_index=True)
            df.to_sql('documento_resumen', con=self.dao.engine, if_exists='append',index=False)
            self.dao.engine.dispose()
            return ("ERROR_RESUMEN");
        
    def getDoc2vec(self,id_documento):
        sql="select vec.*,distancia,case when distancia<0.34 then 0 else 1 end grupo from fn_evaluacion(:id_documento) ml join documento d on ml.id_documento=d.id_documento join documentos_doc2_vec  vec on vec.id_documento=ml.id_documento"
        df=pd.read_sql(sa.text(sql),self.dao.engine,params={'id_documento':id_documento})
        self.dao.engine.dispose()
        return df
     
        
        
        
        
            
            
  
        
        
        