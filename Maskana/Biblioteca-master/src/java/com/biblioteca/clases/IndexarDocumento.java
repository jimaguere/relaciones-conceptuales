/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;


import com.biblioteca.entidad.Documento;
import com.biblioteca.entidad.DocumentoValorMetaDato;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Ricardo Timaran mateo guerrero
 */
public class IndexarDocumento {

    private Directory directory;
    private String contenido;
    private Documento documento;
    private IndexWriter writer;

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
        
    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    private IndexWriter getWriter() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, new SpanishAnalyzer(Version.LUCENE_46));
        IndexWriter indice = new IndexWriter(directory, config);
        return indice;
    }
    
    public IndexWriter getWriter(String ruta) throws IOException {
        directory = FSDirectory.open(new File(ruta));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, new SpanishAnalyzer(Version.LUCENE_46));
        IndexWriter indice = new IndexWriter(directory, config);
        return indice;
    }

    public void indexarTexto(String ruta) throws IOException {       
        directory = FSDirectory.open(new File(ruta));
        writer = getWriter();
        Document doc = new Document();
        doc.add(new Field("id", "" + documento.getIdDocumento(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));

        doc.add(new Field("contenido", "" + documento.getContenido(),
                Field.Store.NO,
                Field.Index.ANALYZED));
        doc.add(new Field("tipoDocumento", "" + documento.getIdTipoDoc().getIdTipoDoc(),
                Field.Store.NO,
                Field.Index.ANALYZED)); 
        
        doc.add(new Field("tipoDocumentoInfo", "" + documento.getIdTipoDoc().getNombre(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED)); 
                
        for(DocumentoValorMetaDato valor:documento.getDocumentoValorMetaDatoList()){
           if(valor.getIdMetaDato().getMetaDatoIr()){
                doc.add(new Field(valor.getIdMetaDato().getDescripcion(), "" + valor.getValorMetaDato(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
                
                doc.add(new Field(valor.getIdMetaDato().getIdMetaDato().toString(), "" + valor.getValorMetaDato(),
                Field.Store.NO,
                Field.Index.ANALYZED));
           }else{
                doc.add(new Field(valor.getIdMetaDato().getDescripcion(), "" + valor.getValorMetaDato(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
           }
        }
        writer.addDocument(doc);
        writer.close();
        directory.close();
    }

    public void modificarIndice(String ruta) throws IOException {
        directory = FSDirectory.open(new File(ruta));
        writer = getWriter();
        Document doc = new Document();
        doc.add(new Field("id", "" + documento.getIdDocumento(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
        
        doc.add(new Field("contenido", "" +documento.getContenido(),
                Field.Store.NO,
                Field.Index.ANALYZED));
        
        doc.add(new Field("tipoDocumento", "" + documento.getIdTipoDoc().getIdTipoDoc(),
                Field.Store.NO,
                Field.Index.ANALYZED)); 
        
        doc.add(new Field("tipoDocumentoInfo", "" + documento.getIdTipoDoc().getNombre(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED)); 
                
        for(DocumentoValorMetaDato valor:documento.getDocumentoValorMetaDatoList()){
           if(valor.getIdMetaDato().getMetaDatoIr()){
                doc.add(new Field(valor.getIdMetaDato().getDescripcion(), "" + valor.getValorMetaDato(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
                
                doc.add(new Field(valor.getIdMetaDato().getIdMetaDato().toString(), "" + valor.getValorMetaDato(),
                Field.Store.NO,
                Field.Index.ANALYZED));
           }else{
                doc.add(new Field(valor.getIdMetaDato().getDescripcion(), "" + valor.getValorMetaDato(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
           }
        }
        writer.updateDocument(new Term("id", "" + documento.getIdDocumento()), doc);
        writer.close();
        directory.close();
    }
    
    public void eliminarDocumento(String ruta) throws IOException{
        directory = FSDirectory.open(new File(ruta));
        writer = getWriter();
        writer.deleteDocuments(new Term("id", "" + documento.getIdDocumento()));
        writer.close();
        directory.close();
    }
    
    public void vaciarRepositorio(String ruta) throws IOException{
         directory = FSDirectory.open(new File(ruta));
         writer = getWriter();
         writer.deleteAll();
         writer.close();
         directory.close();
    }

    public void crearIndice(String ruta) throws IOException {
        directory = FSDirectory.open(new File(ruta));
        writer=getWriter();
    }
    
    public void cerrar() throws IOException {
        writer.close();
    }
    
    public static void main(String arg[]) throws IOException{
        IndexarDocumento doc=new IndexarDocumento();
        doc.vaciarRepositorio("/home/mateo/repositorio");
    }
    
    public void indexMetaData(String ruta) throws IOException{
        directory = FSDirectory.open(new File(ruta));
        writer = getWriter();
        Document doc = new Document();
        doc.add(new Field("id", "" + documento.getIdDocumento(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));

        doc.add(new Field("tipoDocumento", "" + documento.getIdTipoDoc().getIdTipoDoc(),
                Field.Store.NO,
                Field.Index.ANALYZED)); 
        
        doc.add(new Field("tipoDocumentoInfo", "" + documento.getIdTipoDoc().getNombre(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED)); 
                
        for(DocumentoValorMetaDato valor:documento.getDocumentoValorMetaDatoList()){
           if(valor.getIdMetaDato().getMetaDatoIr()){
                doc.add(new Field(valor.getIdMetaDato().getDescripcion(), "" + valor.getValorMetaDato(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
                
                doc.add(new Field(valor.getIdMetaDato().getIdMetaDato().toString(), "" + valor.getValorMetaDato(),
                Field.Store.NO,
                Field.Index.ANALYZED));
           }else{
                doc.add(new Field(valor.getIdMetaDato().getDescripcion(), "" + valor.getValorMetaDato(),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED));
           }
        }
        writer.addDocument(doc);
        writer.close();
        directory.close();
    }
    
}
