package com.biblioteca.controladores;

import com.biblioteca.clases.ExtracPdfText;
import com.biblioteca.clases.IndexDemond;
import com.biblioteca.clases.IndexarDocumento;
import com.biblioteca.controladores.util.JsfUtil;
import com.biblioteca.controladores.util.PaginationHelper;
import com.biblioteca.dao.DocumentoFacade;
import com.biblioteca.entidad.Documento;
import com.biblioteca.entidad.DocumentoValorMetaDato;
import com.biblioteca.entidad.TipoDocumento;
import com.biblioteca.entidad.TipoDocumentoMetaDato;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "documentoController")
@ViewScoped
public class DocumentoController implements Serializable {

    private Documento current;
    private DataModel items = null;
    private String rutaPdf;
    private static final int DEFAULT_BUFFER_SIZE = 10240;
    @EJB
    private com.biblioteca.dao.DocumentoFacade ejbFacade;
    @EJB
    private com.biblioteca.dao.TipoDocumentoFacade ejbTipoDocumentoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean listar;
    private boolean nuevo;
    private boolean skip;
    private List<TipoDocumento> tiposDocumentos;
    private UploadedFile archivoDocumento;
    private int constanteTipoDatoCadena;
    RepositorioControlador repositorio;


    public DocumentoController() {
    }

    public Documento getSelected() {
        if (current == null) {
            current = new Documento();
            selectedItemIndex = -1;
        }
        return current;
    }

    private DocumentoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(5) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public boolean isListar() {
        return listar;
    }

    public void setListar(boolean listar) {
        this.listar = listar;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public List<TipoDocumento> getTiposDocumentos() {
        return tiposDocumentos;
    }

    public void setTiposDocumentos(List<TipoDocumento> tiposDocumentos) {
        this.tiposDocumentos = tiposDocumentos;
    }

    public UploadedFile getArchivoDocumento() {
        return archivoDocumento;
    }

    public void setArchivoDocumento(UploadedFile archivoDocumento) {
        this.archivoDocumento = archivoDocumento;
    }

    public int getConstanteTipoDatoCadena() {
        return constanteTipoDatoCadena;
    }

    public void setConstanteTipoDatoCadena(int constanteTipoDatoCadena) {
        this.constanteTipoDatoCadena = constanteTipoDatoCadena;
    }

    public String prepareList() {
        this.listar = true;
        this.nuevo = false;
        this.archivoDocumento = null;
        recreateModel();
        return "List";
    }

    public double getTamanioArchivo() {
        double tam = Double.parseDouble("" + this.archivoDocumento.getSize());
        double base = 1048576;
        double div = tam / base;
        return redondear(div);
    }

    public double redondear(double numero) {
        double num = numero;
        double aux = Math.rint(num * 10000) / 10000;
        return aux;
    }

    public void prepareCreate() {
        current = new Documento();
        current.setDocumentoValorMetaDatoList(new ArrayList<DocumentoValorMetaDato>());
        current.setIdTipoDoc(new TipoDocumento());
        selectedItemIndex = -1;
        this.nuevo = true;
        this.listar = false;
    }

    public String create() {
        try {
            current.setFechaCreacion(new Date());
            getFacade().create(current);
            String pathArchivo = rutaPdf + current.getIdDocumento() + ".pdf";
            if (guardarArchivo(pathArchivo)) {
                if (ResourceBundle.getBundle("/Bundle").getString("constanteEstadoDocumentoIndexado").equals("" + current.getEstado())) {
                    IndexarDocumento index = new IndexarDocumento();
                    index.setDocumento(current);
                    index.indexarTexto(repositorio.getIndice());
                }else{
                    IndexarDocumento index = new IndexarDocumento();
                    index.setDocumento(current);
                    index.indexMetaData(repositorio.getIndice());
                    IndexDemond demonio=new IndexDemond();
                    demonio.setFileJar(rutaPdf);
                    demonio.levantarDemonio
                            ("jdbc:postgresql://localhost:5432/bd_biblioteca", 
                            "1234", rutaPdf, rutaPdf);
                    
                }
            } else {
                getFacade().remove(current);
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DocumentoCreated"));

            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void prepareEdit() {
        current = (Documento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        this.nuevo = false;
        this.listar = false;
    }
    
    public void prepareEdit(Documento doc){
        this.current=this.ejbFacade.find(doc.getIdDocumento());
        this.nuevo = false;
        this.listar = false; 
    }

    public void prepareDestroy() {
        current = (Documento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
    }
    
    public void prepareDestroy(Documento doc){
        this.current=this.ejbFacade.find(doc.getIdDocumento());
    }

    public String update() {
        try {
            getFacade().editarDocumento(current);
            if (this.archivoDocumento != null) {
                if (!guardarArchivo(rutaPdf + current.getIdDocumento() + ".pdf")) {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                    return null;
                }
            }

            IndexarDocumento index = new IndexarDocumento();
            index.setDocumento(current);
            index.modificarIndice(repositorio.getIndice());

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DocumentoUpdated"));

            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void destroy() throws IOException {
        if (eliminarArchivo(rutaPdf + current.getIdDocumento() + ".pdf")) {
            performDestroy();
            IndexarDocumento index = new IndexarDocumento();
            index.setDocumento(current);
            index.eliminarDocumento(repositorio.getIndice());
            recreatePagination();
            recreateModel();
            prepareList();

        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DocumentoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }
    

    @PostConstruct
    public void iniciar() {
        repositorio = (RepositorioControlador) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("repositorioControlador");
        this.rutaPdf = repositorio.getRutaOntologia();
        this.listar = true;
        this.nuevo = false;
        this.tiposDocumentos = ejbTipoDocumentoFacade.findAll();
        this.constanteTipoDatoCadena = Integer.parseInt(ResourceBundle.getBundle("/Bundle").getString("constanteTipoDatoCadena"));
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;
            return "confirm";
        } else {
            if (event.getOldStep().equals("tipo_documento")
                    && event.getNewStep().equals("meta_dato") && nuevo) {
                if (this.archivoDocumento == null) {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("requiredArchivoVal"));
                    return event.getOldStep();
                }
                current.setDocumentoValorMetaDatoList(new ArrayList<DocumentoValorMetaDato>());
                current.setIdTipoDoc(ejbTipoDocumentoFacade.find(current.getIdTipoDoc().getIdTipoDoc()));
                for (TipoDocumentoMetaDato metaDato : current.getIdTipoDoc().getTipoDocumentoMetaDatoList()) {
                    DocumentoValorMetaDato valorMetaDato = new DocumentoValorMetaDato();
                    valorMetaDato.setIdDocumento(current);
                    valorMetaDato.setIdMetaDato(metaDato.getIdMetaDato());
                    valorMetaDato.setValorMetaDato("");
                    current.getDocumentoValorMetaDatoList().add(valorMetaDato);
                }
            }
            if (event.getOldStep().equals("meta_dato")
                    && event.getNewStep().equals("confirm")) {
                for (DocumentoValorMetaDato valorMetaDato : current.getDocumentoValorMetaDatoList()) {
                    if (valorMetaDato.getIdMetaDato().getIdTipoDato().getIdTipoDato().intValue()
                            == this.constanteTipoDatoCadena) {
                        if (valorMetaDato.getValorMetaDato().trim().length() == 0) {
                            JsfUtil.addErrorMessage(
                                    valorMetaDato.getIdMetaDato().getDescripcion() + " " + ResourceBundle.getBundle("/Bundle").getString("requiredCampoVal"));
                            return event.getOldStep();
                        }
                    } else {
                        if (valorMetaDato.getFechaMetaDato() == null) {
                            JsfUtil.addErrorMessage(
                                    valorMetaDato.getIdMetaDato().getDescripcion() + " " + ResourceBundle.getBundle("/Bundle").getString("requiredCampoVal"));
                            return event.getOldStep();
                        }
                    }
                }

            }
            return event.getNewStep();
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        try {
            this.archivoDocumento = event.getFile();
            ExtracPdfText texto = new ExtracPdfText(archivoDocumento.getInputstream());
            this.current.setContenido(texto.extraerTexto());
            current.setEstado(Short.parseShort(ResourceBundle.getBundle("/Bundle").getString("constanteEstadoDocumentoIndexado")));
            texto.cerrar();
            JsfUtil.addSuccessMessage(archivoDocumento.getFileName() + " " + ResourceBundle.getBundle("/Bundle").getString("archivoCargadoExitosamente"));
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getCause());
            this.archivoDocumento = null;
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("noSepuedeCargarArchivo"));
        }
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public boolean eliminarArchivo(String ruta) {
        File archivo = new File(ruta);
        return archivo.delete();
    }

    public boolean guardarArchivo(String ruta) {
        try {
            if (this.archivoDocumento == null) {
                return true;
            }
            OutputStream documento = new FileOutputStream(new File(ruta));
            InputStream archivoIn = this.archivoDocumento.getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = archivoIn.read(bytes)) != -1) {
                documento.write(bytes, 0, read);
            }
            archivoIn.close();
            documento.flush();
            documento.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String confirmar() {
        try {
            current.setFechaCreacion(new Date());
            current.setEstado(Short.parseShort(ResourceBundle.getBundle("/Bundle").getString("constanteEstadoDocumentoCreado")));
            current.setFechaCreacion(new Date());
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("DocumentoCreated"));

        } catch (Exception e) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("ErrorInesperado"));
        }
        return prepareList();
    }

    public void downloadPDF(String ruta) throws IOException { // Prepare. 
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        File file = new File(rutaPdf, ruta);
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        try {
            // Open file. 
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            // Init servlet response. response.reset();
            response.setHeader("Content-Type", "application/pdf");
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "inline; filename=\"" + ruta + "\"");
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
            // Write file contents to response. 
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Finalize task. output.flush();
        } finally { // Gently close streams. close(output); close(input); }
            // Inform JSF that it doesn't need to handle response.
            // This is very important, otherwise you will get the following exception in the logs: 
            // java.lang.IllegalStateException: Cannot forward after response has been committed. 
            facesContext.responseComplete();
        } // Helpers (can be refactored to public utility class)

    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it. It may be useful to
                // know that this will generally only be thrown when the client aborted the download. 
                e.printStackTrace();
            }
        }
    }

    @FacesConverter(forClass = Documento.class)
    public static class DocumentoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DocumentoController controller = (DocumentoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "documentoController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Documento) {
                Documento o = (Documento) object;
                return getStringKey(o.getIdDocumento());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Documento.class.getName());
            }
        }
    }
}
