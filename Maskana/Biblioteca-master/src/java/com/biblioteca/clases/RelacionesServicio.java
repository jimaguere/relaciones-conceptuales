/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.clases;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.in;

import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author JBARCO
 */
public class RelacionesServicio {

    public String send(String id_documento) throws IOException, ParserConfigurationException, SAXException, JSONException {
        URL url = new URL("http://localhost:8890/Modelo_relaciones?id_documento=" + id_documento);
        URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        InputStream input = new BufferedInputStream(urlConnection.getInputStream());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        System.out.append(responseStrBuilder.toString());
        // JSONObject obj=new JSONObject(responseStrBuilder.toString());

        return responseStrBuilder.toString();

        /* Document doc = dBuilder.parse(input);
        NodeList listaSantoral = doc.getElementsByTagName("source").item(0).getChildNodes();
        NodeList listaMoneda=doc.getElementsByTagName("target").item(0).getChildNodes();*/
        //System.out.println(obj.toString());
    }

    public String sendConcep(String id_documento,String concepto) throws IOException, ParserConfigurationException, SAXException, JSONException {
        URL url = new URL("http://localhost:8890/Modelo_relacionesF?id_documento=" + id_documento+"&concepto="+concepto);
        URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        InputStream input = new BufferedInputStream(urlConnection.getInputStream());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        System.out.append(responseStrBuilder.toString());
        // JSONObject obj=new JSONObject(responseStrBuilder.toString());

        return responseStrBuilder.toString();

        /* Document doc = dBuilder.parse(input);
        NodeList listaSantoral = doc.getElementsByTagName("source").item(0).getChildNodes();
        NodeList listaMoneda=doc.getElementsByTagName("target").item(0).getChildNodes();*/
        //System.out.println(obj.toString());
    }

    public static void main(String[] args) throws JSONException {

    }

}
