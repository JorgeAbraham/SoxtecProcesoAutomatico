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
import serviciosBD.ManejadorDeDatos;
import serviciosBD.operaciones_servicio;
import serviciosBD.pagos_fijos_servicio;
import utilidadEmail.CorreoElectronico;
import utilidadesReportesJasper.Reportes;


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
        this.pass="abgomez181";
                
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
    
    

            
            
    public void enviarEmailReporte(){
        utilidadesReportesJasper.Reportes R=new Reportes();
        R.addParametro("FechaInicial", "1999-01-01");
        R.addParametro("FechaFinal", "2200-01-01");
        
        //ManejadorDeDatos M=new ManejadorDeDatos(ManejadorDeDatos.IP_LOCAL);
        ManejadorDeDatos M=new ManejadorDeDatos();
        
        //String archivoReporte="C:/Program Files/Apache Software Foundation/Tomcat 9.0/webapps/SoxtecWeb/reportes/FormatoPuertasArnes.jasper";
        String archivoReporte="C:/Program Soxtec/ProgramasJava/SoxtecWeb/web/reportes/FormatoPuertasArnes.jasper";
        String salida="c:/ReportTemp/Report.pdf";
        R.creaArchivoPDF(archivoReporte, salida, ManejadorDeDatos.BD.getCon());
        R.verReporte(archivoReporte, ManejadorDeDatos.BD.getCon());
        
        utilidadEmail.CorreoElectronico CE=new CorreoElectronico(props, usuario, pass);
        CE.addAdjuntarArchivo("c:/ReportTemp/Report.pdf", "Report.pdf");
        
        
        String Html="<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "	<title>Estilos CSS Avanzados</title>\n" +
                    "	<!--Etiquetas metas importantes-->\n" +
                    "	<meta charset=\"utf-8\">\n" +
                    "	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "	<!--Fuente Roboto obtenida desde Google Fonts-->\n" +
                    "	<link href=\"https://fonts.googleapis.com/css?family=Roboto:400,500,700\" rel=\"stylesheet\">\n" +
                    "	<style>\n" +
                    "		body{\n" +
                    "	font-family: 'Roboto', sans-serif;\n" +
                    "}\n" +
                    "\n" +
                    "/*\n" +
                    "	Para mayor información visita los siguientes enlaces:\n" +
                    "	https://goo.gl/rfRwjR\n" +
                    "	https://goo.gl/KpQrQV\n" +
                    "	https://goo.gl/PJceyf\n" +
                    "*/\n" +
                    "\n" +
                    ".info{\n" +
                    "	border: thin solid #585151;\n" +
                    "	border-radius: 10px;\n" +
                    "	padding: 1em;\n" +
                    "	margin-bottom: 2em;\n" +
                    "	width: 60%;\n" +
                    "}\n" +
                    "/*\n" +
                    "Estilo aplicado a todas las clases que\n" +
                    "contengan la palabra link, dentro de su nombre\n" +
                    "por ejemplo: link-web, link-oficial, ...\n" +
                    "*/\n" +
                    "a[class*=\"link\"]{\n" +
                    "	display: block;\n" +
                    "	font-size: 1.8em;\n" +
                    "	position: relative;\n" +
                    "}\n" +
                    "/*\n" +
                    "Estilo para todos los tag a que contengan en su nombre\n" +
                    "la cadena link, a excepción del ultimo elemento, por que \n" +
                    "se lo esta negando con el atributo :not()\n" +
                    "*/\n" +
                    "a[class*=\"link\"]:not(:last-child){\n" +
                    "	margin-top: 0.8em;\n" +
                    "	margin-bottom: 1.5em;\n" +
                    "}\n" +
                    ".link-info[title]::after{\n" +
                    "	font-size: 0.5em;\n" +
                    "	margin-left: 1em;\n" +
                    "}\n" +
                    "/*Estilos aplicados según la cadena que contenga su atributo title*/\n" +
                    ".link-info[title='Buscador Google']{\n" +
                    "	color: #DA3232;\n" +
                    "}\n" +
                    ".link-info[title='01luisrene - Luis Rene Mas Mas']{\n" +
                    "	color: #29942E;\n" +
                    "}\n" +
                    ".link-info[title='Tutoriales de desarrollo']{\n" +
                    "	color: #04B176;\n" +
                    "}\n" +
                    ".link-info[title='Libros web de programación']{\n" +
                    "	color: #3F13A4;\n" +
                    "}\n" +
                    "/*Agrego el contenido del atributo title al final del tag a*/\n" +
                    ".link-info[title]::after{\n" +
                    "	content: attr(title);\n" +
                    "}\n" +
                    "\n" +
                    "/*Estilo cuando el tag esta activado*/\n" +
                    ".link:active{\n" +
                    "	color: #3E7DDB;\n" +
                    "	text-decoration: none;\n" +
                    "	border-bottom: thin solid #000;\n" +
                    "}\n" +
                    "/*Estilo cuando el puntero del mouse pasa sobre el tag*/\n" +
                    ".link:hover{\n" +
                    "	color: #171616;\n" +
                    "}\n" +
                    "/*Estilo cuando el foco esta sobre el tag*/\n" +
                    ".link:focus{\n" +
                    "	color: #45AB45;\n" +
                    "	outline: dotted;\n" +
                    "	text-decoration: none;\n" +
                    "}\n" +
                    "/*Estilo cuando el link aya sido visitado*/\n" +
                    ".link:visited{\n" +
                    "	color: #A025A4;\n" +
                    "	font-weight: 700;\n" +
                    "	text-transform: capitalize;\n" +
                    "}\n" +
                    "\n" +
                    ".sistemas-operativos{\n" +
                    "	list-style-type: upper-roman;\n" +
                    "}\n" +
                    ".sistemas-operativos li p{\n" +
                    "	margin: 0;\n" +
                    "}\n" +
                    "\n" +
                    "/*Estilo para el primer elemento li*/\n" +
                    ".sistemas-operativos li:first-child p{\n" +
                    "	color: #1496E9\n" +
                    "	font-size: 1.2em;\n" +
                    "	font-weight: 700;\n" +
                    "}\n" +
                    "/*Estilo para el elemento li posición 2*/\n" +
                    ".sistemas-operativos li:nth-child(2) p{\n" +
                    "	color: #1B1C1C;\n" +
                    "	font-size: 1.4em;\n" +
                    "}\n" +
                    "/*Estilo para el elemento li posición 3*/\n" +
                    ".sistemas-operativos li:nth-child(3) p{\n" +
                    "	color: #DB0817;\n" +
                    "	font-size: 1.4em;\n" +
                    "}\n" +
                    "/*Estilo para el ultimo elemento li*/\n" +
                    ".sistemas-operativos li:last-child p{\n" +
                    "	font-size: 1.2em;\n" +
                    "	color: #F73B08;\n" +
                    "	letter-spacing: 3px;\n" +
                    "	text-transform: uppercase;\n" +
                    "}\n" +
                    "/*Estilo para todos los elementos li a excepción del ultimo*/\n" +
                    ".sistemas-operativos li:not(:last-child){\n" +
                    "	margin-bottom: 10px;\n" +
                    "}\n" +
                    "\n" +
                    ".pago{\n" +
                    "	font-size: 1.3em;\n" +
                    "}\n" +
                    ".monto-pago{\n" +
                    "	font-size: 1.5em;\n" +
                    "	font-weight: 700;\n" +
                    "}\n" +
                    "\n" +
                    "/*Estilo que muestra el atributo antes del tag*/\n" +
                    ".monto-pago::before{\n" +
                    "	content: attr(data-left);\n" +
                    "}\n" +
                    "/*Estilo que muestra el atributo despues del tag*/\n" +
                    ".monto-pago::after{\n" +
                    "	content: attr(data-right);\n" +
                    "}\n" +
                    "\n" +
                    ".poesia{\n" +
                    "	font-size: 2em;\n" +
                    "	margin-left: 40px;\n" +
                    "	margin-right: 40px;\n" +
                    "}\n" +
                    "\n" +
                    "/*Estilo para la primera letra del texto*/\n" +
                    ".poesia::first-letter{\n" +
                    "	font-size: 2.3em;\n" +
                    "}\n" +
                    "\n" +
                    ".texto-grande{\n" +
                    "	font-size: 1.8em;\n" +
                    "}\n" +
                    "/*Estilo para la primera linea de texto*/\n" +
                    ".texto-grande::first-line{\n" +
                    "	background-color: yellow;\n" +
                    "	letter-spacing: 8px;\n" +
                    "	text-decoration: underline;\n" +
                    "	font-weight: 700;\n" +
                    "}\n" +
                    "\n" +
                    "/*Estilo a aplicarse cuando se selecciona una porción de texto con el mouse*/\n" +
                    "::selection{\n" +
                    "	background: #D61212;\n" +
                    "	color: #fff;\n" +
                    "}\n" +
                    "\n" +
                    "/*============Combinadores y selectores múltiples + flexbox============*/\n" +
                    "\n" +
                    "/*\n" +
                    "FlexBox: si deseas mas información y ver el funcionamiento\n" +
                    " en tiempo real visita http://flexbox.help/\n" +
                    "*/\n" +
                    ".lista{\n" +
                    "	display: flex;\n" +
                    "	flex-direction: row;\n" +
                    "	flex-wrap: wrap;\n" +
                    "	justify-content: space-between;\n" +
                    "	align-items: stretch;\n" +
                    "	align-content: stretch;\n" +
                    "}\n" +
                    "/*\n" +
                    "Puedes aplicar un mismo estilo a varios \n" +
                    "elementos separándolos por una coma\n" +
                    "*/\n" +
                    ".lista .item, .lista .sub-item{\n" +
                    "	box-sizing: border-box;\n" +
                    "}\n" +
                    "/*Estilo para el primer elemento*/\n" +
                    ".lista .item:first-child{\n" +
                    "	background: #ABD6FB;\n" +
                    "	margin-bottom: 2em;\n" +
                    "	width: 100%;\n" +
                    "}\n" +
                    "/*\n" +
                    "Estilo para hijo de tag div que se \n" +
                    "encuentre dentro de la clase padre .lista\n" +
                    "*/\n" +
                    ".lista div{\n" +
                    "	padding: 1em;\n" +
                    "}\n" +
                    "\n" +
                    "/*\n" +
                    "Estilo para uno de los siguientes \n" +
                    "hermanos que coincida con el tag div\n" +
                    "*/\n" +
                    ".lista div  ~  div{\n" +
                    "	margin-bottom: 1em;\n" +
                    "	width: 48%;\n" +
                    "}\n" +
                    "\n" +
                    "/*\n" +
                    "Estilo para hijos directos de los tag div que\n" +
                    " sean descendientes del contenedor padre .lista\n" +
                    " */\n" +
                    ".lista > div{\n" +
                    "	border: thin solid #8E8E8E;\n" +
                    "}\n" +
                    "/*\n" +
                    "Estilo que se aplica a partir del segundo tag p \n" +
                    "que se encuentre en el contenedor padre:\n" +
                    "en este caso le aplicaran a todos los párrafos\n" +
                    "que se encuentren dentro de .lista div sin importar\n" +
                    " que sea un sub hijo.\n" +
                    "*/\n" +
                    ".lista div p + p{\n" +
                    "	display: inline-block;\n" +
                    "	position: relative;\n" +
                    "	padding-left: 20px;\n" +
                    "	padding-right: 20px;\n" +
                    "}\n" +
                    "/*Estilo before y after*/\n" +
                    ".lista div p + p::before, .lista div p + p::after{\n" +
                    "	font-size: 2em;\n" +
                    "	font-family: sans-serif;\n" +
                    "	content: '\"';\n" +
                    "	height: 15px;\n" +
                    "	width: 15px;\n" +
                    "	position: absolute;\n" +
                    "}\n" +
                    "\n" +
                    ".lista div p + p::before{\n" +
                    "	top: 0;\n" +
                    "	left: 0;\n" +
                    "}\n" +
                    ".lista div p + p::after{\n" +
                    "	right: 0;\n" +
                    "	bottom: 0;\n" +
                    "}\n" +
                    "/*Estilo que se aplica a las clases .sub-item*/\n" +
                    ".lista div .sub-item{\n" +
                    "	border-left: 4px solid #8E8E8E;\n" +
                    "	width: 100%;\n" +
                    "}\n" +
                    "\n" +
                    "/*Regla media query*/\n" +
                    "@media (max-width: 600px) { /*Se aplica siempre y cuando el ancho de la pantalla sea menor a 600px*/\n" +
                    "	.lista .item{\n" +
                    "		background: #D2D1D1;\n" +
                    "		width: 100%;\n" +
                    "	}\n" +
                    "}\n" +
                    "	</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "	<!--Selectores de Atributos-->\n" +

                    "	\n" +
                    "	<!--Ejemplo para first-letter-->\n" +
                    "	\n" +
                    "\n" +
                    "	<!--Ejemplo para Combinadores y selectores múltiples + flexbox-->\n" +
                    "	<div class=\"lista\">\n" +

                    "		<div class=\"item\">\n" +

                                    "<img src=\"https://www.soxtec.com.mx/logo/logoSox.png\" border=\"0\" width=\"197 \" height=\"60\">\n" +
                    "		<h1>Has received an email from Soxtec</h1>\n" +

                    	
                    "			<div class=\"sub-item\">\n" +
                    "				<h4>Report.</h4>\n" +
                    "				<p>The files are in the attached documents.</p>\n" +
                    "			</div>\n" +               
                    "		</div>\n" +

                    "	</div>\n" +
                    "</body>\n" +
                    "</html>";
        
        
        CE.enviaStartTLSArchivoAdjunto("abraham.gomez@soxtec.com.mx",
               "Report Soxtec",
                Html);
        
         CE.enviaStartTLSArchivoAdjunto("luis.fuentes@fcagroup.com",
               "Report Soxtec",
                Html);
         
             CE.enviaStartTLSArchivoAdjunto("veronica.navarrete@soxtec.com.mx",
               "Report Soxtec",
                Html);

             
                 CE.enviaStartTLSArchivoAdjunto("estrella.amador@soxtec.com.mx",
               "Report Soxtec",
                Html);
        
        
        
    }
    
    
    public static void main(String[] args) {
        
        
        SoxtecProcesoAutomatico PA=new SoxtecProcesoAutomatico();
        // PA.procesoPorSegundo();
     //   PA.enviarEmailReporte();
        
    }
    
}
