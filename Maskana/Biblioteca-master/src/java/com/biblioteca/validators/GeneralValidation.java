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
@FacesValidator("generalValidation")
public class GeneralValidation implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String label;
        HtmlInputText htmlInputText =
                (HtmlInputText) component;
        if (htmlInputText.getLabel() == null
                || htmlInputText.getLabel().trim().equals("")) {
            label = htmlInputText.getId();
        } else {
            label = htmlInputText.getLabel();
        }
        if (value.toString().trim().length() < 5) {
            FacesMessage facesMessage =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    label
                    + "", ResourceBundle.getBundle("/Bundle").getString("mensajeValidarGeneral"));
            throw new ValidatorException(facesMessage);
        }
        for (int i = 0; i < value.toString().length() - 1; i++) {
            if (value.toString().charAt(i) == value.toString().charAt(i + 1)) {
                FacesMessage facesMessage =
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        label
                        + "", ResourceBundle.getBundle("/Bundle").getString("mensajeValidarGeneral"));
                throw new ValidatorException(facesMessage);
            }
        }
    }
}
