package ImagenSQLite1;

public class Usuario extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	public static String usuario;
	public static String pass;

	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(() -> {
			Usuario dialog = new Usuario(new javax.swing.JFrame(), true);
			dialog.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
			});
			dialog.setVisible(true);
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton botonAceptar;

	private javax.swing.JButton botonLimpiar;

	private javax.swing.JButton botonSalir;

	private javax.swing.JPasswordField campoClave;

	private javax.swing.JTextField campoUsuario;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel panelDatos;

	// End of variables declaration//GEN-END:variables
	public Usuario(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		this.setLocationRelativeTo(null);
	}

	private void botonAceptarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonAceptarActionPerformed
		usuario = campoUsuario.getText();
		pass = "";
		char[] pw = campoClave.getPassword();
		for (char element : pw) {
			pass = pass + element;
		}
		dispose();
	}// GEN-LAST:event_botonAceptarActionPerformed

	private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonLimpiarActionPerformed
		campoUsuario.setText("");
		campoClave.setText("");
	}// GEN-LAST:event_botonLimpiarActionPerformed

	private void botonSalirActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_botonSalirActionPerformed
		dispose();
	}// GEN-LAST:event_botonSalirActionPerformed

	private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
		campoUsuario.setText(usuario);
		campoClave.setText(pass);
	}// GEN-LAST:event_formWindowOpened
		// <editor-fold defaultstate="collapsed" desc="Generated
		// Code">//GEN-BEGIN:initComponents

	@SuppressWarnings("deprecation")
	private void initComponents() {

		panelDatos = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		campoClave = new javax.swing.JPasswordField();
		campoUsuario = new javax.swing.JTextField();
		botonAceptar = new javax.swing.JButton();
		botonSalir = new javax.swing.JButton();
		botonLimpiar = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Datos de Usuario");
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		panelDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos de Usuario",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 0, 11), java.awt.Color.blue)); // NOI18N

		jLabel1.setText("Usuario:");

		jLabel2.setText("Contraseña:");

		campoClave.setNextFocusableComponent(botonAceptar);

		campoUsuario.setNextFocusableComponent(campoClave);

		javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
		panelDatos.setLayout(panelDatosLayout);
		panelDatosLayout.setHorizontalGroup(panelDatosLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelDatosLayout.createSequentialGroup().addContainerGap()
						.addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(campoUsuario)
								.addComponent(campoClave, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
						.addContainerGap()));
		panelDatosLayout.setVerticalGroup(panelDatosLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelDatosLayout.createSequentialGroup()
						.addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(campoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(campoClave, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		botonAceptar.setText("Aceptar");
		botonAceptar.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonAceptarActionPerformed(evt);
			}
		});

		botonSalir.setText("Salir");
		botonSalir.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonSalirActionPerformed(evt);
			}
		});

		botonLimpiar.setText("Limpiar");
		botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				botonLimpiarActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(botonAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(botonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(26, 26, 26).addComponent(botonSalir,
												javax.swing.GroupLayout.PREFERRED_SIZE, 90,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(botonAceptar).addComponent(botonSalir).addComponent(botonLimpiar))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents
}
