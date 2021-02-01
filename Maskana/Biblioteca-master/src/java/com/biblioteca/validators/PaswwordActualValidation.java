/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.validators;

import java.security.MessageDigest;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author
 */
@FacesValidator("paswordValidationActual") 
public class PaswwordActualValidation implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage facesMessage =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
               ResourceBundle.getBundle("/Bundle").getString("labelClaveAcrtual") , ResourceBundle.getBundle("/Bundle").getString("mensajeNoCoincideClave"));
        try {
            UIComponent confirmarUi = context.getViewRoot().findComponent("accordion:form:hidenT");
            HtmlInputText inputText = (HtmlInputText) confirmarUi;

                if (!inputText.getValue().toString().equals(md5(value.toString()))) {
                    
                    throw new ValidatorException(facesMessage);
                }
        } catch (Exception ex) {
             throw new ValidatorException(facesMessage);
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
}
