package org.tastefuljava.gianadda.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.tastefuljava.gianadda.ui.map.TileManager;
import org.tastefuljava.gianadda.util.Configuration;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            try (InputStream in = new FileInputStream("logging.properties")) {
                LogManager.getLogManager().readConfiguration(in);
            }
            Configuration conf = Configuration.load(
                    new File("gianadda-ui.properties"));
            final TileManager manager = new TileManager(conf);
            manager.start();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainFrame frame = new MainFrame(manager);
                    frame.setVisible(true);
                }
            });
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
