from flask import Flask
from flask_restplus import Resource, Api
from flask import jsonify
from flask import request
from sqlalchemy import create_engine
import pandas as pd
import json
from Dao import Dao
from Servicio import Service
import json
import pyodbc
import gensim
from pprint import pprint
import pyLDAvis.gensim
from IPython.core.display import HTML
from flask import jsonify
import matplotlib.pyplot as plt
import json
from flask import Response


app = Flask(__name__)
@app.route('/Modelo_topicos', methods=['GET'])
def Modelo_topicos():
    s=Service()
    id_documento = request.args.get('id_documento') 
    topicos = request.args.get('topicos') 

    df=s.GetResumenDocumentosRelacionados(id_documento)
    df.reset_index(inplace=True)

    
    corpus=s.proceso_lda(df)
    dic=gensim.corpora.Dictionary(corpus)
    bow_corpus = [dic.doc2bow(doc) for doc in corpus]
    
    lda_model =  gensim.models.LdaMulticore(bow_corpus, 
                                 num_topics = topicos, 
                               id2word = dic,                                    
                                 passes = 30,
                                per_word_topics=True,
                                 workers = 4)
    
    pyLDAvis.enable_notebook()
    vis = pyLDAvis.gensim.prepare(lda_model, bow_corpus, dic)
    div_id = "pyldavis-in-org-mode"
    html = pyLDAvis.prepared_data_to_html(vis,
                                      template_type="simple",
                                      visid=div_id)
    
    return (html)

@app.route('/Modelo_relaciones', methods=['GET'])
def Modelo_relaciones():
    s=Service()
    id_documento = request.args.get('id_documento') 
    texto=s.getTexto(id_documento)
    modelo=s.getModelo(id_documento)
    conceptos=s.reconocer_conceptos(texto)
    lista_conceptos=s.tokenize(conceptos)
    lista_conceptos=list(set(lista_conceptos))
    links=s.df_conceptual_json(modelo,lista_conceptos)

    return links[['source','target','score']].to_json(orient="records",force_ascii=False)
    
@app.route('/Modelo_relacionesF', methods=['GET'])
def Modelo_relacionesF():
    s=Service()
    id_documento = request.args.get('id_documento') 
    concepto = request.args.get('concepto') 
    texto=s.getTexto(id_documento)
    #modelo=s.modelo_wordwvec(texto)
    modelo=s.getModelo(id_documento)
    conceptos=s.reconocer_conceptos(texto)
    lista_conceptos=s.tokenize(conceptos)
    lista_conceptos=list(set(lista_conceptos))

    if(concepto=="Suave"):
        links=s.df_conceptual_jsonF(modelo,lista_conceptos,2)
    if(concepto=="Medio"):
        links=s.df_conceptual_jsonF(modelo,lista_conceptos,4)
    if(concepto=="Fuerte"):
        links=s.df_conceptual_jsonF(modelo,lista_conceptos,10)

    return links[['source','target','score']].to_json(orient="records",force_ascii=False)
    

if __name__ == '__main__':
    app.run(debug=False, host='localhost', port=8890)
