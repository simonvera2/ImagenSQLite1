package ImagenSQLite1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("unused")
public class BuscaImagen extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	public static String nombre = "";

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				BuscaImagen dialog = new BuscaImagen(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {

					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	public int nfila = 0;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JTable tablaImagenes;
	// End of variables declaration//GEN-END:variables

	public BuscaImagen(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		this.setLocationRelativeTo(null);
	}

	private void borrartabla() {
		DefaultTableModel modelo = (DefaultTableModel) tablaImagenes.getModel();
		while (modelo.getRowCount() > 0) {
			modelo.removeRow(0);
		}
	}

	private void cargar() {
		DefaultTableModel modelo = (DefaultTableModel) tablaImagenes.getModel();
		tablaImagenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		Object[] fila = new Object[1];
		tablaImagenes.getTableHeader().setReorderingAllowed(false);
		tablaImagenes.setAutoCreateRowSorter(false);

		ResultSet rs = null;
		PreparedStatement ps;
		try {
			ps = Acciones.CON.prepareStatement("select * from IMAGEN ORDER BY nombre ASC ");
			rs = ps.executeQuery();
			while (rs.next()) {
				fila[0] = rs.getString("nombre");
				modelo.addRow(fila);
				this.tablaImagenes.setModel(modelo);
				jScrollPane1.setViewportView(tablaImagenes);
			}

		} catch (java.lang.NullPointerException | SQLException ex) {
//            Logger.getLogger(BuscaImagen.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
		}
		try {
			rs.close();
		} catch (java.lang.NullPointerException | SQLException ex) {
//            Logger.getLogger(BuscaImagen.class.getName()).log(Level.SEVERE, null, ex);

		}
	}

	private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
		nombre = "";
		cargar();
	}// GEN-LAST:event_formWindowOpened
		// <editor-fold defaultstate="collapsed" desc="Generated
		// Code">//GEN-BEGIN:initComponents

	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		tablaImagenes = new javax.swing.JTable();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Buscar Imagen");
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		tablaImagenes.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
		tablaImagenes.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "Imagen" }) {
			private static final long serialVersionUID = 1L;
			boolean[] canEdit = new boolean[] { false };

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		tablaImagenes.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tablaImagenesMouseClicked(evt);
			}
		});
		jScrollPane1.setViewportView(tablaImagenes);
		if (tablaImagenes.getColumnModel().getColumnCount() > 0) {
			tablaImagenes.getColumnModel().getColumn(0).setResizable(false);
			tablaImagenes.getColumnModel().getColumn(0).setPreferredWidth(110);
		}

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void tablaImagenesMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tablaImagenesMouseClicked
		DefaultTableModel modelo = (DefaultTableModel) tablaImagenes.getModel();
		nfila = tablaImagenes.getSelectedRow();
		if (evt.getClickCount() == 2) {
			if (nombre.equals("")) {
				nombre = modelo.getValueAt(nfila, 0).toString();
				dispose();
			}
		}
	}// GEN-LAST:event_tablaImagenesMouseClicked
}
