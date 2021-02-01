package com.biblioteca.controladores;

import com.biblioteca.controladores.util.JsfUtil;
import com.biblioteca.controladores.util.PaginationHelper;
import com.biblioteca.dao.TipoDocumentoFacade;
import com.biblioteca.entidad.MetaDato;
import com.biblioteca.entidad.TipoDocumento;
import com.biblioteca.entidad.TipoDocumentoMetaDato;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "tipoDocumentoController")
@ViewScoped
public class TipoDocumentoController implements Serializable {

    private TipoDocumento current;
    private DataModel items = null;
    @EJB
    private com.biblioteca.dao.TipoDocumentoFacade ejbFacade;
    @EJB
    private com.biblioteca.dao.MetaDatoFacade ejbMetaDatoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean bnuevo;
    private boolean listar;
    private DualListModel<MetaDato> metaDatos; 
   

    public TipoDocumentoController() {
    }

    public TipoDocumento getSelected() {
        if (current == null) {
            current = new TipoDocumento();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TipoDocumentoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findAll());
                }
            };
        }
        return pagination;
    }

    public boolean isListar() {
        return listar;
    }

    public void setbListar(boolean listar) {
        this.listar = listar;
    }

    public boolean isBnuevo() {
        return bnuevo;
    }

    public void setBnuevo(boolean bnuevo) {
        this.bnuevo = bnuevo;
    }

    public DualListModel<MetaDato> getMetaDatos() {
        return metaDatos;
    }

    public void setMetaDatos(DualListModel<MetaDato> metaDatos) {
        this.metaDatos = metaDatos;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (TipoDocumento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public void prepareCreate() {
        current = new TipoDocumento();
        selectedItemIndex = -1;
        bnuevo=true;
        listar=false;
    }

    public void create() {
        try {
            prepararMetaDatos();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TipoDocumentoCreated"));
            iniciar();
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        
        }
    }
    
    public void prepararMetaDatos(){
        List<TipoDocumentoMetaDato>metaDatosCurrent=new ArrayList<TipoDocumentoMetaDato>();
        for(MetaDato metaDato:metaDatos.getTarget()){
            TipoDocumentoMetaDato tipoDocMeta=new TipoDocumentoMetaDato();
            tipoDocMeta.setIdTipoDoc(current);
            tipoDocMeta.setIdMetaDato(metaDato);
            metaDatosCurrent.add(tipoDocMeta);
        }
        current.setTipoDocumentoMetaDatoList(metaDatosCurrent);
    }
    
    public void prepareDestroy(){
        current = (TipoDocumento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        System.out.println("ok");
    }

    public void prepareEdit() {
        bnuevo=false;
        listar=false;
        current = (TipoDocumento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        cargarMetaDatosEditr();
    }

    public void update() {
        try {
            prepararMetaDatos();
            getFacade().editar(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TipoDocumentoUpdated"));
            iniciar();
            prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        
        }
    }

    public String destroy() {
        performDestroy();
        recreatePagination();
        recreateModel();
        System.out.println("ok");
        return "List";
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TipoDocumentoDeleted"));
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

    public void cargarMetaDatosIniciales(){
        List<MetaDato> listaSource=ejbMetaDatoFacade.findAll();
        List<MetaDato> listaTarget=new ArrayList<MetaDato>();
        this.metaDatos=new DualListModel<MetaDato>(listaSource, listaTarget);
    }
    
    public void cargarMetaDatosEditr(){
        List<MetaDato> listaSource=ejbMetaDatoFacade.findAll();
        List<MetaDato> listaTarget=new ArrayList<MetaDato>();
        for(TipoDocumentoMetaDato tipoDocMeta:current.getTipoDocumentoMetaDatoList()){
            listaTarget.add(tipoDocMeta.getIdMetaDato());
        }
        listaSource.removeAll(listaTarget);
        this.metaDatos=new DualListModel<MetaDato>(listaSource, listaTarget);
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }
    
    @PostConstruct
    public void iniciar(){
        listar=true;
        bnuevo=false;
        cargarMetaDatosIniciales();
    }

    @FacesConverter(forClass = TipoDocumento.class)
    public static class TipoDocumentoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TipoDocumentoController controller = (TipoDocumentoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tipoDocumentoController");
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
            if (object instanceof TipoDocumento) {
                TipoDocumento o = (TipoDocumento) object;
                return getStringKey(o.getIdTipoDoc());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TipoDocumento.class.getName());
            }
        }
    }
}
