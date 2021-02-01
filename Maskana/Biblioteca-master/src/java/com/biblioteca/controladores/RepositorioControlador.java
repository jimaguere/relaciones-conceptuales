/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores;


import com.biblioteca.clases.IndexarDocumento;
import com.biblioteca.dao.DocumentoFacade;
import java.io.File;
import java.text.ParseException;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.quartz.SchedulerException;


/**
 *
 * @author mateo
 */
@ManagedBean
@ApplicationScoped
public class RepositorioControlador {

    /**
     * Creates a new instance of RepositorioControlador
     */
    private String rutaOntologia;
    private boolean repositorio;
    @EJB
    DocumentoFacade documentoFacade;

    public String getRutaOntologia() {
        return rutaOntologia;
    }

    public void setRutaOntologia(String rutaOntologia) {
        this.rutaOntologia = rutaOntologia;
    }

    public boolean isRepositorio() {
        return repositorio;
    }

    public void setRepositorio(boolean repositorio) {
        this.repositorio = repositorio;
    }
    
    public String getIndice(){
        return rutaOntologia+ResourceBundle.getBundle("/Bundle").getString("constanteRutaIndice");
    }

    public void crearRepositorio() {
        try {
            IndexarDocumento index = new IndexarDocumento();
            index.crearIndice(rutaOntologia+ResourceBundle.getBundle("/Bundle").getString("constanteRutaIndice"));
            index.cerrar();
            this.repositorio = true;
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundle.getBundle("/Bundle").getString("mensajeRepositorioCreado"), ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundle.getBundle("/Bundle").getString("mensajeErrorReposotorioCreado"), ""));
        }
    }

    public void vaciarRepositorio() {
        try {
         //   this.documentoFacade.borrarDocumentos();
            IndexarDocumento doc = new IndexarDocumento();
            doc.vaciarRepositorio(rutaOntologia);
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Repositorio Limpio ", ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al vaciar repositorio ", ""));
        }
    }

    @PostConstruct
    public void iniciarServer(){
        this.repositorio = false;
    }

    public RepositorioControlador() {
    }
}
