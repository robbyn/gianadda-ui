package org.tastefuljava.gianadda.ui;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.tastefuljava.gianadda.geo.GpxReader;
import org.tastefuljava.gianadda.geo.TrackPoint;
import org.tastefuljava.gianadda.ui.map.TileManager;
import org.tastefuljava.gianadda.util.Util;

public class MainFrame extends javax.swing.JFrame {
    private JFileChooser trackChooser = createTrackChooser();

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
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        loadTrack = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mapView.setMinimumSize(new java.awt.Dimension(256, 256));
        getContentPane().add(mapView, java.awt.BorderLayout.CENTER);

        jPanel1.setMaximumSize(new java.awt.Dimension(120, 32767));
        jPanel1.setPreferredSize(new java.awt.Dimension(120, 300));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Zoom");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        jPanel1.add(jLabel1, gridBagConstraints);

        zoom.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        jPanel1.add(zoom, gridBagConstraints);

        jLabel2.setText("Latitude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        jPanel1.add(jLabel2, gridBagConstraints);

        latitude.setText("41");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        jPanel1.add(latitude, gridBagConstraints);

        jLabel3.setText("Longitude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        jPanel1.add(jLabel3, gridBagConstraints);

        longitude.setText("7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        jPanel1.add(longitude, gridBagConstraints);

        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        jPanel1.add(jButton1, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        fileMenu.setText("File");

        loadTrack.setText("Load track...");
        loadTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadTrackActionPerformed(evt);
            }
        });
        fileMenu.add(loadTrack);

        menuBar.add(fileMenu);

        helpMenu.setText("Help");
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int z = Integer.parseInt(zoom.getText());
        double lat = Util.parseDouble(latitude.getText());
        double lng = Util.parseDouble(longitude.getText());
        mapView.setView(z, lat, lng);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void loadTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadTrackActionPerformed
        try {
            int returnVal = trackChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                TrackPoint[] points = GpxReader.readTrack(
                        trackChooser.getSelectedFile());
                mapView.addTrack(points);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Exception was raised", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_loadTrackActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField latitude;
    private javax.swing.JMenuItem loadTrack;
    private javax.swing.JTextField longitude;
    private org.tastefuljava.gianadda.ui.map.MapView mapView;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextField zoom;
    // End of variables declaration//GEN-END:variables

    private JFileChooser createTrackChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Track files (*.gpx)";
            }

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else if (!file.isFile()) {
                    return false;
                } else {
                    String name = file.getName().toLowerCase();
                    return name.endsWith(".gpx");
                }
            }
        });
        chooser.setDialogTitle("Select image");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        return chooser;
    }
}
