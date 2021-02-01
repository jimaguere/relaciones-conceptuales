/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.dao;

import com.biblioteca.entidad.UsuarioRolSoftware;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mateo
 */
@Stateless
public class UsuarioRolSoftwareFacade extends AbstractFacade<UsuarioRolSoftware> {
    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioRolSoftwareFacade() {
        super(UsuarioRolSoftware.class);
    }
    
}
