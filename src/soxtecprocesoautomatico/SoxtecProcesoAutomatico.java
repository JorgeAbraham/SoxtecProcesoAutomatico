/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soxtecprocesoautomatico;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import serviciosBD.operaciones_servicio;
import serviciosBD.pagos_fijos_servicio;
import utilidadEmail.CorreoElectronico;


/**
 *
 * @author usuario
 */
public class SoxtecProcesoAutomatico {

    String usuario;
    String pass;
    Properties props;
    
    public SoxtecProcesoAutomatico() {
        
        this.usuario="no-reply@soxtec.com.mx"; 
        this.pass="abgomez18";
                
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        props.put("mail.smtp.host", "mail.soxtec.com.mx");
        //props.put("mail.smtp.host", "smtp.gmail.com");
        
        props.put("mail.smtp.port", "26");
        //props.put("mail.smtp.port", "587");
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    
    
    
    
    
    public void procesoPorSegundo(){
        
        
        while (true){
        try {
            
            operaciones_servicio OS=new operaciones_servicio();
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            
             Date fechaActual = new Date();
            String auxDate = formatter.format(fechaActual);
            try { 
                fechaActual = formatter.parse( auxDate );
            } catch (ParseException ex) {
                Logger.getLogger(SoxtecProcesoAutomatico.class.getName()).log(Level.SEVERE, null, ex);
            }
             
             
            Calendar calendario = Calendar.getInstance();
            Thread.sleep(30 * 1000); //cada medio minuo
            int hora =calendario.get(Calendar.HOUR_OF_DAY);
            int minutos = calendario.get(Calendar.MINUTE);
            
            
            
            
            if (  (hora==00 && minutos==01)  ||  (hora==8 && minutos==00)  ){
                
                System.out.println("Se ejecuto el proceso automatico  ");
                
                pagos_fijos_servicio PFS=new pagos_fijos_servicio();
                
                
                String pagosProgramados[][] = PFS.listaPagosProgramadosSinPagar();
                    
                for (int i=0;i<pagosProgramados.length;i++){
                    
                    

                    Date fechaDePago=null;
                    try {
                        fechaDePago =  formatter.parse(pagosProgramados[i][9] );
                    } catch (ParseException ex) {
                        Logger.getLogger(SoxtecProcesoAutomatico.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if ( fechaDePago.getTime() > fechaActual.getTime()  ){
                        CorreoElectronico Correo=new CorreoElectronico(props,usuario, pass);
                        Correo.enviaStartTLS(   pagosProgramados[i][14]   ,
                                                pagosProgramados[i][2] +" Pago VENCIDO" ,
                                                pagosProgramados[i][3]  
                                            ); 
                        
                        OS.actualizaEstado(pagosProgramados[i][10]  , "16");  //Atrazada
                        
                    }
                    
                    
                    if ( fechaDePago.getTime() == fechaActual.getTime()  ){
                        CorreoElectronico Correo=new CorreoElectronico(props,usuario, pass);
                        Correo.enviaStartTLS(   pagosProgramados[i][14]   ,
                                                pagosProgramados[i][2] +" Pago Prioridad Extremo Urgente (HOY)" ,
                                                pagosProgramados[i][3]  
                                            ); 
                        
                        OS.actualizaEstado(pagosProgramados[i][10]  , "14"); //Mismo dia
                        
                    }
                    
                    
                    
                    
                    if ( fechaDePago.getTime() ==    fechaActual.getTime() + 1*1000*60*60*24 )  {
                        CorreoElectronico Correo=new CorreoElectronico(props,usuario, pass);
                        Correo.enviaStartTLS(   pagosProgramados[i][14]   ,
                                                pagosProgramados[i][2] +" Pago Prioridad Urgente (1 Día)" ,
                                                pagosProgramados[i][3]  
                                            ); 
                        
                        OS.actualizaEstado(pagosProgramados[i][10]  , "13"); 
                    }
                    if ( fechaDePago.getTime() == fechaActual.getTime() + 2*1000*60*60*24 ){
                        CorreoElectronico Correo=new CorreoElectronico(props,usuario, pass);
                        Correo.enviaStartTLS(   pagosProgramados[i][14]   ,
                                                pagosProgramados[i][2] +" Pago Prioridad Muy Alta (2 Días)" ,
                                                pagosProgramados[i][3]  
                                            ); 
                        OS.actualizaEstado(pagosProgramados[i][10]  , "12");
                    }
                    if ( fechaDePago.getTime() == fechaActual.getTime() + 3*1000*60*60*24 ){
                        CorreoElectronico Correo=new CorreoElectronico(props,usuario, pass);
                        Correo.enviaStartTLS(   pagosProgramados[i][14]   ,
                                                pagosProgramados[i][2] +" Pago Prioridad Alta (3 Días)" ,
                                                pagosProgramados[i][3]  
                                            ); 
                        OS.actualizaEstado(pagosProgramados[i][10]  , "11");
                    }
                    if ( fechaDePago.getTime() == fechaActual.getTime() +  7*1000*60*60*24 ){
                        CorreoElectronico Correo=new CorreoElectronico(props,usuario, pass);
                        Correo.enviaStartTLS(   pagosProgramados[i][14]   ,
                                                pagosProgramados[i][2]  +" Pago Prioridad Media (7 Días)",
                                                pagosProgramados[i][3]  
                                            ); 
                        OS.actualizaEstado(pagosProgramados[i][10]  , "10");
                    }
                       
                    
                }
                
                
                
                
                 
                 
                 
            }
            
                
                
        } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
        }
        }
    }
    
    
    
    public static void main(String[] args) {
        
        
         SoxtecProcesoAutomatico PA=new SoxtecProcesoAutomatico();
         PA.procesoPorSegundo();
        
    }
    
}
