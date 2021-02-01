/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.dao;

import com.biblioteca.entidad.RolSoftware;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mateo
 */
@Stateless
public class RolSoftwareFacade extends AbstractFacade<RolSoftware> {
    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RolSoftwareFacade() {
        super(RolSoftware.class);
    }
    
    public void editar(RolSoftware rolSoftware){
        Query cq = getEntityManager().createNativeQuery("delete from rol_soft_menu where id_rol=?");
        cq.setParameter(1, rolSoftware.getIdRol());
        cq.executeUpdate();
        getEntityManager().merge(rolSoftware);
    }
    
}
