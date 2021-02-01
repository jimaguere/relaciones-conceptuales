/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores.util;

import com.biblioteca.controladores.UsuarioLoginControlador;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author mateo
 */
public class DefaultPhaseListener implements PhaseListener {

    private UsuarioLoginControlador loginMb;

    @Override
    public void afterPhase(PhaseEvent event) {
        
        FacesContext facesContext = event.getFacesContext();
        ExternalContext ext = facesContext.getExternalContext();
        String ctxPath = ((ServletContext) ext.getContext()).getContextPath();

        String currentPage = facesContext.getViewRoot().getViewId();
        boolean isLoginPage;
        System.out.println(currentPage);
        isLoginPage = (currentPage.lastIndexOf("index.xhtml") > -1)||(currentPage.lastIndexOf("template.xhtml") > -1)||(currentPage.contains(ResourceBundle.getBundle("/Bundle").getString("nombreServlet")));

        if (isLoginPage) {
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
            if (session != null) {
                loginMb = (UsuarioLoginControlador) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{usuarioLogin}", UsuarioLoginControlador.class);
                if (loginMb.isLogin()) {
                    try {
                        ext.redirect(ctxPath + "/biblioteca/inicial/inicio.xhtml");
                    } catch (IOException ex) {
                        throw new FacesException("Sesión no login", ex);
                    }
                }
            }
            return;
        }

        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session == null) {
            try {
                ext.redirect(ctxPath + "/index.html?v=nosession");
            } catch (IOException ex) {
                throw new FacesException("Sesión no login", ex);
            }
        } else {
            loginMb = (UsuarioLoginControlador) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{usuarioLogin}", UsuarioLoginControlador.class);
            if (!loginMb.isLogin()) {
                try {
                    ext.redirect(ctxPath + "/index.html?v=nosession");
                } catch (IOException ex) {
                    throw new FacesException("Sesión no login", ex);
                }
            } else if (!currentPage.contains("PaginaNoPermisos.xhtml") &&!currentPage.contains("buscador.xhtml") && !currentPage.contains("grafos.xhtml") ) {
                boolean permiso;
                
                permiso=loginMb.permisosPagina(currentPage);

                if (permiso) {
                    return;
                }
                try {
                    ext.redirect(ctxPath + "/biblioteca/no_permisos/PaginaNoPermisos.xhtml");
                } catch (IOException ex) {
                    throw new FacesException("Sesión no login", ex);
                }
            }

        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
