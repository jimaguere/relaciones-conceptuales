/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mateo
 */
@Entity
@Cacheable(false)
@Table(name = "meta_dato", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "MetaDato.findAll", query = "SELECT m FROM MetaDato m"),
    @NamedQuery(name = "MetaDato.findByIdMetaDato", query = "SELECT m FROM MetaDato m WHERE m.idMetaDato = :idMetaDato"),
    @NamedQuery(name = "MetaDato.findByDescripcion", query = "SELECT m FROM MetaDato m WHERE m.descripcion = :descripcion"),
    @NamedQuery(name = "MetaDato.findByMetaDatoIr", query = "SELECT m FROM MetaDato m WHERE m.metaDatoIr = :metaDatoIr")})
public class MetaDato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_meta_dato", nullable = false)
    private Integer idMetaDato;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "descripcion", nullable = false, length = 2147483647)
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "meta_dato_ir", nullable = false)
    private boolean metaDatoIr;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMetaDato", fetch = FetchType.LAZY)
    private List<DocumentoValorMetaDato> documentoValorMetaDatoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMetaDato", fetch = FetchType.LAZY)
    private List<TipoDocumentoMetaDato> tipoDocumentoMetaDatoList;
    @JoinColumn(name = "id_tipo_dato", referencedColumnName = "id_tipo_dato", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoDato idTipoDato;

    public MetaDato() {
    }

    public MetaDato(Integer idMetaDato) {
        this.idMetaDato = idMetaDato;
    }

    public MetaDato(Integer idMetaDato, String descripcion, boolean metaDatoIr) {
        this.idMetaDato = idMetaDato;
        this.descripcion = descripcion;
        this.metaDatoIr = metaDatoIr;
    }

    public Integer getIdMetaDato() {
        return idMetaDato;
    }

    public void setIdMetaDato(Integer idMetaDato) {
        this.idMetaDato = idMetaDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean getMetaDatoIr() {
        return metaDatoIr;
    }

    public void setMetaDatoIr(boolean metaDatoIr) {
        this.metaDatoIr = metaDatoIr;
    }

    public List<DocumentoValorMetaDato> getDocumentoValorMetaDatoList() {
        return documentoValorMetaDatoList;
    }

    public void setDocumentoValorMetaDatoList(List<DocumentoValorMetaDato> documentoValorMetaDatoList) {
        this.documentoValorMetaDatoList = documentoValorMetaDatoList;
    }

    public List<TipoDocumentoMetaDato> getTipoDocumentoMetaDatoList() {
        return tipoDocumentoMetaDatoList;
    }

    public void setTipoDocumentoMetaDatoList(List<TipoDocumentoMetaDato> tipoDocumentoMetaDatoList) {
        this.tipoDocumentoMetaDatoList = tipoDocumentoMetaDatoList;
    }

    public TipoDato getIdTipoDato() {
        return idTipoDato;
    }

    public void setIdTipoDato(TipoDato idTipoDato) {
        this.idTipoDato = idTipoDato;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMetaDato != null ? idMetaDato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MetaDato)) {
            return false;
        }
        MetaDato other = (MetaDato) object;
        if ((this.idMetaDato == null && other.idMetaDato != null) || (this.idMetaDato != null && !this.idMetaDato.equals(other.idMetaDato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.MetaDato[ idMetaDato=" + idMetaDato + " ]";
    }
    
}
