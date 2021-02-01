/**
 *
 */
package com.biblioteca.mail;

import com.biblioteca.aes.Aes;
import java.io.InputStream;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Correo {

    private String asunto;
    private String mensaje;
    private Properties props;
    private Session session;
    private MimeMessage sender;

    public Correo() {
        try {
            //Se obtiene las propiedades
            this.props = this.getProperties();
            //Se inicia la sesion con los parametros de conexion
            this.session = Session.getDefaultInstance(this.props);
            //this.session.setDebug(true);
            //Se crea el objeto para envio de mensajes
            this.sender = new MimeMessage(this.session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Properties getProperties() {
        try {
            Properties propiedades = new Properties();
            InputStream io = this.getClass().getResourceAsStream("smtp.properties");
            propiedades.load(io);
            io.close();
            if (!propiedades.isEmpty()) {
                return propiedades;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addDestino(String destino) {
        try {
            this.sender.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviar() {
        try {
            //this.sender.setFrom(new InternetAddress(NMABusinessProperties.getInstance().getParameter(NMABusinessProperties.PROP_EMAIL_ACCOUNT_FROM),NMABusinessProperties.getInstance().getParameter(NMABusinessProperties.PROP_EMAIL_NAME_FROM)));
            this.sender.setFrom(new InternetAddress(props.getProperty("mail.smtp.user"), props.getProperty("mail.smtp.user")));
            this.sender.setSubject(this.asunto);
            this.sender.setContent(this.mensaje, "text/html;charset=utf-8");
           
            
            String[] cad={"spanish"};
            this.sender.setContentLanguage(cad);

            //Se envia el mensaje con los parametros de autenticacion
            Transport t = this.session.getTransport("smtp");
            
            t.connect(props.getProperty("mail.smtp.user"), Aes.desencriptar(props.getProperty("mail.smtp.password")));
            t.sendMessage(this.sender, this.sender.getAllRecipients());
          
          
            t.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {        
        Correo c = new Correo();
        c.addDestino("jimaguere@hotmail.com");
        c.setAsunto("Asunto 10");
        c.setMensaje("<?xml version='1.0' encoding='iso-8859-1' ?>"
                + "<html>"
                + "<meta content='text/html; charset=iso-8859-1'/>"
                + "<body>"
                + ""
                + "HOLA"
                + "</body></html>");
        c.enviar();
        System.out.println("/documento/List.xhtml".contains("/documento/List"));
    }
}