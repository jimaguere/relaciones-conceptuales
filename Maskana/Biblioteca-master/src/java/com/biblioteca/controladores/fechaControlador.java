/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author mateo
 */
@ManagedBean
@ViewScoped
public class fechaControlador {

    /**
     * Creates a new instance of fechaControlador
     */
    public fechaControlador() {
    }
    
    public String fecha(Date fecha){
        try{
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        return df.format(fecha);
        }catch(Exception e){
            return "";
        }    
    }
}
