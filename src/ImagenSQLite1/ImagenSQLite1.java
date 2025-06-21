package ImagenSQLite1;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class ImagenSQLite1 extends javax.swing.JFrame {

	Acciones acciones = new Acciones();
	public int nfila = 0;
	public static String nombre = "";
	ImageIcon icono;
	Image ima;
	boolean ajustado = false;
	File Archivo;
	byte[] archivoLeido = null;

	public ImagenSQLite1() {
		Acciones.LookAndFeel();
		initComponents();
		this.setLocationRelativeTo(null);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imagenes/note4.png")));
	}

	public void info() {
		try {
			if (!comboBaseDatos.getSelectedItem().toString().equals("")) {
				campoGestor.setText(Acciones.DBMD.getDatabaseProductName());
				campoVersion.setText(Acciones.DBMD.getDatabaseProductVersion());
				campoBaseDatos.setText(Acciones.DBMD.getURL());
				campoUsuario.setText(Acciones.DBMD.getUserName());
				campoJava.setText(System.getProperty("java.version"));
//            System.out.println(Acciones.DBMD.getUserName());
//            System.out.println(Acciones.DBMD.getDriverName());
//            System.out.println(Acciones.DBMD.getDriverVersion());
			} else {
				campoGestor.setText("");
				campoVersion.setText("");
				campoBaseDatos.setText("");
				campoUsuario.setText("");
			}
		} catch (SQLException | java.lang.NullPointerException ex) {
//            Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@SuppressWarnings("deprecation")
	public double Redondear(double numero, int decimales) {
		double resultado;
		BigDecimal res;
		res = new BigDecimal(numero).setScale(decimales, BigDecimal.ROUND_HALF_UP);
		resultado = res.doubleValue();
		return resultado;
	}

	public void Limpiar() {
		lblImagen.setIcon(null);
		lblFactor.setText("");
		lblNombre.setText("");
		lblRuta.setText("");
		lblFormato.setText("");
		altoOriginal.setText("");
		anchoOriginal.setText("");
		altoAjustado.setText("");
		anchoAjustado.setText("");
		icono = null;
		ima = null;
	}

	public void Ajustar() {
		double ancho = icono.getIconWidth();
		double alto = icono.getIconHeight();

		double factor_real;

		double factor_ancho = ancho / jScrollPane1.getWidth();
		double factor_alto = alto / jScrollPane1.getHeight();

		if (factor_ancho > factor_alto) {
			factor_real = factor_ancho;
		} else {
			factor_real = factor_alto;
		}

		double ancho_nuevo = ancho / factor_real;
		double alto_nuevo = alto / factor_real;

		ima = icono.getImage();

		anchoAjustado.setText("" + (int) ancho_nuevo);
		altoAjustado.setText("" + (int) alto_nuevo);

		lblFactor.setText("" + acciones.Redondear(factor_real, 2));

		lblImagen.setIcon(
				new ImageIcon(ima.getScaledInstance((int) ancho_nuevo - 2, (int) alto_nuevo - 2, Image.SCALE_FAST)));
	}

	static Image ICONO_A_IMAGEN(Icon icon) {
		if (icon instanceof ImageIcon) {
			return ((ImageIcon) icon).getImage();
		} else {
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}

	private String ObtenerFormato(File Archivo) {
		String formato = null;
		try {
			try (ImageInputStream iis = ImageIO.createImageInputStream(Archivo)) {
				Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
				if (!iter.hasNext()) {
					throw new RuntimeException("No se encontraron lectores!");
				}
				ImageReader reader = iter.next();
				formato = reader.getFormatName();
			}

		} catch (IOException ex) {
			Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
		}
		return formato;
	}

	private void Borrar() {
		try {
			PreparedStatement ps;
			ps = Acciones.CON.prepareStatement("delete from IMAGEN where nombre = '" + lblNombre.getText() + "'");
			Acciones.CON.setAutoCommit(false);

			ps.execute();

			Acciones.CON.commit();

			ps.close();

			JOptionPane.showMessageDialog(null, "Imagen Borrada de la Base de Datos", "Atencion",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (java.lang.NullPointerException | SQLException ex) {
			Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "No se pudo eliminar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public void GuardarEnBd(String nombre) throws SQLException {

		File arch = new File("tmp");
		@SuppressWarnings("unused")
		String formato = lblFormato.getText();

		BufferedImage imagen = new BufferedImage(lblImagen.getWidth(), lblImagen.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics grafico = imagen.getGraphics();
		lblImagen.paint(grafico);
		String aaa;

		try {
//            ImageIO.write(imagen, formato, arch);

			FileInputStream fis;
			fis = new FileInputStream(Archivo);
			PreparedStatement ps = Acciones.CON.prepareStatement("insert into IMAGEN values(?, ?, ?)");

			ps.setString(1, nombre);
			ps.setBinaryStream(2, fis, (int) Archivo.length());
			ps.setString(3, lblFormato.getText());
			aaa = convertStreamToString(fis);
			System.out.print(aaa);
			Acciones.CON.setAutoCommit(false);

			ps.executeUpdate();

			fis.close();
			arch.delete();

			Acciones.CON.commit();

			JOptionPane.showMessageDialog(null, "Archivo Guardado en la Base de Datos", "Atencion",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (java.lang.NullPointerException | IOException e) {
			arch.delete();
//            Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, e);
			// throw new SQLException("No se pudo guardar la imagen.\n" + e);
			JOptionPane.showMessageDialog(null, "No se pudo guardar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
			arch.delete();
		} catch (SQLException e) {
			arch.delete();
//            Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, e);
			// throw new SQLException("No se pudo guardar la imagen.\n" + e);
			JOptionPane.showMessageDialog(null, "Archivo ya Existe en la Base de Datos", "Error",
					JOptionPane.ERROR_MESSAGE);
			arch.delete();
		} catch (HeadlessException e) {
			arch.delete();
//            Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, e);
			// throw new SQLException("No se pudo guardar la imagen.\n" + e);
			JOptionPane.showMessageDialog(null, "Error Guardando el Archivo", "Error", JOptionPane.ERROR_MESSAGE);
			arch.delete();
		}
	}

	public ImageIcon AbrirdeBd() throws SQLException {
		if (Acciones.CON == null) {
			throw new SQLException("Para recuperar una imagen la coneccion no puede ser nula");
		}
		PreparedStatement ps;
		ps = Acciones.CON.prepareStatement("select ARCHIVO, TIPO " + "from IMAGEN " + " where NOMBRE = ?");

		ps.setString(1, BuscaImagen.nombre);

		ResultSet rs;
		rs = ps.executeQuery();
		ImageIcon imagenLeida = new ImageIcon();

		while (rs.next()) {
			archivoLeido = rs.getBytes("archivo");
			imagenLeida = new ImageIcon(archivoLeido);
			lblFormato.setText(rs.getString("tipo"));
		}

		rs.close();
		ps.close();
		return imagenLeida;
	}

	public void AbrirArchivo() {
		// FileFilter tipoImagenes1 = new FileNameExtensionFilter("Archivos de Imagen
		// (.xls)", "xls");
		FileFilter tipoImagenes2 = new FileNameExtensionFilter("Archivos de Imagen", new String[] { "JPG", "jpg", "bmp",
				"BMP", "gif", "GIF", "WBMP", "png", "PNG", "jpeg", "wbmp", "JPEG" });
		JFileChooser AbrirArchivo = new JFileChooser();

		// AbrirArchivo.setCurrentDirectory(new
		// java.io.File("C:\\Users\\Simon\\pictures"));
		// AbrirArchivo.addChoosableFileFilter(tipoImagenes);
		AbrirArchivo.setFileFilter(tipoImagenes2);

		int estado = AbrirArchivo.showOpenDialog(null);

		if (estado == JFileChooser.APPROVE_OPTION) {

			Archivo = AbrirArchivo.getSelectedFile();
			String aux = Archivo.getPath();
			icono = new ImageIcon(aux);

			lblImagen.setIcon(icono);

			int alto = icono.getIconHeight();
			int ancho = icono.getIconWidth();

			anchoOriginal.setText("" + ancho);
			altoOriginal.setText("" + alto);

			lblNombre.setText(Archivo.getName());
			lblRuta.setText(Archivo.getParent());

			lblFormato.setText(ObtenerFormato(Archivo).toUpperCase());
		}
	}

	public void Original() {
		lblImagen.setIcon(new ImageIcon(ima.getScaledInstance(Integer.parseInt(anchoOriginal.getText()),
				Integer.parseInt(altoOriginal.getText()), Image.SCALE_FAST)));

	}

	public void GuardarArchivo() {
		JFileChooser GuardarArchivo = new javax.swing.JFileChooser();
		// GuardarArchivo.setCurrentDirectory(new
		// java.io.File("C:\\Users\\Simon\\pictures"));
		GuardarArchivo.setCurrentDirectory(new java.io.File("/"));

		GuardarArchivo.setAcceptAllFileFilterUsed(false);
		FileFilter tipoImagenes2 = new FileNameExtensionFilter("Archivos de Imagen", new String[] { "JPG", "jpg", "bmp",
				"BMP", "gif", "GIF", "WBMP", "png", "PNG", "jpeg", "wbmp", "JPEG" });
		// javax.swing.filechooser.FileFilter filtro1 = new
		// FileNameExtensionFilter("Imagenes (jpg, jpeg)", "jpg", "jpeg");
		GuardarArchivo.setFileFilter(tipoImagenes2);

		String ruta = "";

		if (GuardarArchivo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			ruta = GuardarArchivo.getSelectedFile().getAbsolutePath();

//            System.out.print(ruta);
			File fichero = new File(ruta + "." + lblFormato.getText());
			String formato = lblFormato.getText();

			BufferedImage imagen = new BufferedImage(lblImagen.getWidth(), lblImagen.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			Graphics grafico = imagen.getGraphics();
			lblImagen.paint(grafico);

			if (new File(ruta + "." + lblFormato.getText()).exists()) {
				if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this,
						"El Archivo existe,desea reemplazarlo?", "Reemplazar", JOptionPane.YES_NO_OPTION)) {
					try {
						ImageIO.write(imagen, formato, fichero);
					} catch (IOException ex) {
//                        Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

			} else {

				try {
					ImageIO.write(imagen, formato, fichero);
				} catch (IOException ex) {
//                    Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	private void GuardarArchivo2() {
		JFileChooser GuardarArchivo2 = new javax.swing.JFileChooser();
		GuardarArchivo2.setSelectedFile(new File(lblNombre.getText()));
		GuardarArchivo2.setCurrentDirectory(new java.io.File("/"));

		GuardarArchivo2.setAcceptAllFileFilterUsed(false);
		FileFilter tipoImagenes2 = new FileNameExtensionFilter("Archivos de Imagen", new String[] { "JPG", "jpg", "bmp",
				"BMP", "gif", "GIF", "WBMP", "png", "PNG", "jpeg", "wbmp", "JPEG" });
		GuardarArchivo2.setFileFilter(tipoImagenes2);

		String ruta = "";

		if (GuardarArchivo2.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			ruta = GuardarArchivo2.getSelectedFile().getAbsolutePath();

			@SuppressWarnings("unused")
			File ArchivoNuevo = new File(ruta + "." + lblFormato.getText());
			@SuppressWarnings("unused")
			String formato = lblFormato.getText();

			BufferedImage imagen = new BufferedImage(lblImagen.getWidth(), lblImagen.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			Graphics grafico = imagen.getGraphics();
			lblImagen.paint(grafico);

			if (new File(ruta + "." + lblFormato.getText()).exists()) {
				if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this,
						"El Archivo existe,desea reemplazarlo?", "Reemplazar", JOptionPane.YES_NO_OPTION)) {
					try {
						FileUtils.writeByteArrayToFile(new File(ruta + "." + lblFormato.getText()), archivoLeido);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, "Error Guardando el Archivo\n" + ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
//                        Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			} else {
				try {
					FileUtils.writeByteArrayToFile(new File(ruta + "." + lblFormato.getText()), archivoLeido);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, "Error Guardando el Archivo\n" + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
//                    Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jSeparator5 = new javax.swing.JSeparator();
		jScrollPane1 = new javax.swing.JScrollPane();
		lblImagen = new javax.swing.JLabel();
		panelAcciones = new javax.swing.JPanel();
		botonAbrirDeArchivo = new javax.swing.JButton();
		botonDeshacerAjuste = new javax.swing.JButton();
		botonAjustarTamano = new javax.swing.JButton();
		botonGuardarEnBD = new javax.swing.JButton();
		botonAbrirDeBD = new javax.swing.JButton();
		botonGuardarEnArchivo = new javax.swing.JButton();
		botonCopiarVentana = new javax.swing.JButton();
		botonSalir = new javax.swing.JButton();
		botonCapturaVentana = new javax.swing.JButton();
		botonImprimirListado = new javax.swing.JButton();
		botonBorrarDeBD = new javax.swing.JButton();
		botonLimpiar = new javax.swing.JButton();
		botonImprimirActual = new javax.swing.JButton();
		jLabel9 = new javax.swing.JLabel();
		comboBaseDatos = new javax.swing.JComboBox();
		jSeparator1 = new javax.swing.JSeparator();
		jSeparator2 = new javax.swing.JSeparator();
		jSeparator3 = new javax.swing.JSeparator();
		jSeparator4 = new javax.swing.JSeparator();
		jSeparator6 = new javax.swing.JSeparator();
		jSeparator7 = new javax.swing.JSeparator();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		panelDatosImagen = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		anchoOriginal = new javax.swing.JLabel();
		anchoAjustado = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		lblFactor = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		altoOriginal = new javax.swing.JLabel();
		altoAjustado = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		lblNombre = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		lblRuta = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		jLabel14 = new javax.swing.JLabel();
		lblFormato = new javax.swing.JLabel();
		panelDatosBaseDatos = new javax.swing.JPanel();
		jLabel10 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		campoGestor = new javax.swing.JLabel();
		campoVersion = new javax.swing.JLabel();
		campoBaseDatos = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		campoUsuario = new javax.swing.JLabel();
		jLabel15 = new javax.swing.JLabel();
		campoJava = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Imagenes... Rafael Simon Vera");
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent evt) {
				formWindowClosed(evt);
			}
		});

		lblImagen.setBackground(new java.awt.Color(255, 255, 255));
		lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblImagen.setOpaque(true);
		jScrollPane1.setViewportView(lblImagen);

		panelAcciones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Acciones",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Segoe UI", 0, 12), java.awt.Color.blue)); // NOI18N

		botonAbrirDeArchivo.setBackground(new java.awt.Color(255, 255, 0));
		botonAbrirDeArchivo.setText("Abrir de Archivo");
		botonAbrirDeArchivo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonAbrirDeArchivoActionPerformed(evt);
			}
		});

		botonDeshacerAjuste.setBackground(new java.awt.Color(255, 0, 255));
		botonDeshacerAjuste.setText("Deshacer Ajuste");
		botonDeshacerAjuste.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonDeshacerAjusteActionPerformed(evt);
			}
		});

		botonAjustarTamano.setBackground(new java.awt.Color(255, 0, 255));
		botonAjustarTamano.setText("Ajustar Tamaño");
		botonAjustarTamano.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonAjustarTamanoActionPerformed(evt);
			}
		});

		botonGuardarEnBD.setBackground(new java.awt.Color(0, 0, 255));
		botonGuardarEnBD.setText("Guardar en BD");
		botonGuardarEnBD.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonGuardarEnBDActionPerformed(evt);
			}
		});

		botonAbrirDeBD.setBackground(new java.awt.Color(0, 0, 255));
		botonAbrirDeBD.setText("Abrir de BD");
		botonAbrirDeBD.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonAbrirDeBDActionPerformed(evt);
			}
		});

		botonGuardarEnArchivo.setBackground(new java.awt.Color(255, 255, 0));
		botonGuardarEnArchivo.setText("Guardar a Archivo");
		botonGuardarEnArchivo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonGuardarEnArchivoActionPerformed(evt);
			}
		});

		botonCopiarVentana.setBackground(new java.awt.Color(0, 204, 204));
		botonCopiarVentana.setText("Copiar Ventana");

		botonSalir.setBackground(new java.awt.Color(255, 0, 0));
		botonSalir.setText("Salir");
		botonSalir.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonSalirActionPerformed(evt);
			}
		});

		botonCapturaVentana.setBackground(new java.awt.Color(0, 204, 204));
		botonCapturaVentana.setText("Captura Ventana");
		botonCapturaVentana.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonCapturaVentanaActionPerformed(evt);
			}
		});

		botonImprimirListado.setBackground(new java.awt.Color(255, 102, 0));
		botonImprimirListado.setText("Imprimir Listado");
		botonImprimirListado.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonImprimirListadoActionPerformed(evt);
			}
		});

		botonBorrarDeBD.setBackground(new java.awt.Color(0, 0, 255));
		botonBorrarDeBD.setText("Borrar de BD");
		botonBorrarDeBD.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonBorrarDeBDActionPerformed(evt);
			}
		});

		botonLimpiar.setBackground(new java.awt.Color(0, 255, 0));
		botonLimpiar.setText("Limpiar");
		botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonLimpiarActionPerformed(evt);
			}
		});

		botonImprimirActual.setBackground(new java.awt.Color(255, 102, 0));
		botonImprimirActual.setText("Imprimir Actual");
		botonImprimirActual.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonImprimirActualActionPerformed(evt);
			}
		});

		jLabel9.setText("Seleccione Base de Datos");

		comboBaseDatos.setBackground(new java.awt.Color(255, 51, 255));
		comboBaseDatos.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "", "PostgreSQL", "SQLite", "H2", "MySQL 8.0", "Web" }));
		comboBaseDatos.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				comboBaseDatosActionPerformed(evt);
			}
		});

		jButton1.setBackground(new java.awt.Color(255, 0, 255));
		jButton1.setText("Usuario");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("jButton2");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout panelAccionesLayout = new javax.swing.GroupLayout(panelAcciones);
		panelAcciones.setLayout(panelAccionesLayout);
		panelAccionesLayout.setHorizontalGroup(panelAccionesLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelAccionesLayout.createSequentialGroup().addContainerGap().addGroup(panelAccionesLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jSeparator6)
						.addComponent(comboBaseDatos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jSeparator4).addComponent(jSeparator3).addComponent(jSeparator1)
						.addComponent(jSeparator2)
						.addGroup(panelAccionesLayout.createSequentialGroup().addGroup(panelAccionesLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 147,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(botonAbrirDeArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(botonGuardarEnArchivo, javax.swing.GroupLayout.PREFERRED_SIZE,
												134, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(botonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(botonCopiarVentana, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(botonCapturaVentana, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(botonAjustarTamano, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(botonDeshacerAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(botonImprimirActual, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(botonImprimirListado, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addComponent(botonAbrirDeBD, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(botonBorrarDeBD, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGap(0, 0, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								panelAccionesLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
										.addComponent(botonGuardarEnBD, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(65, 65, 65).addComponent(jButton2)))
						.addContainerGap()));
		panelAccionesLayout.setVerticalGroup(panelAccionesLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelAccionesLayout.createSequentialGroup()
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel9).addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE,
										19, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(comboBaseDatos, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 2,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(botonAbrirDeArchivo, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(botonGuardarEnArchivo, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(botonAjustarTamano, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(botonDeshacerAjuste, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(botonCopiarVentana, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(botonCapturaVentana, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(botonImprimirActual, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(botonImprimirListado, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addGroup(panelAccionesLayout.createSequentialGroup()
										.addGroup(panelAccionesLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(botonAbrirDeBD, javax.swing.GroupLayout.PREFERRED_SIZE,
														23, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(botonBorrarDeBD, javax.swing.GroupLayout.PREFERRED_SIZE,
														23, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jButton2))
								.addComponent(botonGuardarEnBD, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(6, 6, 6)
						.addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(botonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		panelDatosImagen.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos de la Imagen",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Segoe UI", 0, 12), java.awt.Color.blue)); // NOI18N

		jLabel3.setText("Ajustado:");

		jLabel2.setText("Original:");

		anchoOriginal.setBackground(new java.awt.Color(255, 255, 255));
		anchoOriginal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		anchoOriginal.setOpaque(true);

		anchoAjustado.setBackground(new java.awt.Color(255, 255, 255));
		anchoAjustado.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		anchoAjustado.setOpaque(true);

		jLabel6.setText("Factor:");

		lblFactor.setBackground(new java.awt.Color(255, 255, 255));
		lblFactor.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lblFactor.setOpaque(true);

		jLabel4.setText("Ancho:");

		jLabel5.setText("Alto:");

		altoOriginal.setBackground(new java.awt.Color(255, 255, 255));
		altoOriginal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		altoOriginal.setOpaque(true);

		altoAjustado.setBackground(new java.awt.Color(255, 255, 255));
		altoAjustado.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		altoAjustado.setOpaque(true);

		jLabel7.setText("Nombre:");

		lblNombre.setBackground(new java.awt.Color(255, 255, 255));
		lblNombre.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lblNombre.setOpaque(true);

		jLabel8.setText("Ruta:");

		lblRuta.setBackground(new java.awt.Color(255, 255, 255));
		lblRuta.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lblRuta.setOpaque(true);

		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/tierra-40.gif"))); // NOI18N

		jLabel14.setText("Formato:");

		lblFormato.setBackground(new java.awt.Color(255, 255, 255));
		lblFormato.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lblFormato.setOpaque(true);

		javax.swing.GroupLayout panelDatosImagenLayout = new javax.swing.GroupLayout(panelDatosImagen);
		panelDatosImagen.setLayout(panelDatosImagenLayout);
		panelDatosImagenLayout.setHorizontalGroup(panelDatosImagenLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelDatosImagenLayout.createSequentialGroup().addContainerGap()
						.addGroup(panelDatosImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(panelDatosImagenLayout.createSequentialGroup().addGroup(panelDatosImagenLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addGroup(panelDatosImagenLayout.createSequentialGroup()
												.addGroup(panelDatosImagenLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(jLabel5,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.PREFERRED_SIZE, 45,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.PREFERRED_SIZE, 45,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(panelDatosImagenLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(anchoOriginal,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.PREFERRED_SIZE, 90,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(altoOriginal,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.PREFERRED_SIZE, 90,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
												javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(18, 18, 18)
										.addGroup(panelDatosImagenLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(panelDatosImagenLayout.createSequentialGroup()
														.addComponent(altoAjustado,
																javax.swing.GroupLayout.PREFERRED_SIZE, 90,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
																45, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addGroup(panelDatosImagenLayout.createSequentialGroup()
														.addGroup(panelDatosImagenLayout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(jLabel3,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 90,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGroup(panelDatosImagenLayout.createSequentialGroup()
																		.addComponent(anchoAjustado,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				90,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(jLabel6,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				45,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(lblFactor,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				102,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(jLabel14,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				52,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(lblFormato,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				69,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGap(0, 0, Short.MAX_VALUE))))
								.addGroup(panelDatosImagenLayout.createSequentialGroup()
										.addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 45,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(lblRuta, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGap(13, 13, 13).addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		panelDatosImagenLayout.setVerticalGroup(panelDatosImagenLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelDatosImagenLayout.createSequentialGroup().addGroup(panelDatosImagenLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1)
						.addGroup(panelDatosImagenLayout.createSequentialGroup().addGroup(panelDatosImagenLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16,
										javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelDatosImagenLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(panelDatosImagenLayout.createSequentialGroup()
												.addGroup(panelDatosImagenLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(anchoAjustado,
																javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(anchoOriginal,
																javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblFactor, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblFormato,
																javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(panelDatosImagenLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(altoAjustado,
																javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(altoOriginal,
																javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(panelDatosImagenLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblRuta, javax.swing.GroupLayout.PREFERRED_SIZE,
																23, javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		panelDatosBaseDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Propiedades Base de Datos",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Segoe UI", 0, 12), java.awt.Color.blue)); // NOI18N

		jLabel10.setText("Gestor:");

		jLabel11.setText("Version:");

		jLabel12.setText("Base de Datos:");

		campoBaseDatos.setMaximumSize(new java.awt.Dimension(14, 192));
		campoBaseDatos.setMinimumSize(new java.awt.Dimension(14, 192));
		campoBaseDatos.setPreferredSize(new java.awt.Dimension(14, 192));

		jLabel13.setText("Usuario:");

		jLabel15.setText("Java Version:");

		javax.swing.GroupLayout panelDatosBaseDatosLayout = new javax.swing.GroupLayout(panelDatosBaseDatos);
		panelDatosBaseDatos.setLayout(panelDatosBaseDatosLayout);
		panelDatosBaseDatosLayout.setHorizontalGroup(panelDatosBaseDatosLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelDatosBaseDatosLayout.createSequentialGroup().addContainerGap()
						.addGroup(panelDatosBaseDatosLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
								.addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								panelDatosBaseDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(campoGestor, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(campoVersion, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(campoUsuario, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(campoJava, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(campoBaseDatos, javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.PREFERRED_SIZE, 192,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));
		panelDatosBaseDatosLayout.setVerticalGroup(panelDatosBaseDatosLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosBaseDatosLayout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(panelDatosBaseDatosLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(campoGestor, javax.swing.GroupLayout.PREFERRED_SIZE, 14,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelDatosBaseDatosLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel11)
								.addComponent(campoVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 14,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelDatosBaseDatosLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(campoBaseDatos, javax.swing.GroupLayout.PREFERRED_SIZE, 14,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelDatosBaseDatosLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(campoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 14,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelDatosBaseDatosLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(campoJava, javax.swing.GroupLayout.PREFERRED_SIZE, 14,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(panelDatosBaseDatos, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(panelAcciones, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addComponent(panelDatosImagen, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(panelAcciones, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(panelDatosBaseDatos, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(jScrollPane1))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(panelDatosImagen, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void botonAbrirDeArchivoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonAbrirDeArchivoActionPerformed
		Limpiar();
		AbrirArchivo();
//        System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
	}// GEN-LAST:event_botonAbrirDeArchivoActionPerformed

	private void botonAjustarTamanoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonAjustarTamanoActionPerformed
		if (lblImagen.getIcon() != null) {
			Ajustar();
			ajustado = true;
		} else {
			JOptionPane.showMessageDialog(null, "No hay Imagen para Ajustar", "Error", JOptionPane.ERROR_MESSAGE);

		}

	}// GEN-LAST:event_botonAjustarTamanoActionPerformed

	private void botonSalirActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonSalirActionPerformed
		System.exit(0);
	}// GEN-LAST:event_botonSalirActionPerformed

	private void botonDeshacerAjusteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonDeshacerAjusteActionPerformed
		try {
			anchoAjustado.setText("");
			altoAjustado.setText("");
			lblFactor.setText("");

			Original();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No ha hecho ningun Ajuste", "Error", JOptionPane.ERROR_MESSAGE);
		}
		ajustado = false;
	}// GEN-LAST:event_botonDeshacerAjusteActionPerformed

	private void botonGuardarEnBDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonGuardarEnBDActionPerformed
		try {
			if (lblImagen.getIcon() != null) {
				GuardarEnBd(lblNombre.getText());
			} else {
				JOptionPane.showMessageDialog(null, "No hay nada que Guardar", "Error", JOptionPane.ERROR_MESSAGE);

			}
		} catch (SQLException ex) {
//            Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}// GEN-LAST:event_botonGuardarEnBDActionPerformed

	private void botonAbrirDeBDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonAbrirDeBDActionPerformed
		new BuscaImagen(new javax.swing.JFrame(), true).setVisible(true);
		if (BuscaImagen.nombre.equals("")) {
		} else {
			try {
				Limpiar();
				ImageIcon leida = AbrirdeBd();

				icono = leida;

				lblImagen.setIcon(leida);
				lblNombre.setText(BuscaImagen.nombre);
				lblRuta.setText("Esta Imagen se encuentra Guardada en la Base de Datos");

				int alto = leida.getIconHeight();
				int ancho = leida.getIconWidth();

				anchoOriginal.setText(ancho + "");
				altoOriginal.setText(alto + "");

			} catch (SQLException ex) {
//                Logger.getLogger(ImagenSQLite1.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}// GEN-LAST:event_botonAbrirDeBDActionPerformed

	private void botonGuardarEnArchivoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonGuardarEnArchivoActionPerformed
		if (!lblNombre.getText().equals("")) {
			GuardarArchivo2();
		} else {
			JOptionPane.showMessageDialog(null, "No hay nada que Guardar", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}// GEN-LAST:event_botonGuardarEnArchivoActionPerformed

	private void botonCapturaVentanaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonCapturaVentanaActionPerformed

		JFileChooser GuardarArchivo = new javax.swing.JFileChooser();
		// GuardarArchivo.setCurrentDirectory(new
		// java.io.File("C:\\Users\\Simon\\pictures"));

		GuardarArchivo.setAcceptAllFileFilterUsed(false);
		javax.swing.filechooser.FileFilter filtro1 = new FileNameExtensionFilter("Imagenes (jpg, jpeg)", "jpg", "jpeg");
		GuardarArchivo.addChoosableFileFilter(filtro1);

		String ruta = "";

		if (GuardarArchivo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			ruta = GuardarArchivo.getSelectedFile().getAbsolutePath();

			String formato = "jpg";
			File arch = new File(ruta + "." + formato);

			BufferedImage imagen2 = new BufferedImage(this.getContentPane().getWidth(),
					this.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics g = imagen2.getGraphics();
			this.getContentPane().paint(g);

			try {
				ImageIO.write(imagen2, formato, arch);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "No se puede Guardar el archivo", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}// GEN-LAST:event_botonCapturaVentanaActionPerformed

	private void botonImprimirListadoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonImprimirListadoActionPerformed
		if (!comboBaseDatos.getSelectedItem().toString().isEmpty()) {
			acciones.imprimir2(null, "reportes/listado.jasper", "Lista de Imagenes", "/imagenes/note4.png");
		} else {
			JOptionPane.showMessageDialog(null, "No ha Seleccionado una Base de Datos", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}// GEN-LAST:event_botonImprimirListadoActionPerformed

	private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonLimpiarActionPerformed
		Limpiar();
	}// GEN-LAST:event_botonLimpiarActionPerformed

	private void botonBorrarDeBDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonBorrarDeBDActionPerformed
		if (lblImagen.getIcon() != null) {
			Borrar();
		} else {
			JOptionPane.showMessageDialog(null, "No hay nada que Borrar", "Error", JOptionPane.ERROR_MESSAGE);

		}
	}// GEN-LAST:event_botonBorrarDeBDActionPerformed

	private void botonImprimirActualActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonImprimirActualActionPerformed
		if (lblImagen.getIcon() != null) {
			acciones.nombreimg = lblNombre.getText();
			acciones.imprimir2(null, "reportes/reporte.jasper", "Impresion de Imagenes", "/imagenes/note4.png");
		} else {
			JOptionPane.showMessageDialog(null, "No hay Imagen que Imprimir", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}// GEN-LAST:event_botonImprimirActualActionPerformed

	private void comboBaseDatosActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_comboBaseDatosActionPerformed
		Limpiar();
		String seleccion = comboBaseDatos.getSelectedItem().toString();
		if (!seleccion.isEmpty()) {
			Acciones.SeleccionDB(seleccion);
		}
		String usuario = Usuario.usuario;
		String clave = Usuario.pass;
		// H2 usuario: TEST clave: test
		// PostgreSQL usuario: 1 clave: 1
		try {
			if (!usuario.isEmpty() && !clave.isEmpty()) {
				switch (seleccion) {
				case "PostgreSQL" -> Acciones.conexionPostgres(usuario, clave);
				case "SQLite" -> Acciones.ConexionSQLite(usuario, clave);
				case "H2" -> Acciones.ConexionH2(usuario, clave);
				case "MySQL 8.0" -> Acciones.ConexionMysql(usuario, clave);
				case "" -> {
					Acciones.Desconectar();
					Limpiar();
				}
				}
			}

		} catch (java.lang.NullPointerException nullPointerException) {
			JOptionPane.showMessageDialog(null, "No hay Usuario Para Conectar", "Error", JOptionPane.ERROR_MESSAGE);
			comboBaseDatos.setSelectedIndex(0);
		}
		info();
		Acciones.crearBaseTablas();
	}// GEN-LAST:event_comboBaseDatosActionPerformed

	private void formWindowClosed(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosed
		System.exit(0);
	}// GEN-LAST:event_formWindowClosed

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
		new Usuario(new javax.swing.JFrame(), true).setVisible(true);
	}// GEN-LAST:event_jButton1ActionPerformed

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
		String aaa = Acciones.generateInsertStatement("IMAGEN", lblNombre.getText(), lblRuta.getText());
		System.out.print(aaa);

	}// GEN-LAST:event_jButton2ActionPerformed

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new ImagenSQLite1().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel altoAjustado;
	private javax.swing.JLabel altoOriginal;
	private javax.swing.JLabel anchoAjustado;
	private javax.swing.JLabel anchoOriginal;
	private javax.swing.JButton botonAbrirDeArchivo;
	private javax.swing.JButton botonAbrirDeBD;
	private javax.swing.JButton botonAjustarTamano;
	private javax.swing.JButton botonBorrarDeBD;
	private javax.swing.JButton botonCapturaVentana;
	private javax.swing.JButton botonCopiarVentana;
	private javax.swing.JButton botonDeshacerAjuste;
	private javax.swing.JButton botonGuardarEnArchivo;
	private javax.swing.JButton botonGuardarEnBD;
	private javax.swing.JButton botonImprimirActual;
	private javax.swing.JButton botonImprimirListado;
	private javax.swing.JButton botonLimpiar;
	private javax.swing.JButton botonSalir;
	private javax.swing.JLabel campoBaseDatos;
	private javax.swing.JLabel campoGestor;
	private javax.swing.JLabel campoJava;
	private javax.swing.JLabel campoUsuario;
	private javax.swing.JLabel campoVersion;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox comboBaseDatos;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JSeparator jSeparator4;
	@SuppressWarnings("unused")
	private javax.swing.JSeparator jSeparator5;
	private javax.swing.JSeparator jSeparator6;
	private javax.swing.JSeparator jSeparator7;
	private javax.swing.JLabel lblFactor;
	private javax.swing.JLabel lblFormato;
	private javax.swing.JLabel lblImagen;
	private javax.swing.JLabel lblNombre;
	private javax.swing.JLabel lblRuta;
	private javax.swing.JPanel panelAcciones;
	private javax.swing.JPanel panelDatosBaseDatos;
	private javax.swing.JPanel panelDatosImagen;
	// End of variables declaration//GEN-END:variables
}
