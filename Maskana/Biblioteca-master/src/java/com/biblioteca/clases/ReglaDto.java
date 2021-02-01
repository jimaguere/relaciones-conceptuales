/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;

/**
 *
 * @author and
 */
public class ReglaDto {
    String l;
    String a;
    String conclusion;
    double soporte;
    double soporteRegla;

    public double getSoporteRegla() {
        return soporteRegla;
    }

    public void setSoporteRegla(double soporteRegla) {
        this.soporteRegla = soporteRegla;
    }

    public ReglaDto(String l, String a, String conclusion, double soporte, double soporteRegla) {
        this.l = l;
        this.a = a;
        this.conclusion = conclusion;
        this.soporte = soporte;
        this.soporteRegla = soporteRegla;
    }

    
    
    public ReglaDto(String l, String a, double soporte) {
        this.l = l;
        this.a = a;
        this.soporte = soporte;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public double getSoporte() {
        return soporte;
    }

    public void setSoporte(double soporte) {
        this.soporte = soporte;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }  
}
