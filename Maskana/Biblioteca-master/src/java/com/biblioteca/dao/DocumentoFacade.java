/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.dao;

import com.biblioteca.entidad.Documento;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mateo
 */
@Stateless
public class DocumentoFacade extends AbstractFacade<Documento> {
    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoFacade() {
        super(Documento.class);
    }
    
    public  List<Documento> listaDocumentos(){
        return null;
    }
    
    public void editarDocumento(Documento doc){
        Query q=getEntityManager().createNativeQuery("delete from documento_valor_meta_dato where"
                + " id_documento=?");
        q.setParameter(1, doc.getIdDocumento());
        q.executeUpdate();
        getEntityManager().merge(doc);
    }
    


    public List<Object[]> findAllJaroWordsComplet(String string) {
        List<Object[]> listWords=new ArrayList<Object[]>();
         try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();

            listWords= getEntityManager().createNativeQuery("select distinct palabra,"
                    + " fn_porcentaje_similitud((palabra) ,'" + string + "') as aceptacion"
                    + " from vocabulario"
                    + " where"
                    + " fn_porcentaje_similitud(palabra,'" + string + "')>0.6 order by aceptacion desc").getResultList();
        } catch (Exception e) {}
         return listWords;
    }
    
    public Documento finById(Integer id){
        Query q=getEntityManager().createNamedQuery("Documento.findByIdDocumento",Documento.class);
        q.setParameter("idDocumento", id);
        return (Documento)q.getSingleResult();   
    }
    
    public List<Object[]> findDocSimilar(Integer idDocumento){
        List<Object[]> list=new ArrayList<Object[]>();
         try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();

            list= getEntityManager().createNativeQuery("select * from  "
                    + " fn_distancia_milikowski(" + idDocumento + ") ").getResultList();
        } catch (Exception e) {}
         return list;
        
    }
    
}
