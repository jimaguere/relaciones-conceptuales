/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.validators;

import java.util.ResourceBundle;
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
@FacesValidator("paswordValidation") 
public class PaswordValidation implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) 
            throws ValidatorException {   
    
        UIComponent confirmarUi=context.getViewRoot().findComponent("form:claveConf");
        HtmlInputText inputText=(HtmlInputText)confirmarUi;
        if(!inputText.getValue().toString().equals(value.toString())){
             FacesMessage facesMessage =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    inputText.getLabel()
                    + "",ResourceBundle.getBundle("/Bundle").getString("mensajeValidarPasword"));
            throw new ValidatorException(facesMessage);
        }        
    }  
}
