/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores;

import com.biblioteca.dao.LogConsultaFacade;
import com.biblioteca.dao.UsuarioFacade;
import com.biblioteca.entidad.LogConsulta;
import com.biblioteca.entidad.LogDescargas;
import com.biblioteca.entidad.RolSoftMenu;
import com.biblioteca.entidad.Usuario;
import com.biblioteca.entidad.UsuarioRolSoftware;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author mateo
 */
@ManagedBean(name = "usuarioLogin")
@SessionScoped
public class UsuarioLoginControlador {

    /**
     * Creates a new instance of UsuarioLoginControlador
     */
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    private LogConsultaFacade logConsultaEjbFacade;
    private String usuario;
    private String clave;
    private String claveActual;
    private String claveNueva;
    private String claveConfirmada;
    private boolean login;
    private String url;
    private Usuario usuarioSession;
    private LogConsulta logC;

    public LogConsulta getLogC() {
        return logC;
    }

    public void setLogC(LogConsulta logC) {
        this.logC = logC;
    }
    
    
    

    public String getClaveActual() {
        return claveActual;
    }

    public void setClaveActual(String claveActual) {
        this.claveActual = claveActual;
    }

    public String getClaveNueva() {
        return claveNueva;
    }

    public void setClaveNueva(String claveNueva) {
        this.claveNueva = claveNueva;
    }

    public String getClaveConfirmada() {
        return claveConfirmada;
    }

    public void setClaveConfirmada(String claveConfirmada) {
        this.claveConfirmada = claveConfirmada;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UsuarioFacade getUsuarioFacade() {
        return usuarioFacade;
    }

    public void setUsuarioFacade(UsuarioFacade usuarioFacade) {
        this.usuarioFacade = usuarioFacade;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public Usuario getUsuarioSession() {
        return usuarioSession;
    }

    public void setUsuarioSession(Usuario usuarioSession) {
        this.usuarioSession = usuarioSession;
    }

    public void cerrarSession() throws IOException {
        this.login = false;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        FacesContext.getCurrentInstance().getExternalContext().getSession(login);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath = ((ServletContext) ext.getContext()).getContextPath();
        ext.redirect(ctxPath + "/biblioteca/inicial/inicio.xhtml?close");

    }

    public void confirmar() throws Exception {
        String ruta = "";
        FacesContext context = FacesContext.getCurrentInstance();
        ServletContext servContx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        ruta = (String) servContx.getContextPath();

        List<Usuario> usuarios = usuarioFacade.findUsuario(this.usuario);
        if (usuarios.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundle.getBundle("/Bundle").getString("errorLogin"), ResourceBundle.getBundle("/Bundle").getString("errorUsuarioNoExiste")));
            return;
        }

        Usuario user = usuarios.get(0);
        this.usuarioSession = user;
        if (user.getEstado() == Integer.parseInt(
                ResourceBundle.getBundle("/Bundle").getString("constanteUsuarioEstadoInavito"))) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundle.getBundle("/Bundle").getString("errorLogin"), ResourceBundle.getBundle("/Bundle").getString("usuarioMensajeInactivo") + this.usuario));
            return;
        }

        if (user.getClave().equals(md5(this.clave))) {
            this.logC = new LogConsulta();
            this.login = true;
            this.logC.setUsuario(getUsuarioSession());
            this.logC.setConsulta(getUsuarioSession().getCorreoElectronico());
            this.logC.setFechaConsulta(new Date());
            this.logC.setLogDescargasList(new ArrayList<LogDescargas>());
            this.logConsultaEjbFacade.create(logC);
            context.getExternalContext().redirect(ruta + "/biblioteca/buscador/buscador.xhtml");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundle.getBundle("/Bundle").getString("errorLogin"), ResourceBundle.getBundle("/Bundle").getString("mensajeNoLogin") + this.usuario));
        }
    }

    private static String md5(String clear) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(clear.getBytes());
        int size = b.length;
        StringBuilder h = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int u = b[i] & 255;
            if (u < 16) {
                h.append("0").append(Integer.toHexString(u));
            } else {
                h.append(Integer.toHexString(u));
            }
        }

        return h.toString();
    }

    public boolean permisosPagina(String currentPage) {
        if(currentPage.contains(ResourceBundle.getBundle("/Bundle").getString("contantePaginaPerfilEditar"))){
            return true;
        }
        if (this.usuarioSession.getUsuarioRolSoftwareList() == null) {
            return false;
        }
        List<UsuarioRolSoftware> roles = this.usuarioSession
                .getUsuarioRolSoftwareList();


        for (UsuarioRolSoftware rol : roles) {
            for (RolSoftMenu menu : rol.getIdRol().getRolSoftMenuList()) {
                if (currentPage.contains(menu.getIdMenu().getUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a new instance of UsuarioLogin
     */
    @PostConstruct
    public void reset() {
        ServletContext servContx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        url = (String) servContx.getContextPath();
    }

    public UsuarioLoginControlador() {
    }
}
