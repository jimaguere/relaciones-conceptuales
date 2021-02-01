/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author mateo
 */
@Entity
@Cacheable(false)
@Table(name = "tipo_documento_meta_dato", catalog = "bd_biblioteca", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_tipo_doc", "id_meta_dato"})})
@NamedQueries({
    @NamedQuery(name = "TipoDocumentoMetaDato.findAll", query = "SELECT t FROM TipoDocumentoMetaDato t"),
    @NamedQuery(name = "TipoDocumentoMetaDato.findByIdTipoDocumentoMetaDato", query = "SELECT t FROM TipoDocumentoMetaDato t WHERE t.idTipoDocumentoMetaDato = :idTipoDocumentoMetaDato")})
public class TipoDocumentoMetaDato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tipo_documento_meta_dato", nullable = false)
    private Integer idTipoDocumentoMetaDato;
    @JoinColumn(name = "id_tipo_doc", referencedColumnName = "id_tipo_doc", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoDocumento idTipoDoc;
    @JoinColumn(name = "id_meta_dato", referencedColumnName = "id_meta_dato", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private MetaDato idMetaDato;

    public TipoDocumentoMetaDato() {
    }

    public TipoDocumentoMetaDato(Integer idTipoDocumentoMetaDato) {
        this.idTipoDocumentoMetaDato = idTipoDocumentoMetaDato;
    }

    public Integer getIdTipoDocumentoMetaDato() {
        return idTipoDocumentoMetaDato;
    }

    public void setIdTipoDocumentoMetaDato(Integer idTipoDocumentoMetaDato) {
        this.idTipoDocumentoMetaDato = idTipoDocumentoMetaDato;
    }

    public TipoDocumento getIdTipoDoc() {
        return idTipoDoc;
    }

    public void setIdTipoDoc(TipoDocumento idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    public MetaDato getIdMetaDato() {
        return idMetaDato;
    }

    public void setIdMetaDato(MetaDato idMetaDato) {
        this.idMetaDato = idMetaDato;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoDocumentoMetaDato != null ? idTipoDocumentoMetaDato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoDocumentoMetaDato)) {
            return false;
        }
        TipoDocumentoMetaDato other = (TipoDocumentoMetaDato) object;
        if ((this.idTipoDocumentoMetaDato == null && other.idTipoDocumentoMetaDato != null) || (this.idTipoDocumentoMetaDato != null && !this.idTipoDocumentoMetaDato.equals(other.idTipoDocumentoMetaDato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.TipoDocumentoMetaDato[ idTipoDocumentoMetaDato=" + idTipoDocumentoMetaDato + " ]";
    }
    
}
