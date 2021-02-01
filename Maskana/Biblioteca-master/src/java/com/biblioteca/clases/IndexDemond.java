/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mateo
 */
public class IndexDemond {

    private String fileJar;

    public String getFileJar() {
        return fileJar;
    }

    public void setFileJar(String fileJar) {
        this.fileJar = fileJar;
    }

    public void levantarDemonio(String bdData,String clave,String urlRepositorio,
            String usuario) {
        Process p;
        ProcessBuilder pb = new ProcessBuilder("java -jar" + fileJar+ bdData
                +" "+clave+" "+urlRepositorio);
        try {
            p = pb.start();
        } catch (IOException ex) {
            Logger.getLogger(IndexDemond.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
