/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;

import com.biblioteca.dao.DocumentoFacade;
import com.biblioteca.entidad.Documento;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @author Ricardo Timaran Pereira
 * @author Mateo Guerrero Restrepo
 */
public class ReglasAsociacion {
    private Apriori ap;
    private double minConf;
    private List<ItemSetFrequenteL> reglas;
    private DocumentoFacade documentoEjbFacade;
    private List<ReglaDto> reglasFuertes;
    private Map<String,String[]> antecendente;

    public ReglasAsociacion(Apriori ap, double minConf, DocumentoFacade documentoEjbFacade) {
        this.ap = ap;
        this.minConf = minConf;
        this.documentoEjbFacade = documentoEjbFacade;
        this.reglasFuertes=new ArrayList<ReglaDto>();
    }

    public List<ReglaDto> getReglasFuertes() {
        return reglasFuertes;
    }

    public void setReglasFuertes(List<ReglaDto> reglasFuertes) {
        this.reglasFuertes = reglasFuertes;
    }
    
    

    public double getMinConf() {
        return minConf;
    }

    public void setMinConf(double minConf) {
        this.minConf = minConf;
    }

    public List<ItemSetFrequenteL> getReglas() {
        return reglas;
    }

    public void setReglas(List<ItemSetFrequenteL> reglas) {
        this.reglas = reglas;
    }
    
    public DocumentoFacade getDocumentoEjbFacade() {
        return documentoEjbFacade;
    }

    public void setDocumentoEjbFacade(DocumentoFacade documentoEjbFacade) {
        this.documentoEjbFacade = documentoEjbFacade;
    }

    public ReglasAsociacion(Apriori ap,double minConf) {
        this.ap = ap;
        this.ap.setSoporte(2.0);
        this.minConf=minConf;
        this.reglasFuertes=new ArrayList<ReglaDto>();
        this.antecendente=new HashMap<String, String[]>();
    }

    public Apriori getAp() {
        return ap;
    }

    public void setAp(Apriori ap) {
        this.ap = ap;
    }

    public Map<String, String[]> getAntecendente() {
        return antecendente;
    }

    public void setAntecendente(Map<String, String[]> antecendente) {
        this.antecendente = antecendente;
    }
    
    public void regla(ItemSetFrequenteL l){  
        String[] lItem=l.getL().replaceAll("]", "").replaceAll("\\[", "").split(",");
        for(int i=0;i<lItem.length;i++){
            String[] subL=new String[lItem.length-1];
            int c=0;
            for(int j=0;j<lItem.length;j++){
                if(!lItem[i].equals(lItem[j])){
                    subL[c]=lItem[j].trim();
                    c++;
                }
            }        
            String subS= Arrays.toString(subL);
            double sopR=ap.getSoporte()/ap.getReglaLMap().get(subS.trim()).doubleValue();
            if(sopR>minConf){
                ReglaDto rF=new ReglaDto(subS,lItem[i] , sopR);
                rF.setSoporteRegla(ap.getReglaLMap().get(l.getL()).doubleValue());
                reglasFuertes.add(rF);
                antecendente.put(lItem[i].trim(), subL);
            }            
        }
    }
    
    public void generarReglas(){
        List<ItemSetFrequenteL> itemsL=ap.getReglasL();
        reglas=new ArrayList<ItemSetFrequenteL>();
        for(int i=itemsL.size()-1;i>=0;i--){
            ItemSetFrequenteL item=itemsL.get(i);
            if(i==0){
                break;
            } 
            if(item.getL().split(",").length==1){
                break;
            }
            if(i==itemsL.size()-1){
                regla(item);
                continue;
            }
        
            if(item.getL().split(",").length<itemsL.get(i+1).getL().split(",").length){
                break;
            }    
           regla(item);
        }
    }
    
    public static void main(String arg[]) throws Exception{
        String[] entrada = {"/home/and/NetBeansProjects/Biblioteca/prueba.dat", "2"};
        Apriori ap = new Apriori(entrada);
    
        ReglasAsociacion ra=new ReglasAsociacion(ap, Double.parseDouble("0.5"));
        ra.generarReglas();
        for(int i=0;i<ra.reglasFuertes.size();i++){
            System.out.println(ra.reglasFuertes.get(i).getL() +"=>"+ra.reglasFuertes.get(i).getA()+" "+ra.reglasFuertes.get(i).getSoporte()+" "+ra.reglasFuertes.get(i).getSoporteRegla());
        }   
    }
    

}
