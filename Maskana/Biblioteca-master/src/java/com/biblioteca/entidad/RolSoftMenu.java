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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author mateo
 */
@Entity
@Cacheable(false)
@Table(name = "rol_soft_menu", catalog = "bd_biblioteca", schema = "public")
@NamedQueries({
    @NamedQuery(name = "RolSoftMenu.findAll", query = "SELECT r FROM RolSoftMenu r"),
    @NamedQuery(name = "RolSoftMenu.findByIdRolSoftMenu", query = "SELECT r FROM RolSoftMenu r WHERE r.idRolSoftMenu = :idRolSoftMenu")})
public class RolSoftMenu implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="RolSoftMenu_ID_GENERATOR", sequenceName="rol_soft_menu_id_rol_soft_menu_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RolSoftMenu_ID_GENERATOR")
    @Column(name = "id_rol_soft_menu", nullable = false)
    private Integer idRolSoftMenu;
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RolSoftware idRol;
    @JoinColumn(name = "id_menu", referencedColumnName = "id_menu", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Menu idMenu;

    public RolSoftMenu() {
    }

    public RolSoftMenu(Integer idRolSoftMenu) {
        this.idRolSoftMenu = idRolSoftMenu;
    }

    public Integer getIdRolSoftMenu() {
        return idRolSoftMenu;
    }

    public void setIdRolSoftMenu(Integer idRolSoftMenu) {
        this.idRolSoftMenu = idRolSoftMenu;
    }

    public RolSoftware getIdRol() {
        return idRol;
    }

    public void setIdRol(RolSoftware idRol) {
        this.idRol = idRol;
    }

    public Menu getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(Menu idMenu) {
        this.idMenu = idMenu;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRolSoftMenu != null ? idRolSoftMenu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolSoftMenu)) {
            return false;
        }
        RolSoftMenu other = (RolSoftMenu) object;
        if ((this.idRolSoftMenu == null && other.idRolSoftMenu != null) || (this.idRolSoftMenu != null && !this.idRolSoftMenu.equals(other.idRolSoftMenu))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.RolSoftMenu[ idRolSoftMenu=" + idRolSoftMenu + " ]";
    }
    
}
