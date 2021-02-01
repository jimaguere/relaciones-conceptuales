/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.dao;

import com.biblioteca.entidad.DocumentoValorMetaDato;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mateo
 */
@Stateless
public class DocumentoValorMetaDatoFacade extends AbstractFacade<DocumentoValorMetaDato> {
    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoValorMetaDatoFacade() {
        super(DocumentoValorMetaDato.class);
    }
    
}
