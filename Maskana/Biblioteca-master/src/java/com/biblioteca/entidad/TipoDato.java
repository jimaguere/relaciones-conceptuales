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
@Table(name = "tipo_dato", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "TipoDato.findAll", query = "SELECT t FROM TipoDato t"),
    @NamedQuery(name = "TipoDato.findByIdTipoDato", query = "SELECT t FROM TipoDato t WHERE t.idTipoDato = :idTipoDato"),
    @NamedQuery(name = "TipoDato.findByDescripcion", query = "SELECT t FROM TipoDato t WHERE t.descripcion = :descripcion")})
public class TipoDato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tipo_dato", nullable = false)
    private Integer idTipoDato;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "descripcion", nullable = false, length = 2147483647)
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTipoDato", fetch = FetchType.LAZY)
    private List<MetaDato> metaDatoList;

    public TipoDato() {
    }

    public TipoDato(Integer idTipoDato) {
        this.idTipoDato = idTipoDato;
    }

    public TipoDato(Integer idTipoDato, String descripcion) {
        this.idTipoDato = idTipoDato;
        this.descripcion = descripcion;
    }

    public Integer getIdTipoDato() {
        return idTipoDato;
    }

    public void setIdTipoDato(Integer idTipoDato) {
        this.idTipoDato = idTipoDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<MetaDato> getMetaDatoList() {
        return metaDatoList;
    }

    public void setMetaDatoList(List<MetaDato> metaDatoList) {
        this.metaDatoList = metaDatoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoDato != null ? idTipoDato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoDato)) {
            return false;
        }
        TipoDato other = (TipoDato) object;
        if ((this.idTipoDato == null && other.idTipoDato != null) || (this.idTipoDato != null && !this.idTipoDato.equals(other.idTipoDato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
}
