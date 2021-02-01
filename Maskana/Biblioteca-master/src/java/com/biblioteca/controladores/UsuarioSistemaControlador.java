/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores;

import com.biblioteca.controladores.util.JsfUtil;
import com.biblioteca.dao.RolSoftwareFacade;
import com.biblioteca.dao.UsuarioFacade;
import com.biblioteca.entidad.RolSoftware;
import com.biblioteca.entidad.Usuario;
import com.biblioteca.entidad.UsuarioRolSoftware;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author and
 */
@ManagedBean
@ViewScoped
public class UsuarioSistemaControlador {

    /**
     * Creates a new instance of UsuarioSistemaControlador
     */
    private List<Usuario> usuariosSistema;
    private Usuario usuario;
    private boolean listar;
    private boolean editar;
    private String confirmarClave;
    private DualListModel<RolSoftware> roles;
    @EJB
    private UsuarioFacade usuarioEjb;
    @EJB
    private RolSoftwareFacade rolSoftwareEjb;
    private boolean skip;

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isSkip() {
        return skip;
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

    public List<Usuario> getUsuariosSistema() {
        return usuariosSistema;
    }

    public void setUsuariosSistema(List<Usuario> usuariosSistema) {
        this.usuariosSistema = usuariosSistema;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public String getConfirmarClave() {
        return confirmarClave;
    }

    public void setConfirmarClave(String confirmarClave) {
        this.confirmarClave = confirmarClave;
    }

    public DualListModel<RolSoftware> getRoles() {
        return roles;
    }

    public void setRoles(DualListModel<RolSoftware> roles) {
        this.roles = roles;
    }

    @PostConstruct
    public void iniciar() {
        this.usuario = new Usuario();
        this.usuariosSistema = this.usuarioEjb.findUsuariosSistema();
        this.listar = true;
        this.roles = new DualListModel<RolSoftware>(new ArrayList<RolSoftware>(), new ArrayList<RolSoftware>());
    }

    public void prepareEdit(Usuario usuario) {
        this.listar = false;
        this.editar = true;
        this.usuario = usuario;
        prepararRolUsuario();

    }

    public void prepareCreate() {
        this.listar = false;
        this.editar = false;
        this.usuario = new Usuario();
        this.usuario.setEstado(Integer.parseInt(ResourceBundle.getBundle("/Bundle").getString("constanteUsuarioEstadoActivo")));
        this.usuario.setFechaCreacion(new Date());
        roles.setSource(rolSoftwareEjb.findAll());
    }

    public void prepareDestroy(Usuario usuario) {
        this.usuario = usuario;
    }

    public UsuarioSistemaControlador() {
    }

    public void create() {
        try {
            usuario.setClave(md5(confirmarClave));
            usuario.setUsuario(usuario.getCorreoElectronico());
            asociarUsuarioRol();
            usuarioEjb.create(usuario);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("mensajeUsuarioReg"));
            iniciar();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }

    }

    public void update() {
        try {
            this.usuario.setClave(md5(confirmarClave));
            asociarUsuarioRol();
            usuarioEjb.editar(this.usuario);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("mensajeUsuarioActualizado"));
            iniciar();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void destroy() {
        try {
            usuarioEjb.remove(usuario);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("mensajeUsuarioDes"));
            iniciar();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void asociarUsuarioRol() {
        List<UsuarioRolSoftware> listaUsuariosRol = new ArrayList<UsuarioRolSoftware>();
        List<RolSoftware> rolesSoft = roles.getTarget();
        for (RolSoftware rol : rolesSoft) {
            UsuarioRolSoftware usuarioRol = new UsuarioRolSoftware();
            usuarioRol.setIdRol(rol);
            usuarioRol.setUsuario(this.usuario);
            listaUsuariosRol.add(usuarioRol);
        }
        this.usuario.setUsuarioRolSoftwareList(listaUsuariosRol);
    }

    public void prepararRolUsuario() {
        List<RolSoftware> listaSource = rolSoftwareEjb.findAll();
        List<RolSoftware> listaTarget = new ArrayList<RolSoftware>();
        for (UsuarioRolSoftware rolSoftware : usuario.getUsuarioRolSoftwareList()) {
            listaTarget.add(rolSoftware.getIdRol());
        }
        listaSource.removeAll(listaTarget);
        this.roles = new DualListModel<RolSoftware>(listaSource, listaTarget);
    }

    private static String md5(String clear) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(clear.getBytes());
        int size = b.length;
        StringBuffer h = new StringBuffer(size);
        for (int i = 0; i < size; i++) {
            int u = b[i] & 255;
            if (u < 16) {
                h.append("0" + Integer.toHexString(u));
            } else {
                h.append(Integer.toHexString(u));
            }
        }

        return h.toString();
    }
}
