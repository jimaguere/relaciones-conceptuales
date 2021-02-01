/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author mateo
 */
@Entity
@Table(name = "log_descargas", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "LogDescargas.findAll", query = "SELECT l FROM LogDescargas l"),
    @NamedQuery(name = "LogDescargas.findById", query = "SELECT l FROM LogDescargas l WHERE l.id = :id"),
    @NamedQuery(name = "LogDescargas.findByFechaDescarga", query = "SELECT l FROM LogDescargas l WHERE l.fechaDescarga = :fechaDescarga")})
public class LogDescargas implements Serializable {
    @JoinColumn(name = "id_consulta", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private LogConsulta idConsulta;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_descarga", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaDescarga;
    @JoinColumn(name = "usuario", referencedColumnName = "usuario", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;
    @JoinColumn(name = "id_documento", referencedColumnName = "id_documento", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Documento idDocumento;

    public LogDescargas() {
    }

    public LogDescargas(Integer id) {
        this.id = id;
    }

    public LogDescargas(Integer id, Date fechaDescarga) {
        this.id = id;
        this.fechaDescarga = fechaDescarga;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaDescarga() {
        return fechaDescarga;
    }

    public void setFechaDescarga(Date fechaDescarga) {
        this.fechaDescarga = fechaDescarga;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Documento getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Documento idDocumento) {
        this.idDocumento = idDocumento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogDescargas)) {
            return false;
        }
        LogDescargas other = (LogDescargas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.LogDescargas[ id=" + id + " ]";
    }

    public LogConsulta getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(LogConsulta idConsulta) {
        this.idConsulta = idConsulta;
    }
    
}
