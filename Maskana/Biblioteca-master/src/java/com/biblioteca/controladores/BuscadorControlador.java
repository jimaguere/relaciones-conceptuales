/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores;

import com.biblioteca.clases.Apriori;
import com.biblioteca.clases.ReglaDto;
import com.biblioteca.clases.ReglasAsociacion;
import com.biblioteca.clases.RelacionesServicio;
import com.biblioteca.controladores.util.JsfUtil;
import com.biblioteca.dao.DocumentoFacade;
import com.biblioteca.dao.LogConsultaFacade;
import com.biblioteca.dao.LogDescargasFacade;
import com.biblioteca.dao.MetaDatoFacade;
import com.biblioteca.dao.TipoDocumentoFacade;
import com.biblioteca.entidad.Documento;
import com.biblioteca.entidad.DocumentoValorMetaDato;
import com.biblioteca.entidad.LogConsulta;
import com.biblioteca.entidad.LogDescargas;
import com.biblioteca.entidad.MetaDato;
import com.biblioteca.entidad.TipoDocumento;
import com.biblioteca.entidad.TipoDocumentoMetaDato;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.primefaces.json.JSONException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.xml.sax.SAXException;

/**
 *
 * @author mateo
 */
@ManagedBean(name = "buscadorControlador")
@ViewScoped
//@SessionScoped
public class BuscadorControlador {

    private FSDirectory dir;
    private IndexSearcher searcher;
    private String tipoBusqueda;
    private String cadenaBusqueda;
    private String contenido;
    private List<Documento> listaDocuementos;
    private Documento documentoSeleccionado;
    private List<TipoDocumento> listaTipoDocumento;
    private TipoDocumento tipoDocumento;
    private MetaDato metaDato;
    private boolean btBusqueda;
    private boolean seleccionDocumento;
    private RepositorioControlador repositorio;
    private StreamedContent file;
    private InputStream stream;
    private LogDescargas logD;
    private LogConsulta logC;
    private UsuarioLoginControlador usuario;
    private boolean relacionados;
    private String busquedaRelacionada;
    private boolean descarga;
    private String soporte;
    @EJB
    private TipoDocumentoFacade tipoDocumentoEjbFacade;
    @EJB
    private MetaDatoFacade metaDatoEjbFacade;
    @EJB
    private DocumentoFacade documentoEjbFacade;
    @EJB
    private LogDescargasFacade logDescargaEjbFacade;
    @EJB
    private LogConsultaFacade logConsultaEjbFacade;
    private ReglasAsociacion ra;
    private String jsonRelaciones;
    private String topicos;
    private String concepto;

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getTopicos() {
        return topicos;
    }

    public void setTopicos(String topicos) {
        this.topicos = topicos;
    }

    public String getJsonRelaciones() {
        return jsonRelaciones;
    }

    public void setJsonRelaciones(String jsonRelaciones) {
        this.jsonRelaciones = jsonRelaciones;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public String getCadenaBusqueda() {
        return cadenaBusqueda;
    }

    public void setCadenaBusqueda(String cadenaBusqueda) {
        this.cadenaBusqueda = cadenaBusqueda;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public List<Documento> getListaDocuementos() {
        return listaDocuementos;
    }

    public void setListaDocuementos(List<Documento> listaDocuementos) {
        this.listaDocuementos = listaDocuementos;
    }

    public Documento getDocumentoSeleccionado() {
        return documentoSeleccionado;
    }

    public List<TipoDocumento> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<TipoDocumento> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public MetaDato getMetaDato() {
        return metaDato;
    }

    public void setMetaDato(MetaDato metaDato) {
        this.metaDato = metaDato;
    }

    public boolean isBtBusqueda() {
        return btBusqueda;
    }

    public void setBtBusqueda(boolean btBusqueda) {
        this.btBusqueda = btBusqueda;
    }

    public boolean isSeleccionDocumento() {
        return seleccionDocumento;
    }

    public void setSeleccionDocumento(boolean seleccionDocumento) {
        this.seleccionDocumento = seleccionDocumento;
    }

    public StreamedContent getFile() {
        try {
            stream = new FileInputStream(repositorio.getRutaOntologia() + this.documentoSeleccionado.getIdDocumento() + ".pdf");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        file = new DefaultStreamedContent(stream, "application/pdf", "doc.pdf");
        logDescargaEjbFacade.create(logD);
        this.descarga = true;
        return file;
    }

    public boolean isRelacionados() {
        return relacionados;
    }

    public void setRelacionados(boolean relacionados) {
        this.relacionados = relacionados;
    }

    public boolean isDescarga() {
        return descarga;
    }

    public void setDescarga(boolean descarga) {
        this.descarga = descarga;
    }

    public void actualizarTipoDocumento() {
        List<TipoDocumentoMetaDato> metaDatos = new ArrayList<TipoDocumentoMetaDato>();
        if (tipoDocumento.getIdTipoDoc().intValue() != -1) {
            tipoDocumento = tipoDocumentoEjbFacade.find(tipoDocumento.getIdTipoDoc());
            for (TipoDocumentoMetaDato tD : tipoDocumento.getTipoDocumentoMetaDatoList()) {
                if (tD.getIdMetaDato().getMetaDatoIr()) {
                    metaDatos.add(tD);
                }
            }
            tipoDocumento.setTipoDocumentoMetaDatoList(metaDatos);
        } else {
            tipoDocumento.setTipoDocumentoMetaDatoList(new ArrayList<TipoDocumentoMetaDato>());
        }
        metaDato = new MetaDato();
        metaDato.setIdMetaDato(Integer.parseInt("-1"));
    }

    /**
     * Creates a new instance of BuscadorControlador
     */
    public BuscadorControlador() throws IOException {
        this.tipoBusqueda = "1";

    }

    /**
     * Creates a new instance of UsuarioLogin
     *
     * @throws java.io.IOException
     */
    @PostConstruct
    public void iniciar() {

        this.descarga = false;
        this.btBusqueda = false;
        this.seleccionDocumento = false;
        this.repositorio = (RepositorioControlador) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("repositorioControlador");
        this.usuario = (UsuarioLoginControlador) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogin");
        if (this.repositorio == null) {
            return;
        }
        if (!repositorio.isRepositorio()) {
            return;
        }
        cadenaBusqueda = "";
        listaTipoDocumento = this.tipoDocumentoEjbFacade.findAll();
        tipoDocumento = new TipoDocumento();
        metaDato = new MetaDato();
        tipoDocumento.setTipoDocumentoMetaDatoList(new ArrayList<TipoDocumentoMetaDato>());
        metaDato.setIdMetaDato(Integer.parseInt("-1"));
        tipoDocumento.setIdTipoDoc(Integer.parseInt("-1"));
        try {
            dir = FSDirectory.open(new File(repositorio.getIndice()));
        } catch (IOException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            searcher = new IndexSearcher(IndexReader.open(dir));
        } catch (IOException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.tipoBusqueda = "1";
        this.cadenaBusqueda = "";
        this.contenido = "1";
        this.relacionados = false;
        this.busquedaRelacionada = "";
    }

    public List<String> completeGeneral(String query) {
        query = query.replaceAll("'", "");
        List<String> resul = new ArrayList<String>();
        List<Object[]> listaWords = new ArrayList<Object[]>();
        String[] listaTitulo = query.split(" ");
        String res = "";
        if (listaTitulo.length == 0) {
            return resul;
        }
        for (int i = 0; i < listaTitulo.length - 1; i++) {
            if (i == 0) {
                res = listaTitulo[i];
            } else {
                res = res + " " + listaTitulo[i];
            }
        }
        //listaWords = this.documentoEjbFacade.findAllJaroWordsComplet(listaTitulo[listaTitulo.length - 1]);
        if (listaWords.isEmpty()) {
            return resul;
        }
        for (int i = 0; i < listaTitulo.length; i++) {
            if (i > 5) {
                break;
            }
            if (res.equals("")) {
                resul.add(listaWords.get(i)[0].toString());
            } else {
                resul.add(res + " " + listaWords.get(i)[0].toString());
            }
        }
        return resul;

    }

    public String depurarContenidoTodo() {
        String busquedaContenido = "(\"" + this.cadenaBusqueda + "\")";
        return busquedaContenido;
    }

    public String depurarContenidoAlguno(String cont) {
        String[] busquedaContenido = cont.split(" ");
        String buscar = "";
        for (int i = 0; i < busquedaContenido.length; i++) {
            buscar = buscar + "+" + busquedaContenido[i].trim()
                    + " ";
        }
        return "(" + buscar.substring(0, buscar.length() - 1) + ")";
    }

    public String depurarContenidoAlgunoCompleto() {
        String[] busquedaContenido = this.cadenaBusqueda.split(" ");
        String buscar = "";
        for (int i = 0; i < busquedaContenido.length; i++) {
            buscar = buscar + "+" + busquedaContenido[i] + " ";
        }
        return buscar.substring(1, buscar.length() - 1);
    }

    public String depurarContenidoTodoCorrec() {
        String[] busquedaContenido = this.cadenaBusqueda.split(" ");
        String buscar = "";
        for (int i = 0; i < busquedaContenido.length; i++) {
            buscar = buscar + "+" + busquedaContenido[i] + "~ ";
        }
        return buscar.substring(0, buscar.length() - 1);
    }

    public String depurarContenidoAlgunoCorrec(String cad) {
        String[] busquedaContenido = cad.split(" ");
        String buscar = "";
        for (int i = 0; i < busquedaContenido.length; i++) {
            buscar = buscar + "" + busquedaContenido[i] + "~ ";
        }
        return "(" + buscar.substring(0, buscar.length() - 1) + ")";
    }

    public void generarResultados(TopDocs hits) throws IOException {
        this.logC = new LogConsulta();
        this.btBusqueda = true;
        this.seleccionDocumento = false;
        this.listaDocuementos = new ArrayList<Documento>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            Documento documento = new Documento();

            documento.setIdDocumento(Integer.parseInt(doc.get("id")));
            documento.setMetaDatosDocumentos("");
            for (IndexableField index : doc.getFields()) {
                if (!doc.get("id").equals(index.stringValue())) {
                    documento.setMetaDatosDocumentos(documento.getMetaDatosDocumentos() + "\n" + index.stringValue());
                }
            }
            this.listaDocuementos.add(documento);
        }
        this.logC = this.usuario.getLogC();
    }

    @SuppressWarnings("UnusedAssignment")
    public void buscar() throws ParseException, IOException {
        this.descarga = false;
        this.relacionados = false;
        String cont;
        this.busquedaRelacionada = this.cadenaBusqueda.trim();
        if (metaDato.getIdMetaDato().intValue() != -1) {
            metaDato = metaDatoEjbFacade.find(metaDato.getIdMetaDato());
        }

        cont = this.cadenaBusqueda.trim();
        if (this.contenido.equals("1")) {
            cont = this.depurarContenidoTodo();
        } else {
            String escapeChars = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
            String escaped = cont.replaceAll(escapeChars, " ");
            cont = escaped;
            cont = cont.trim();
            cont = this.depurarContenidoAlguno(cont);
        }
        if (metaDato.getIdMetaDato().intValue() == -1) {
            cont = "contenido:" + cont;
        } else {
            cont = metaDato.getIdMetaDato() + ":" + cont;
        }
        if (tipoDocumento.getIdTipoDoc().intValue() != -1) {
            cont = cont + " AND tipoDocumento:" + tipoDocumento.getIdTipoDoc();
        }

        QueryParser parser;
        parser = new QueryParser(Version.LUCENE_46,
                "<default field>",
                new SpanishAnalyzer(
                        Version.LUCENE_46));
        parser.setAllowLeadingWildcard(true);
        parser.setFuzzyMinSim(Float.parseFloat("2.0"));
        parser.setFuzzyPrefixLength(1);
        Query query = parser.parse(cont);
        TopDocs hits = searcher.search(query, 30);

        if (hits.totalHits == 0) {
            if (!this.contenido.equals("1")) {
                cont = this.cadenaBusqueda.trim();
                String escapeChars = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
                String escaped = cont.replaceAll(escapeChars, " ");
                cont = escaped;
                cont = cont.trim();
                cont = this.depurarContenidoAlgunoCorrec(cont);
                if (metaDato.getIdMetaDato().intValue() == -1) {
                    cont = "contenido:" + cont;
                } else {
                    cont = metaDato.getIdMetaDato() + ":" + cont;
                }
                if (tipoDocumento.getIdTipoDoc().intValue() != -1) {
                    cont = cont + " AND tipoDocumento:" + tipoDocumento.getIdTipoDoc();
                }
                query = parser.parse(cont);
                hits = searcher.search(query, 30);
            }
        }
        this.generarResultados(hits);
    }

    public void asignarDocumento(Documento doc) throws FileNotFoundException {
        this.documentoSeleccionado = this.documentoEjbFacade.find(doc.getIdDocumento());
        this.seleccionDocumento = true;
        this.relacionados = false;
        this.descarga = false;
        stream = new FileInputStream(repositorio.getRutaOntologia() + this.documentoSeleccionado.getIdDocumento() + ".pdf");
        file = new DefaultStreamedContent(stream, "application/pdf", "doc.pdf");
        this.logD = new LogDescargas();
        logD.setIdDocumento(this.documentoSeleccionado);
        logD.setUsuario(this.usuario.getUsuarioSession());
        logD.setFechaDescarga(new Date());
        logD.setIdConsulta(logC);
    }

    public StreamedContent downloadPDF() throws IOException {
        return new DefaultStreamedContent(stream, "application/pdf", "documento.pdf");
    }

    public void regresar() {
        this.seleccionDocumento = false;
        this.relacionados = false;
        this.descarga = false;
    }

    public void relacionarDocumentos() throws IOException, ParseException {
        this.relacionados = true;
        QueryParser parser;
        parser = new QueryParser(Version.LUCENE_46,
                "contenido",
                new SpanishAnalyzer(
                        Version.LUCENE_46));
        parser.setAllowLeadingWildcard(true);
        parser.setFuzzyMinSim(Float.parseFloat("2.0"));

        String cont = "(" + QueryParser.escape(this.documentoSeleccionado.getMetaDatosValor()) + ")+(" + this.busquedaRelacionada + ")";
        Query query = parser.parse(cont);

        TopDocs hits = searcher.search(query, 10);
        this.documentoSeleccionado.setDocumentosRelacionados(new ArrayList<Documento>());

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            Documento documento = new Documento();

            documento.setIdDocumento(Integer.parseInt(doc.get("id")));
            if (documento.getIdDocumento().intValue() == this.documentoSeleccionado.getIdDocumento().intValue()) {
                continue;
            }
            documento.setMetaDatosDocumentos("");
            for (IndexableField index : doc.getFields()) {
                if (!doc.get("id").equals(index.stringValue())) {
                    documento.setMetaDatosDocumentos(documento.getMetaDatosDocumentos() + "\n" + index.stringValue());
                }
            }
            this.documentoSeleccionado.getDocumentosRelacionados().add(documento);
        }
    }

    public void relacionarDocumentos_doc2vec() throws IOException, ParseException {
        this.relacionados = true;

        String cont = "(" + QueryParser.escape(this.documentoSeleccionado.getMetaDatosValor()) + ")+(" + this.busquedaRelacionada + ")";

        List<Object[]> listaDocumentos = this.documentoEjbFacade.findDocSimilar(this.documentoSeleccionado.getIdDocumento());
        this.documentoSeleccionado.setDocumentosRelacionados(new ArrayList<Documento>());

        for (Object[] obj : listaDocumentos) {
            try {
                Documento documento = new Documento();
                documento = this.documentoEjbFacade.finById(Integer.parseInt(obj[0].toString()));
                documento.setMetaDatosDocumentos("");
                for (DocumentoValorMetaDato dv : documento.getDocumentoValorMetaDatoList()) {
                    if (dv.getValorMetaDato() != null) {
                        documento.setMetaDatosDocumentos(documento.getMetaDatosDocumentos() + "\n" + dv.getValorMetaDato());
                    }

                }

                this.documentoSeleccionado.getDocumentosRelacionados().add(documento);
            } catch (Exception e) {
            }
        }
    }

    public String getSoporte() {
        return soporte;
    }

    public void setSoporte(String soporte) {
        this.soporte = soporte;
    }

    public void modalSoporte() {
        this.soporte = "";
    }

    public ReglasAsociacion getRa() {
        return ra;
    }

    public void setRa(ReglasAsociacion ra) {
        this.ra = ra;
    }

    public void apriori() throws Exception {
        List<Object[]> descargas = logDescargaEjbFacade.transaccionesDescargas();
        int items = logDescargaEjbFacade.maxIntems();
        Apriori ap = new Apriori(descargas, Double.parseDouble(soporte), items);
        ra = new ReglasAsociacion(ap, Double.parseDouble("0.7"));
        ra.generarReglas();
        if (!ra.getReglasFuertes().isEmpty()) {
            generarConclusionRegla();
        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("usuarioValNoRule"));
        }
    }

    public void aprioriDocumento() throws Exception {
        List<Object[]> descargas = logDescargaEjbFacade.transaccionesDescargas();
        int items = logDescargaEjbFacade.maxIntems();
        Apriori ap = new Apriori(descargas, Double.parseDouble("2"), items);
        ra = new ReglasAsociacion(ap, Double.parseDouble("0.7"));
        ra.generarReglas();
        if (!ra.getReglasFuertes().isEmpty()) {
            generarConclusionReglaDocumento();
        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("usuarioValNoRule"));
        }
    }

    public void generarConclusionReglaDocumento() throws JRException {
        if (ra.getAntecendente().containsKey(this.documentoSeleccionado.getIdDocumento().toString().trim())) {
            String[] l = ra.getAntecendente().get(this.documentoSeleccionado.getIdDocumento().toString().trim());
            String conclusion = ResourceBundle.getBundle("/Bundle").getString("usarioAptioRegLos");
            for (int i = 0; i < l.length; i++) {
                String idDoc = l[i].replaceAll("\\[", "").replaceAll("]", "").trim();
                Integer idD = Integer.parseInt(idDoc);
                Documento doc = documentoEjbFacade.finById(idD);

                if (i < l.length - 1) {
                    conclusion = conclusion + " " + doc.getMetaDatosValor() + " " + ResourceBundle.getBundle("/Bundle").getString("usuariosDocumentosAprioAnd") + "";
                } else {
                    conclusion = conclusion + " " + doc.getMetaDatosValor();
                }
            }
            Documento doc = documentoSeleccionado;
            conclusion = conclusion + ResourceBundle.getBundle("/Bundle").getString("usuariosAprioTam") + " " + doc.getMetaDatosValor().replaceAll("\n", "") + ".\n";
            generarReporteDos(conclusion);
        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("usuarioValNoRule"));
        }

    }

    public void generarConclusionRegla() throws JRException {
        for (int j = 0; j < ra.getReglasFuertes().size(); j++) {
            ReglaDto reglaFuerte = ra.getReglasFuertes().get(j);
            // String format=String.format("%.2g%n", (reglaFuerte.getSoporte() * 100)).replaceAll("\n", "");
            String format = "" + (reglaFuerte.getSoporte() * 100);
            String conclusion = ResourceBundle.getBundle("/Bundle").getString("elAprio") + " " + format + "% "
                    + ResourceBundle.getBundle("/Bundle").getString("usuariosAprio");
            String[] itemL = reglaFuerte.getL().replaceAll("\\[", "").replaceAll("]", "").split(",");
            for (int i = 0; i < itemL.length; i++) {
                String idDoc = itemL[i].replaceAll("\\[", "").replaceAll("]", "").trim();
                Integer idD = Integer.parseInt(idDoc);
                Documento doc = documentoEjbFacade.finById(idD);

                if (i < itemL.length - 1) {
                    conclusion = conclusion + " " + doc.getMetaDatosValor() + " " + ResourceBundle.getBundle("/Bundle").getString("usuariosDocumentosAprioAnd") + "";
                } else {
                    conclusion = conclusion + " " + doc.getMetaDatosValor();
                }
            }
            Documento doc = documentoEjbFacade.finById(Integer.parseInt(reglaFuerte.getA().trim()));
            //String format1=String.format("%.2g%n", (reglaFuerte.getSoporteRegla() / ra.getAp().getNumTransactions() * 100)).replaceAll("\n", "");
            String format1 = "" + (reglaFuerte.getSoporteRegla() / ra.getAp().getNumTransactions() * 100);
            conclusion = conclusion + ResourceBundle.getBundle("/Bundle").getString("usuariosAprioTam") + " " + doc.getMetaDatosValor().replaceAll("\n", "") + ".\n" + ResourceBundle.getBundle("/Bundle").getString("elAprio") + " " + format1 + "% "
                    + ResourceBundle.getBundle("/Bundle").getString("usuariosAprioDes") + " " + (reglaFuerte.getL().split(",").length + 1) + " " + ResourceBundle.getBundle("/Bundle").getString("usuariosAprioDicNum");
            ra.getReglasFuertes().get(j).setConclusion(conclusion);
        }
        generarReporte();
    }

    public void generarReporte() throws JRException {
        ServletContext servContx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String rutaJasper = (String) servContx.getRealPath("/");//ruta raiz proyecto
        rutaJasper = rutaJasper + "resources/jasper/reglasA.jasper";
        String rutaTit = (String) servContx.getRealPath("/") + "resources/img/biblioteca.png";
        String rutaPie = (String) servContx.getRealPath("/") + "resources/img/piep.png";
        Map parameters = new HashMap();
        parameters.put("titulo", rutaTit);
        parameters.put("pie", rutaPie);
        JRBeanCollectionDataSource lista = new JRBeanCollectionDataSource(ra.getReglasFuertes());
        File fichero = new File(rutaJasper);
        JasperReport jasperReport = (JasperReport) JRLoader
                .loadObject(fichero);

        JasperPrint print = JasperFillManager.fillReport(jasperReport,
                parameters, lista);

        byte[] bytes = JasperExportManager.exportReportToPdf(print);
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context
                .getExternalContext().getResponse();

        response.addHeader("Content-disposition",
                "attachment;filename=reporte.pdf");
        response.setContentLength(bytes.length);
        try {
            response.getOutputStream().write(bytes);
            response.setContentType("application/pdf");
            context.responseComplete();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e.toString());
        }
    }

    private void generarReporteDos(String conclusion) throws JRException {
        this.soporte = "2";
        ServletContext servContx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String rutaJasper = (String) servContx.getRealPath("/");//ruta raiz proyecto
        rutaJasper = rutaJasper + "resources/jasper/reglasA.jasper";
        String rutaTit = (String) servContx.getRealPath("/") + "resources/img/biblioteca.png";
        String rutaPie = (String) servContx.getRealPath("/") + "resources/img/piep.png";
        Map parameters = new HashMap();
        parameters.put("titulo", rutaTit);
        parameters.put("pie", rutaPie);
        ra.setReglasFuertes(new ArrayList<ReglaDto>());
        ra.getReglasFuertes().add(new ReglaDto("", "", conclusion, Double.parseDouble(soporte), Double.parseDouble(soporte)));
        JRBeanCollectionDataSource lista = new JRBeanCollectionDataSource(ra.getReglasFuertes());
        File fichero = new File(rutaJasper);
        JasperReport jasperReport = (JasperReport) JRLoader
                .loadObject(fichero);

        JasperPrint print = JasperFillManager.fillReport(jasperReport,
                parameters, lista);

        byte[] bytes = JasperExportManager.exportReportToPdf(print);
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context
                .getExternalContext().getResponse();

        response.addHeader("Content-disposition",
                "attachment;filename=reporte.pdf");
        response.setContentLength(bytes.length);
        try {
            response.getOutputStream().write(bytes);
            response.setContentType("application/pdf");
            context.responseComplete();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e.toString());
        }
    }

    public void word2Vec(String idDocumento) {
        try {
            this.jsonRelaciones = "";
            RelacionesServicio s = new RelacionesServicio();
            this.jsonRelaciones = s.send(idDocumento.toString());
            /* this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"source\":", "source:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"target\":", "target:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"score\":", "score:");*/
        } catch (IOException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void word2VecFilterS() {
        try {
            this.jsonRelaciones = "";
            System.out.println("concepto:" + this.concepto);
            RelacionesServicio s = new RelacionesServicio();
            this.jsonRelaciones = s.sendConcep(this.documentoSeleccionado.getIdDocumento().toString(), "Suave");
            /* this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"source\":", "source:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"target\":", "target:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"score\":", "score:");*/
        } catch (IOException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void word2VecFilterM() {
        try {
            this.jsonRelaciones = "";
            System.out.println("concepto:" + this.concepto);
            RelacionesServicio s = new RelacionesServicio();
            this.jsonRelaciones = s.sendConcep(this.documentoSeleccionado.getIdDocumento().toString(),"Medio");
            /* this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"source\":", "source:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"target\":", "target:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"score\":", "score:");*/
        } catch (IOException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void word2VecFilterF() {
        try {
            this.jsonRelaciones = "";
            System.out.println("concepto:" + this.concepto);
            RelacionesServicio s = new RelacionesServicio();
            this.jsonRelaciones = s.sendConcep(this.documentoSeleccionado.getIdDocumento().toString(), "Fuerte");
            /* this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"source\":", "source:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"target\":", "target:");
            this.jsonRelaciones=this.jsonRelaciones.replaceAll("\"score\":", "score:");*/
        } catch (IOException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(BuscadorControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
