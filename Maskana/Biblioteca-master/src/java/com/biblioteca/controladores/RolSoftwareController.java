package com.biblioteca.controladores;

import com.biblioteca.controladores.util.JsfUtil;
import com.biblioteca.dao.RolSoftwareFacade;
import com.biblioteca.entidad.Menu;
import com.biblioteca.entidad.RolSoftMenu;
import com.biblioteca.entidad.RolSoftware;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "rolSoftwareController")
@ViewScoped
public class RolSoftwareController implements Serializable {

    private RolSoftware current;
    private List<RolSoftware> items = null;
    private boolean listar;
    private boolean editar;
    private TreeNode root3;
    private TreeNode[] selectedNodes2;
    @EJB
    private com.biblioteca.dao.RolSoftwareFacade ejbFacade;
    @EJB
    private com.biblioteca.dao.MenuFacade ejbMenuFacade;

    public RolSoftware getCurrent() {
        return current;
    }

    public void setCurrent(RolSoftware current) {
        this.current = current;
    }

    public List<RolSoftware> getItems() {
        return items;
    }

    public void setItems(List<RolSoftware> items) {
        this.items = items;
    }

    public boolean isListar() {
        return listar;
    }

    public void setListar(boolean listar) {
        this.listar = listar;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public TreeNode getRoot3() {
        return root3;
    }

    public void setRoot3(TreeNode root3) {
        this.root3 = root3;
    }

    public RolSoftwareController() {
    }

    public TreeNode[] getSelectedNodes2() {
        return selectedNodes2;
    }

    public void setSelectedNodes2(TreeNode[] selectedNodes2) {
        this.selectedNodes2 = selectedNodes2;
    }

    @PostConstruct
    public void iniciar() {
        items = getFacade().findAll();
        this.listar = true;

    }

    private RolSoftwareFacade getFacade() {
        return ejbFacade;
    }

    public void prepareCreate() {
        current = new RolSoftware();
        this.listar = false;
        this.editar = false;
        List<Menu> menus = this.ejbMenuFacade.findAll();
        root3 = new CheckboxTreeNode(new Menu(Integer.SIZE, "", ""), null);
        TreeNode root[] = new TreeNode[menus.size()];
        int i = 0;
        for (Menu menu : menus) {
            if (menu.getPadreIdMenu() == null) {
                root[i] = new CheckboxTreeNode(menu, root3);
                int j = i + 1;
                for (Menu menuHijo : menu.getMenuList()) {
                    root[j] = new CheckboxTreeNode(menuHijo, root[i]);
                    j++;
                }
                i++;
            }
        }
    }

    public void create() {
        try {
            current.setRolSoftMenuList(new ArrayList<RolSoftMenu>());
            asociarRolMenu();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolSoftwareCreated"));
            iniciar();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void prepareEdit(RolSoftware rolSoftware) {
        current = rolSoftware;
        this.listar = false;
        this.editar = true;
        List<Menu> menus = this.ejbMenuFacade.findAll();
        root3 = new CheckboxTreeNode(new Menu(Integer.SIZE, "", ""), null);
        TreeNode root[] = new TreeNode[menus.size()];
        int i = 0;
        for (Menu menu : menus) {
            if (menu.getPadreIdMenu() == null) {
                root[i] = new CheckboxTreeNode(menu, root3);
                int j = i + 1;
                for (Menu menuHijo : menu.getMenuList()) {
                    root[j] = new CheckboxTreeNode(menuHijo, root[i]);
                    for (RolSoftMenu rolMenu : current.getRolSoftMenuList()) {
                        if (rolMenu.getIdMenu().getIdMenu().intValue() == menuHijo.getIdMenu().intValue()) {
                            root[j].setSelected(true);
                        }
                    }
                    j++;
                }
                i++;
            }
        }
    }

    public void asociarRolMenu() {
        Set<Menu> menus=new HashSet <Menu>();
        current.setRolSoftMenuList(new ArrayList<RolSoftMenu>());
        for (TreeNode node : selectedNodes2) {
            Menu menu = (Menu) node.getData();
            menus.add(menu);
            if(menu.getPadreIdMenu()!=null){
                menus.add(menu.getPadreIdMenu());        
            }    
        }
        List<Menu> listaMenu=new ArrayList<Menu>(menus);
        for(Menu menu:listaMenu){
                RolSoftMenu rolSoftMenuPadre = new RolSoftMenu();
                rolSoftMenuPadre.setIdMenu(menu);
                rolSoftMenuPadre.setIdRol(current);
                current.getRolSoftMenuList().add(rolSoftMenuPadre);
        }        
    }

    public void update() {
        try {
            asociarRolMenu();
            getFacade().editar(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolSoftwareUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }
    
    public void prepareDestroy(RolSoftware rol){
        current=rol;
    }

    public void destroy() {
        performDestroy();
        iniciar();
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolSoftwareDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = RolSoftware.class)
    public static class RolSoftwareControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RolSoftwareController controller = (RolSoftwareController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rolSoftwareController");
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
            if (object instanceof RolSoftware) {
                RolSoftware o = (RolSoftware) object;
                return getStringKey(o.getIdRol());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RolSoftware.class.getName());
            }
        }
    }
}
