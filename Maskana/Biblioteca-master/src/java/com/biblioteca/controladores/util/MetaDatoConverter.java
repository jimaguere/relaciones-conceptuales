/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.controladores.util;

import com.biblioteca.entidad.MetaDato;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author RIcardo TImaran Pereria,Mateo Guerrero
 */
@FacesConverter("metaDatoConverter")
public class MetaDatoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return getObjectFromUIPickListComponent(component, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String string;

        if (value == null) {
            string = "";
        } else {
            try {
                string = String.valueOf(((MetaDato) value).getIdMetaDato());
            } catch (ClassCastException cce) {
                throw new ConverterException();
            }
        }
        return string;
    }

    @SuppressWarnings("unchecked")
    private MetaDato getObjectFromUIPickListComponent(UIComponent component, String value) {
        final DualListModel<MetaDato> dualList;
        try {
            dualList = (DualListModel<MetaDato>) ((PickList) component).getValue();
            MetaDato lin = getObjectFromList(dualList.getSource(), Integer.valueOf(value));
            if (lin == null) {
                lin = getObjectFromList(dualList.getTarget(), Integer.valueOf(value));
            }

            return lin;
        } catch (ClassCastException cce) {
            throw new ConverterException();
        } catch (NumberFormatException nfe) {
            throw new ConverterException();
        }
    }

    private MetaDato getObjectFromList(final List<?> list, final Integer identifier) {
        for (final Object object : list) {
            final MetaDato lin = (MetaDato) object;
            if (lin.getIdMetaDato().equals(identifier)) {
                return lin;
            }
        }
        return null;
    }
}
