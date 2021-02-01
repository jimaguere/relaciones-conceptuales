/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca.servlet;

import com.biblioteca.aes.Aes;
import com.biblioteca.entidad.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mateo
 */
public class ConfirmacionServlet extends HttpServlet {

    @EJB
    private com.biblioteca.dao.UsuarioFacade ejbFacade;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ConfirmacionServlet</title>");
            out.println("</head>");
            out.println("<body>");
            
            
            String usuarioClave=Aes.desencriptar(request.getParameter("usuarioclave").replaceAll(" ", "+").replaceAll("comodin", "="));
            String vectorUsu[]=usuarioClave.split("_");

            String nomUsuario = vectorUsu[0];
            String clave=vectorUsu[1];
            Usuario usuario = ejbFacade.find(nomUsuario);
            if (usuario != null) {

                                
                if (usuario.getClave().equals(clave)) {
                    System.out.println("eq");
                    if (usuario.getEstado()
                            == Integer.parseInt(ResourceBundle.getBundle("/Bundle").getString("constanteUsuarioEstadoInavito"))) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(usuario.getFechaCreacion());
                        calendar.add(Calendar.DAY_OF_YEAR, 5);
                        Date fechaActual=new Date();
                        if(fechaActual.compareTo(calendar.getTime())<0){
                            usuario.setEstado(Integer.parseInt(ResourceBundle.getBundle("/Bundle").getString("constanteUsuarioEstadoActivo")));
                            ejbFacade.edit(usuario);
                            out.println("<h1>Su cuenta esta activa puede consultar materieales bibliográficos"
                                    + "</h1>");
                        }else{
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }else{
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ConfirmacionServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ConfirmacionServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
