/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mateo
 */
@Entity
@Cacheable(false)
@Table(name = "documento", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Documento.findAll", query = "SELECT d FROM Documento d"),
    @NamedQuery(name = "Documento.findByIdDocumento", query = "SELECT d FROM Documento d WHERE d.idDocumento = :idDocumento"),
    @NamedQuery(name = "Documento.findByEstado", query = "SELECT d FROM Documento d WHERE d.estado = :estado"),
    @NamedQuery(name = "Documento.findByFechaCreacion", query = "SELECT d FROM Documento d WHERE d.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "Documento.findByContenido", query = "SELECT d FROM Documento d WHERE d.contenido = :contenido")})
public class Documento implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDocumento", fetch = FetchType.LAZY)
    private List<LogDescargas> logDescargasList;
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="DOCUMENTO_ID_GENERATOR", sequenceName="documento_id_documento_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DOCUMENTO_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "id_documento", nullable = false)
    private Integer idDocumento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado", nullable = false)
    private short estado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "contenido", nullable = false, length = 2147483647)
    private String contenido;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDocumento", fetch = FetchType.LAZY)
    private List<DocumentoValorMetaDato> documentoValorMetaDatoList;
    @JoinColumn(name = "id_tipo_doc", referencedColumnName = "id_tipo_doc", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoDocumento idTipoDoc;
    @Transient
    private String metaDatosDocumentos;
    @Transient
    private List<Documento> documentosRelacionados;

    public Documento() {
    }
    
    

    public Documento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Documento(Integer idDocumento, short estado, Date fechaCreacion, String contenido) {
        this.idDocumento = idDocumento;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.contenido = contenido;
    }

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public List<DocumentoValorMetaDato> getDocumentoValorMetaDatoList() {
        return documentoValorMetaDatoList;
    }

    public void setDocumentoValorMetaDatoList(List<DocumentoValorMetaDato> documentoValorMetaDatoList) {
        this.documentoValorMetaDatoList = documentoValorMetaDatoList;
    }

    public TipoDocumento getIdTipoDoc() {
        return idTipoDoc;
    }

    public void setIdTipoDoc(TipoDocumento idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    public String getMetaDatosDocumentos() {
        return metaDatosDocumentos;
    }

    public void setMetaDatosDocumentos(String metaDatosDocumentos) {
        this.metaDatosDocumentos = metaDatosDocumentos;
    }

    public List<Documento> getDocumentosRelacionados() {
        return documentosRelacionados;
    }

    public void setDocumentosRelacionados(List<Documento> documentosRelacionados) {
        this.documentosRelacionados = documentosRelacionados;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDocumento != null ? idDocumento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Documento)) {
            return false;
        }
        Documento other = (Documento) object;
        if ((this.idDocumento == null && other.idDocumento != null) || (this.idDocumento != null && !this.idDocumento.equals(other.idDocumento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.Documento[ idDocumento=" + idDocumento + " ]";
    }
    
    public String getNombreEstado(){
      if(ResourceBundle.getBundle("/Bundle").getString("constanteEstadoDocumentoIndexado").
              equals(""+this.estado)){
          return ResourceBundle.getBundle("/Bundle").getString("constanteDocumentoIndexado");
      }
     if(ResourceBundle.getBundle("/Bundle").getString("constanteEstadoDocumentoCreado").
              equals(""+this.estado)){
          return ResourceBundle.getBundle("/Bundle").getString("constanteDOcumentoNoIndexado");
      }
     if(ResourceBundle.getBundle("/Bundle").getString("constanteEstadoDocumentoErrorIndexado").
              equals(""+this.estado)){
          return ResourceBundle.getBundle("/Bundle").getString("constanteDocumentoErrorIndexado");
      }
      return "";
    }
    
    public String getMetaDatosValor(){
        String metaDatos="";
        if(this.documentoValorMetaDatoList==null){
            return metaDatos;
        }
        for(DocumentoValorMetaDato valor:this.documentoValorMetaDatoList){
            metaDatos=metaDatos+valor.getIdMetaDato().getDescripcion()
                    +":"+valor.getValorMetaDato()+"\n";
        }
        return metaDatos;   
    }

    public List<LogDescargas> getLogDescargasList() {
        return logDescargasList;
    }

    public void setLogDescargasList(List<LogDescargas> logDescargasList) {
        this.logDescargasList = logDescargasList;
    }
}