/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.entidad;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author mateo
 */
@Embeddable
public class RolSoftMenuPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "id_rol_soft_menu", nullable = false)
    private int idRolSoftMenu;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_menu", nullable = false)
    private int idMenu;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_rol", nullable = false)
    private int idRol;

    public RolSoftMenuPK() {
    }

    public RolSoftMenuPK(int idRolSoftMenu, int idMenu, int idRol) {
        this.idRolSoftMenu = idRolSoftMenu;
        this.idMenu = idMenu;
        this.idRol = idRol;
    }

    public int getIdRolSoftMenu() {
        return idRolSoftMenu;
    }

    public void setIdRolSoftMenu(int idRolSoftMenu) {
        this.idRolSoftMenu = idRolSoftMenu;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idRolSoftMenu;
        hash += (int) idMenu;
        hash += (int) idRol;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolSoftMenuPK)) {
            return false;
        }
        RolSoftMenuPK other = (RolSoftMenuPK) object;
        if (this.idRolSoftMenu != other.idRolSoftMenu) {
            return false;
        }
        if (this.idMenu != other.idMenu) {
            return false;
        }
        if (this.idRol != other.idRol) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.biblioteca.entidad.RolSoftMenuPK[ idRolSoftMenu=" + idRolSoftMenu + ", idMenu=" + idMenu + ", idRol=" + idRol + " ]";
    }
    
}
