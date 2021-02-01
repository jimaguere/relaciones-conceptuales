/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mateo
 */
@Entity
@Table(name = "log_consulta", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "LogConsulta.findAll", query = "SELECT l FROM LogConsulta l"),
    @NamedQuery(name = "LogConsulta.findById", query = "SELECT l FROM LogConsulta l WHERE l.id = :id"),
    @NamedQuery(name = "LogConsulta.findByConsulta", query = "SELECT l FROM LogConsulta l WHERE l.consulta = :consulta")})
public class LogConsulta implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idConsulta", fetch = FetchType.LAZY)
    private List<LogDescargas> logDescargasList;
    @Column(name = "fecha_consulta")
    @Temporal(TemporalType.DATE)
    private Date fechaConsulta;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "consulta", nullable = false, length = 2147483647)
    private String consulta;
    @JoinColumn(name = "usuario", referencedColumnName = "usuario", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;

    public LogConsulta() {
    }

    public LogConsulta(Integer id) {
        this.id = id;
    }

    public LogConsulta(Integer id, String consulta) {
        this.id = id;
        this.consulta = consulta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof LogConsulta)) {
            return false;
        }
        LogConsulta other = (LogConsulta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.LogConsulta[ id=" + id + " ]";
    }

    public Date getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Date fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public List<LogDescargas> getLogDescargasList() {
        return logDescargasList;
    }

    public void setLogDescargasList(List<LogDescargas> logDescargasList) {
        this.logDescargasList = logDescargasList;
    }
    
}
