package org.tastefuljava.gianadda.ui;

import org.tastefuljava.gianadda.ui.map.TileManager;
import org.tastefuljava.gianadda.util.Util;

public class MainFrame extends javax.swing.JFrame {

    public MainFrame(TileManager manager) {
        this();
        mapView.setManager(manager);
    }

    public MainFrame() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mapView = new org.tastefuljava.gianadda.ui.map.MapView();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        zoom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        latitude = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        longitude = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(mapView, java.awt.BorderLayout.CENTER);

        jPanel1.setMaximumSize(new java.awt.Dimension(120, 32767));
        jPanel1.setPreferredSize(new java.awt.Dimension(120, 300));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Zoom");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        jPanel1.add(jLabel1, gridBagConstraints);

        zoom.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        jPanel1.add(zoom, gridBagConstraints);

        jLabel2.setText("Latitude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        jPanel1.add(jLabel2, gridBagConstraints);

        latitude.setText("41");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        jPanel1.add(latitude, gridBagConstraints);

        jLabel3.setText("Longitude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        jPanel1.add(jLabel3, gridBagConstraints);

        longitude.setText("7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        jPanel1.add(longitude, gridBagConstraints);

        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new java.awt.GridBagConstraints());

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int z = Integer.parseInt(zoom.getText());
        double lat = Util.parseDouble(latitude.getText());
        double lng = Util.parseDouble(longitude.getText());
        mapView.setView(z, lat, lng);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField latitude;
    private javax.swing.JTextField longitude;
    private org.tastefuljava.gianadda.ui.map.MapView mapView;
    private javax.swing.JTextField zoom;
    // End of variables declaration//GEN-END:variables
}
