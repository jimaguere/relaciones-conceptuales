/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;

/**
 *
 * @author Ricardo Timaran Pereira
 * @author Mateo Guerrero Restrepo
 */
public class ItemSetFrequenteL {
    private String l;
    private double prob;
    private int soporte;

    
    
    

    public ItemSetFrequenteL(String l, double prob, int soporte) {
        this.l = l;
        this.prob = prob;
        this.soporte = soporte;
    }
  
    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public int getSoporte() {
        return soporte;
    }

    public void setSoporte(int soporte) {
        this.soporte = soporte;
    }
    
    
    
}
