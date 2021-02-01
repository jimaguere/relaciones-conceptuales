/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.dao;

import com.biblioteca.entidad.TipoDocumento;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mateo
 */
@Stateless
public class TipoDocumentoFacade extends AbstractFacade<TipoDocumento> {

    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoDocumentoFacade() {
        super(TipoDocumento.class);
    }

    public void editar(TipoDocumento tipoDocumento) {
        Query cq = getEntityManager().createNativeQuery("delete from tipo_documento_meta_dato where id_tipo_doc=?");
        cq.setParameter(1, tipoDocumento.getIdTipoDoc());
        cq.executeUpdate();
        getEntityManager().merge(tipoDocumento);
    }
}
