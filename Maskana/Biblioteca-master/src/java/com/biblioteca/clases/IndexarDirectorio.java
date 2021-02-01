/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;

import com.biblioteca.entidad.Documento;
import com.biblioteca.entidad.DocumentoValorMetaDato;
import com.biblioteca.entidad.MetaDato;
import com.biblioteca.entidad.TipoDocumento;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.st;


/**
 *
 * @author mateo
 */
public class IndexarDirectorio {

    File dir;

    public void recorrerDirectorioArchivo(String directorio, String indice) throws IOException {
        dir = new File(directorio);
        File[] files = dir.listFiles();
        for (int i = 89; i < files.length; i++) {
            File file = files[i];
            if (file.getName().endsWith(".pdf") && file.length() <= 11930889 && file.getName().contains("85070")) {
                System.out.println(i);
                return;
            }
        }
    }

    public void recorrerDirectorio(String directorio, String indice) throws IOException, CryptographyException, InvalidPasswordException {
        dir = new File(directorio);
        File[] files = dir.listFiles();
        int contador = 1;
        String idNoIndexados = "";
        //quedo aqui 84749
        for (int i = 307; i < files.length; i++) {
            try {
                File file = files[i];
                if (file.getName().endsWith(".pdf")) {
                    IndexarDocumento indiceArchivos = new IndexarDocumento();
                    InputStream archivoI = new FileInputStream(file);

                  //  ExtracPdfText texto = new ExtracPdfText(archivoI);
                    Documento doc = new Documento();
                //    doc.setId(Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf(".")).replaceAll(" ", "").trim()));
               //     doc.setTitulo(texto.extraerTitulo());
             
                    indiceArchivos.setDocumento(doc);
                  //  indiceArchivos.setContenido(texto.extraerTexto());
                    indiceArchivos.indexarTexto(indice);
                    archivoI.close();
                   // texto.cerrar();
                    System.out.println(contador + " -------- " + doc.getIdDocumento());
                    contador++;
                }
            } catch (Exception e) {
                idNoIndexados = idNoIndexados + files[i].getName() + "____" + e.toString() + "\n";
            }
        }
        System.out.println("archivos No indexados");
        System.out.println(idNoIndexados);

    }

    public void crearArchivo(String nombreArchivo, String contenido) {
        FileWriter fw = null; // la extension al archivo
        try {
            fw = new FileWriter(nombreArchivo);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter salArch = new PrintWriter(bw);
            salArch.write(contenido);
            salArch.close();
        } catch (IOException ex) {
        }
    }
    
    public void archivosNoIndexados(String archivo){
         FileWriter fw = null; // la extension al archivo
        try {
            fw = new FileWriter("opt/mateo/no_idexados.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter salArch = new PrintWriter(bw);
            salArch.println(archivo);
            salArch.close();
        } catch (IOException ex) {
        }
    }
    

    public void guardarTextoPdfTxt(String carpetaPdf, String carpetaTxt) {
        File origenPdf = new File(carpetaPdf);
        File[] archivosPdf = origenPdf.listFiles();
        for (int i = 75; i < archivosPdf.length; i++) {
            try {
                File file = archivosPdf[i];
                if (file.getName().endsWith(".pdf") && file.length() > 11930889) {
                    InputStream archivoI = new FileInputStream(file);
                    ExtracPdfText texto = new ExtracPdfText(archivoI);
                    String contenido = texto.extraerTexto();
                    crearArchivo(carpetaTxt+file.getName().toLowerCase().replaceAll("pdf", "txt"), contenido);
                    System.out.println(i + " -------- " + file.getName());
                }
            } catch (Exception e) {
                this.archivosNoIndexados(archivosPdf[i].getName());
            }
        }

    }
    
    public void luceneEspaniol(){
        SpanishAnalyzer esp=new SpanishAnalyzer(Version.LUCENE_46);
        System.out.println(esp.getStopwordSet().toString());
    }
    
    public void indexarTexto(String directorio,String indice){
        dir = new File(directorio);
        File[] files = dir.listFiles();
        int contador = 1;
        String idNoIndexados = "";
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                if (file.getName().endsWith(".txt")) {   
                    String contenido=this.extraerTextoTxt(file);
                    IndexarDocumento indiceArchivos = new IndexarDocumento();
                    Documento doc = new Documento();
                    doc.setIdDocumento(Integer.parseInt(file.getName().substring(0, file.getName().lastIndexOf(".")).replaceAll(" ", "").trim()));
                    indiceArchivos.setDocumento(doc);
                    indiceArchivos.setContenido(contenido);
                    indiceArchivos.indexarTexto(indice);
                }
            }catch(Exception e){
                idNoIndexados=idNoIndexados+file.getName()+"\n";
            }
        }
    }
    
    public String extraerTextoTxt(File archivo){
        String texto="";
        return texto;
    }

    public void indexarBd(String indice) throws IOException {
        String idNoIndexados = "";
        Connection cn;
        String url = "jdbc:postgresql://localhost:5433/bd_biblioteca";
        String user = "postgres";
        String password = "postgres1";
        try {
            Class.forName("org.postgresql.Driver");
            cn = DriverManager.getConnection(url, user, password);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM documento");
            while (rs.next()) {
                Documento doc = new Documento();
                try {
                        
                        IndexarDocumento indiceArchivos = new IndexarDocumento();
                       
                        doc.setIdDocumento(Integer.parseInt(rs.getString(1)));
                        String contenido = rs.getString(5);
                        doc.setContenido(contenido);
                        TipoDocumento td=new TipoDocumento();
                        td.setIdTipoDoc(1);
                        td.setNombre("Tesis");
                        doc.setIdTipoDoc(td);
                        DocumentoValorMetaDato vd=new DocumentoValorMetaDato();
                        MetaDato idMetaDato=new MetaDato();
                        idMetaDato.setIdMetaDato(1);
                        idMetaDato.setDescripcion("Título");
                        idMetaDato.setMetaDatoIr(true);
                        vd.setIdMetaDato(idMetaDato);
                        vd.setValorMetaDato(contenido.substring(0, 100));
                        List<DocumentoValorMetaDato> lista=new ArrayList<>();
                        lista.add(vd);
                        doc.setDocumentoValorMetaDatoList(lista);
                        indiceArchivos.setDocumento(doc);
                        indiceArchivos.indexarTexto(indice);
                        System.out.println("Documento:"+doc.getIdDocumento());
                } catch (Exception e) {
                    System.out.println("Error:"+e.toString());
                    idNoIndexados = idNoIndexados + rs.getString(1) + "\n";
                }  
            }
            rs.close();
            st.close();

            System.out.println("CONEXIÓN ESTABLECIDA");
        } catch (Exception e) {
            System.out.println("Error de conexión" + e.toString());
        }

    }

    public static void main(String arg[]) throws IOException, CryptographyException, InvalidPasswordException {
        String ruta="C:\\Users\\JBARCO\\Documents\\docs mateo";
        ruta="D:\\Desarrollo_maskana\\RepositorioMaskana\\";
        IndexarDirectorio dir = new IndexarDirectorio();
        IndexarDocumento index = new IndexarDocumento();
        index.crearIndice(ruta);
        //index.crearIndice("C:\\Users\\JBARCO\\Documents\\docs mateo");
        index.cerrar();
        // dir.recorrerDirectorioArchivo("/opt/documentos pdfs/","/opt/indice/");
        //System.out.println(index.getWriter("/opt/indice_espaniol/").maxDoc());
        //dir.recorrerDirectorio("/opt/documentos pdfs/", "/opt/indice_espaniol/");
        // dir.guardarTextoPdfTxt("/opt/documentos pdfs/", "/opt/mateo/tesisTxt/");
       // dir.luceneEspaniol();
       dir.indexarBd(ruta);
    }
}
    