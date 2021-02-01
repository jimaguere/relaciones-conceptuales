/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mateo
 */
@Entity
@Cacheable(false)
@Table(name = "documento_valor_meta_dato", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "DocumentoValorMetaDato.findAll", query = "SELECT d FROM DocumentoValorMetaDato d"),
    @NamedQuery(name = "DocumentoValorMetaDato.findByIdDocumentoValorMetaDato", query = "SELECT d FROM DocumentoValorMetaDato d WHERE d.idDocumentoValorMetaDato = :idDocumentoValorMetaDato"),
    @NamedQuery(name = "DocumentoValorMetaDato.findByValorMetaDato", query = "SELECT d FROM DocumentoValorMetaDato d WHERE d.valorMetaDato = :valorMetaDato")})
public class DocumentoValorMetaDato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_documento_valor_meta_dato", nullable = false)
    private Integer idDocumentoValorMetaDato;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "valor_meta_dato", nullable = false, length = 2147483647)
    private String valorMetaDato;
    @JoinColumn(name = "id_meta_dato", referencedColumnName = "id_meta_dato", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private MetaDato idMetaDato;
    @JoinColumn(name = "id_documento", referencedColumnName = "id_documento", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Documento idDocumento;
    @Transient
    private Date fechaMetaDato;
    

    public DocumentoValorMetaDato() {
    }

    public DocumentoValorMetaDato(Integer idDocumentoValorMetaDato) {
        this.idDocumentoValorMetaDato = idDocumentoValorMetaDato;
    }

    public DocumentoValorMetaDato(Integer idDocumentoValorMetaDato, String valorMetaDato) {
        this.idDocumentoValorMetaDato = idDocumentoValorMetaDato;
        this.valorMetaDato = valorMetaDato;
    }

    public Integer getIdDocumentoValorMetaDato() {
        return idDocumentoValorMetaDato;
    }

    public void setIdDocumentoValorMetaDato(Integer idDocumentoValorMetaDato) {
        this.idDocumentoValorMetaDato = idDocumentoValorMetaDato;
    }

    public String getValorMetaDato() {
        return valorMetaDato;
    }

    public void setValorMetaDato(String valorMetaDato) {
        this.valorMetaDato = valorMetaDato;
    }

    public MetaDato getIdMetaDato() {
        return idMetaDato;
    }

    public void setIdMetaDato(MetaDato idMetaDato) {
        this.idMetaDato = idMetaDato;
    }

    public Documento getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Documento idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Date getFechaMetaDato() {
        if(this.fechaMetaDato==null && this.valorMetaDato!=null
                && idMetaDato.getIdTipoDato().getIdTipoDato().intValue()!=
                Integer.parseInt(ResourceBundle.getBundle("/Bundle").getString("constanteTipoDatoCadena"))){
            SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
            try {
                fechaMetaDato=df.parse(valorMetaDato);
            } catch (ParseException ex) {
                Logger.getLogger(DocumentoValorMetaDato.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fechaMetaDato;
    }

    public void setFechaMetaDato(Date fechaMetaDato) {
        this.fechaMetaDato = fechaMetaDato;
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        this.valorMetaDato=df.format(fechaMetaDato);
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDocumentoValorMetaDato != null ? idDocumentoValorMetaDato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DocumentoValorMetaDato)) {
            return false;
        }
        DocumentoValorMetaDato other = (DocumentoValorMetaDato) object;
        if ((this.idDocumentoValorMetaDato == null && other.idDocumentoValorMetaDato != null) || (this.idDocumentoValorMetaDato != null && !this.idDocumentoValorMetaDato.equals(other.idDocumentoValorMetaDato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.DocumentoValorMetaDato[ idDocumentoValorMetaDato=" + idDocumentoValorMetaDato + " ]";
    }
    
    
    
}
