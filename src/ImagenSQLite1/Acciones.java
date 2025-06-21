package ImagenSQLite1;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.beans.Statement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.PropertyConfigurator;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

public class Acciones {

    private SecretKey key;
    private String skey = "";
    private Cipher desCipher;

    public static DatabaseMetaData DBMD;

    public static String BASEDATOS;
    public static String URL;
    public static String BD_URL;
    public static String BD_FORNAME;

    public static Connection CON;
    public static Statement SQLST;
    public String nombreimg;
//    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Acciones.class);

    public static void SeleccionDB(String DB) {
        switch (DB) {
            case "PostgreSQL" -> {
                BASEDATOS = "/imagenes";
                URL = "localhost";
                BD_URL = "jdbc:postgresql://";
                BD_FORNAME = "org.postgresql.Driver";
            }
            case "H2" -> {
                BASEDATOS = "";
                URL = "./imagenesH2;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
                BD_URL = "jdbc:h2:";
                BD_FORNAME = "org.h2.Driver";
            }
            case "SQLite" -> {
                BASEDATOS = "";
                URL = "imagenes.db";
                BD_URL = "jdbc:sqlite:";
                BD_FORNAME = "org.sqlite.JDBC";
            }
            case "MySQL 8.0" -> {
                BASEDATOS = "/imagenes?serverTimezone=UTC";
                URL = "localhost";
                BD_URL = "jdbc:mysql://";
                BD_FORNAME = "com.mysql.cj.jdbc.Driver";
            }
        }
    }

    public static void conexionPostgres(final String usuario, final String clave) {
        try {
            Class.forName(BD_FORNAME);//llamamos al drives de postgres
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "NO se puede Conectar a la Base de Datos",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        try {
            CON = DriverManager.getConnection(BD_URL + URL + BASEDATOS, usuario, clave);
            DBMD = CON.getMetaData();
        } catch (SQLException ex) {
//            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "usuario o Contraseña Errada ",
                    "ATENCION",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void ConexionMysql(final String usuario, final String clave) {
        try {
            Class.forName(BD_FORNAME);//llamamos al drives de postgres
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "NO se puede Conectar a la Base de Datos",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        try {
            String url = BD_URL + URL + BASEDATOS;
            CON = DriverManager.getConnection(url, usuario, clave);
            DBMD = CON.getMetaData();
        } catch (SQLException ex) {
//            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "usuario o Contraseña Errada ",
                    "ATENCION",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void ConexionSQLite(final String usuario, final String clave) {
        try {
            Class.forName("org.sqlite.JDBC"); //llamamos al jdrivers de sqlite
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "NO se puede Conectar a la Base de Datos",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        try {
            CON = DriverManager.getConnection(BD_URL + URL + BASEDATOS, usuario, clave); //agregamos el string de conexion,
//            CON = DriverManager.getConnection("jdbc:sqlite:C:\\Proyectos NetBeans\\ImagenSQLite1\\imagenes.db"); //agregamos el string de conexion,  
//            CON = DriverManager.getConnection("jdbc:sqlite::memory:"); //agregamos el string de conexion,
            DBMD = CON.getMetaData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "usuario o Contraseña Errada ",
                    "ATENCION",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void ConexionH2(final String usuario, final String clave) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "NO se puede Conectar a la Base de Datos",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        try {
            CON = DriverManager.getConnection(BD_URL + URL + BASEDATOS, usuario, clave);
            DBMD = CON.getMetaData();

        } catch (SQLException ex) {
//            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);            
            JOptionPane.showMessageDialog(null,
                    "usuario o Contraseña Errada ",
                    "ATENCION",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void Desconectar() {
        try {
            CON.close();
        } catch (SQLException ex) {
            //Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "Error al Desconectar de la Base de Datos",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void crearBaseTablas() {
        String database = "CREATE TABLE IMAGEN (NOMBRE VARCHAR  NOT NULL, ARCHIVO BYTEA, TIPO VARCHAR, PRIMARY KEY(NOMBRE)); ";
        String tabla = "CREATE TABLE IF NOT EXISTS IMAGEN (NOMBRE VARCHAR  NOT NULL, ARCHIVO BYTEA, TIPO VARCHAR, PRIMARY KEY(NOMBRE)); ";
        java.sql.Statement statement;
        try {
            statement = CON.createStatement();
            statement.executeUpdate(database + tabla);
            statement.close();
        } catch (SQLException | java.lang.NullPointerException ex) {
//            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String generateInsertStatement(String tableName, String imageName, String imagePath) {
        File imageFile = new File(imagePath);
        String imageDataString = "";
        try (FileInputStream fis = new FileInputStream(imageFile + "\\" + imageName)) {
            byte[] imageData = new byte[(int) imageFile.length()];
            fis.read(imageData);
            imageDataString = Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String sql = "INSERT INTO " + tableName + " (nombre, archivo, tipo) VALUES ('" + imageName + "', '" + imageDataString + ", 'gif');";
        return sql;
    }

    public static String generateInsertStatement2(String tableName, String imageName, String imagePath) {
        File imageFile = new File(imagePath);
        String imageDataString = "";
        try (FileInputStream fis = new FileInputStream(imageFile + "\\" + imageName)) {
            byte[] imageData = new byte[(int) imageFile.length()];
            fis.read(imageData);
            imageDataString = Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String sql = "INSERT INTO " + tableName + " (nombre, archivo, tipo) VALUES ('" + imageName + "', '" + imageDataString + ", 'gif');";
        return sql;
    }

    public static String generateInsertStatement3(String tableName, String imageName, String imagePath) {
        File imageFile = new File(imagePath);
        String imageDataString = "";
        try (FileInputStream fis = new FileInputStream(imageFile + "\\" + imageName)) {
            byte[] imageData = new byte[(int) imageFile.length()];
            fis.read(imageData);
            imageDataString = Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String sql = "INSERT INTO " + tableName + " (nombre, archivo, tipo) VALUES ('" + imageName + "', '" + imageDataString + ", 'gif');";
        return sql;
    }

    public static String generateInsertStatement4(String tableName, String imageName, String imagePath) {
        File imageFile = new File(imagePath);
        String imageDataString = "";
        try {
            FileInputStream fis = new FileInputStream(imageFile + "\\" + imageName);
            byte[] imageData = new byte[(int) imageFile.length()];
            fis.read(imageData);
            fis.close();
            imageDataString = Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String sql = "INSERT INTO " + tableName + " (nombre, archivo, tipo) VALUES ('" + imageName + "', '" + imageDataString + ", 'gif');";
        return sql;
    }

    public static String generateInsertStatement5(String tableName, String imageName, String imagePath) {
        File imageFile = new File(imagePath);
        String imageDataString = "";
        if (imageFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(imageFile + "\\" + imageName);
                byte[] imageData = new byte[(int) imageFile.length()];
                fis.read(imageData);
                fis.close();
                imageDataString = Base64.getEncoder().encodeToString(imageData);
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        String sql = "INSERT INTO " + tableName + " (nombre, archivo, tipo) VALUES ('" + imageName + "', '" + imageDataString + ", 'gif');";
        return sql;
    }

    public void addKey(String value) {
        try {
            skey = value;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(value.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            key = new SecretKeySpec(keyBytes, "DESede");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public String getsKey() {
        return skey;
    }

    /**
     * Metodo para encriptar un texto
     *
     * @param texto
     * @return String texto encriptado
     */
//    public String encrypt(String texto) {
//        String value = "";
//        try {
//            desCipher = Cipher.getInstance("DESede");
//            //inicia el Cipher para la encriptacion
//            desCipher.init(Cipher.ENCRYPT_MODE, key);
//            byte[] byteDataToEncrypt = texto.getBytes();
//            byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);
//            value = new BASE64Encoder().encode(byteCipherText);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
//            System.err.println(ex.getMessage());
//        }
//        return value;
//    }
    /**
     * Metodo para desencriptar un texto
     *
     * @param texto Texto encriptado
     * @return String texto desencriptado
     */
//    public String decrypt(String texto) {
//        String strDecryptedText = "";
//        byte[] value;
//        try {
//            value = new BASE64Decoder().decodeBuffer(texto);
//
//            desCipher = Cipher.getInstance("DESede");
//            desCipher.init(Cipher.DECRYPT_MODE, key, desCipher.getParameters());
//            byte[] byteDecryptedText = desCipher.doFinal(value);
//            strDecryptedText = new String(byteDecryptedText);
//        } catch (InvalidKeyException | IllegalBlockSizeException | IOException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
//            System.err.println(ex.getMessage());
//        } catch (BadPaddingException ex) {
//            System.err.println(ex.getMessage());
//            JOptionPane.showMessageDialog(null, "La contraseña es incorrecta.");
//        }
//        return strDecryptedText;
//    }
    public static void LookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double Redondear(double numero, int decimales) {
        double resultado;
        BigDecimal res;
        res = new BigDecimal(numero).setScale(decimales, BigDecimal.ROUND_HALF_UP);
        resultado = res.doubleValue();
        return resultado;
    }

    public void imprimir2(JDialog parentFrame, String nombre, String titulo, String icono) {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("propiedades/log4j.properties"));
        if (parentFrame != null) {
            parentFrame.setModal(false);
        }
        try {
            String usuario = "";

            InputStream hoja = this.getClass().getClassLoader().getResourceAsStream(nombre);

            Map parametros = new HashMap();
            parametros.put("quien", usuario);
            parametros.put("nombre", nombreimg);

            JasperPrint print = JasperFillManager.fillReport(hoja, parametros, CON);

            JasperViewer visor = new JasperViewer(print, false);
            visor.setSize(600, 750);
            visor.setResizable(true);
            visor.setLocationRelativeTo(null);
            visor.setVisible(true);
            visor.setTitle(titulo);
            visor.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(icono)));
            visor.setAlwaysOnTop(true);
            visor.setFitPageZoomRatio();
            visor.setFitWidthZoomRatio();
            visor.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        } catch (JRException | SecurityException ex) {
//            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimir(JDialog parentFrame, String nombre, String titulo, String icono) {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("propiedades/log4j.properties"));

        JDialog VentanaImpresion = new JDialog(parentFrame, titulo, true);
        VentanaImpresion.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(icono)));
        VentanaImpresion.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        VentanaImpresion.setSize(600, 750);
        VentanaImpresion.setLocationRelativeTo(null);

        try {
            InputStream hoja = this.getClass().getClassLoader().getResourceAsStream(nombre);
            Map parametros = new HashMap();

            String usuario = "";

            parametros.put("quien", usuario);
            parametros.put("nombre", nombreimg);

            JasperPrint print = JasperFillManager.fillReport(hoja, parametros, CON);
            JRViewer jrViewer = new JRViewer(print);

            float anchoReporte = print.getPageWidth();
            float anchoVentana = VentanaImpresion.getWidth();
            float factorZoom = (float) ((anchoVentana / anchoReporte) / 1.59);

            jrViewer.setZoomRatio(factorZoom);

            VentanaImpresion.getContentPane().add(jrViewer);
            VentanaImpresion.setVisible(true);

        } catch (JRException | SecurityException ex) {
            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public static String MostrarMensaje(String ruta, String tipo) {
//
//        char[] buffer = new char[4000];
//        int chars_leidos;
//        String respuesta = null;
//        StringBuilder cadenacreciente = new StringBuilder(400);
//
//        URL fileURL = Acciones.class.getResource(ruta);
//        BufferedReader br;
//
//        try {
//            br = new BufferedReader(new InputStreamReader(fileURL.openStream()));
//            while (br.ready()) {
//                chars_leidos = br.read(buffer);
//                if (chars_leidos > 0) {
//                    cadenacreciente.append(buffer, 0, chars_leidos);
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        String cadena = cadenacreciente.toString();
//
//        if (tipo.equals("ERROR")) {
//            JOptionPane.showMessageDialog(null,
//                    cadena,
//                    "ERROR",
//                    JOptionPane.ERROR_MESSAGE);
//        }
//        if (tipo.equals("INFORMACION")) {
//            JOptionPane.showMessageDialog(null,
//                    cadena,
//                    "ATENCION",
//                    JOptionPane.INFORMATION_MESSAGE);
//        }
//        if (tipo.equals("CUIDADO")) {
//            JOptionPane.showMessageDialog(null,
//                    cadena,
//                    "CUIDADO",
//                    JOptionPane.WARNING_MESSAGE);
//        }
//        if (tipo.equals("PREGUNTA")) {
//            int n = JOptionPane.showConfirmDialog(null,
//                    cadena,
//                    "ATENCION",
//                    JOptionPane.YES_NO_OPTION);
//
//            if (n == JOptionPane.YES_OPTION) {
//                respuesta = "SI";
//            } else {
//                respuesta = "NO";
//            }
//        }
//        return respuesta;
//    }
//    
//    public void Imprimir(String nombre, String titulo, String icono, String orientacion, Boolean exportarPDF, String tamano) {
//        PropertyConfigurator.configure("log4j.properties");
//
//        boolean convisor = true;
//        int copias = 1;
//        String impresora = null;
//        
//        JasperViewer visor;
//        if (convisor) {
//            try {
//                InputStream hoja = this.getClass().getClassLoader().getResourceAsStream(nombre);
//                Map parametros = new HashMap();
//
//                parametros.put("REP_QUIEN", "");
//
//                parametros.put("REPORT_LOCALE", new java.util.Locale("es", "VE"));
//                JasperPrint print = JasperFillManager.fillReport(hoja, parametros, CON);
//
//                print.setName(titulo);
//
//                if (print.getPages().toString().equals("[]")) {
//                    Acciones.MostrarMensaje("/mensajes/mensaje52.txt", "INFORMACION");
//                } else {
//
//                    if (convisor) {
//                        visor = new JasperViewer(print, false);
//
//                        visor.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(icono)));
//                        if (orientacion.equals("vertical")) {
//                            visor.setSize(600, 750);
//                        } else {
//                            visor.setSize(955, 710);
//                        }
//
//                        visor.setResizable(true);
//                        visor.setTitle(titulo);
//                        visor.setLocationRelativeTo(null);
//                        visor.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
//
//                        visor.setVisible(true);
//
//                        //visor.setZoomRatio(10);
//                        visor.setFitPageZoomRatio();
//                        //visor.setFitWidthZoomRatio();
//                        //visor.setAlwaysOnTop(true);
//                    } else {
//                        // crear el servicio de impresion exportador a fin de que podamos Imprimir en una impresora
//                        JRPrintServiceExporter exportador = new JRPrintServiceExporter();
//                        // coloco el reporte a Imprimir
//                        exportador.setParameter(JRPrintServiceExporterParameter.JASPER_PRINT, print);
//
//                        // indico las copias y el tamaño del papel
//                        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//                        //aset.add(getMediaPrintableArea());
//                        aset.add(new JobName(titulo, null));//nombre del documento a Imprimir
//                        aset.add(new Copies(copias)); //aqui le indico el numero de copias
//                        switch (tamano) {
//                            case "CARTA":
//                                aset.add(MediaSizeName.NA_LETTER); // aqui indico el tamaño del papel
//                                break;
//                            case "OFICIO":
//                                aset.add(MediaSizeName.NA_LEGAL); // aqui indico el tamaño del papel
//                                break;
//                        }
//                        //orientacion del papel
//                        if (orientacion.equals("vertical")) {
//                            aset.add(OrientationRequested.PORTRAIT);
//                        } else {
//                            aset.add(OrientationRequested.LANDSCAPE);
//                        }
//                        exportador.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, aset);
//
//                        // indico la impresora y atributos
//                        PrintServiceAttributeSet serviceAttributeSet = new HashPrintServiceAttributeSet();
//                        // aqui puedo poner el nombre de la impresora leida
//                        serviceAttributeSet.add(new PrinterName(impresora, null));
//                        exportador.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, serviceAttributeSet);
//
//                        exportador.exportReport();
//                    }
//                    //exportar el reporte a pdf en c:
//                    if (exportarPDF) {
//                        JasperExportManager.exportReportToPdfFile(print, "" + titulo + ".pdf");
//                    }
//                }
//
//            } catch (JRException ex) {
////                Logger.getLogger(Beans.class.getName()).log(Level.SEVERE, null, ex);
////                log.error(ex.toString());
//            } catch (java.lang.OutOfMemoryError ex) {
////                Logger.getLogger(Beans.class.getName()).log(Level.SEVERE, null, ex);
////                log.error(ex.toString());
//                Acciones.MostrarMensaje("/mensajes/mensaje95.txt", "INFORMACION");
//            }
//        } else {
//            String respuesta = MostrarMensaje("/mensajes/mensaje35.txt", "PREGUNTA");
//            if (respuesta.equals("SI")) {
////                new Impresora(new javax.swing.JFrame(), true).setVisible(true);
//            }
//        }
//    }
}
