package org.tastefuljava.gianadda.ui.map;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.tastefuljava.gianadda.geo.LatLng;
import org.tastefuljava.gianadda.ui.util.ListenerList;
import org.tastefuljava.gianadda.util.Configuration;
import org.tastefuljava.gianadda.util.Files;
import org.tastefuljava.gianadda.util.Util;

public class TileManager implements Closeable {
    private static final Logger LOG
            = Logger.getLogger(TileManager.class.getName());

    private final Configuration conf;
    private final File baseDir;
    private final Dimension tileSize;
    private final BlockingQueue<TileRequest> requestQueue;
    private final ListenerList listeners = new ListenerList();
    private final TileListener notifier
            = listeners.getNotifier(TileListener.class);
    private TileServer[] servers;

    public TileManager(Configuration conf) throws IOException {
        this.conf = conf;
        String base = conf.getString("tiles.base-dir", null);
        if (base != null) {
            baseDir = new File(base);
        } else {
            File confDir = new File(
                    System.getProperty("user.home"), ".gianadda-ui");
            baseDir = new File(confDir, "tiles");
        }
        if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
            throw new IOException("Could not create dir " + this.baseDir);
        }
        tileSize = conf.getDimension("tiles.tile-size", new Dimension(256,256));
        requestQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    public Configuration getConf() {
        return conf;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public int getTileWidth() {
        return tileSize.width;
    }

    public int getTileHeight() {
        return tileSize.height;
    }

    public Dimension getTileSize() {
        return new Dimension(tileSize);
    }

    public int getRows(int zoom) {
        return 1 << zoom;
    }

    public int getColumns(int zoom) {
        return 1 << zoom;
    }

    public int getWidth(int zoom) {
        return getColumns(zoom)*getTileWidth();
    }

    public int getHeight(int zoom) {
        return getRows(zoom)*getTileHeight();
    }

    public int lat2y(double lat, int zoom) {
        lat = LatLng.normalizeLat(lat);
        int h = getHeight(zoom);
        double rlat = Math.toRadians(lat);
        double ratio = (1 - Math.log(Math.tan(rlat) + 1 / Math.cos(rlat))
                / Math.PI) / 2;
        if (ratio <= 0) {
            return 0;
        } else if (ratio >= 1.0) {
            return h;
        }
        double fy = ratio*h;
        return (int)fy;
    }

    public int lng2x(double lng, int zoom) {
        lng = LatLng.normalizeLng(lng);
        int w = getWidth(zoom);
        double ratio = (lng + 180) / 360;
        if (ratio <= 0) {
            return 0;
        } else if (ratio >= 1.0) {
            return w;
        }
        double fx = ratio * w;
        return (int)fx;
    }

    public double x2lng(int x, int zoom) {
        return col2lng((double)x/getTileWidth(), zoom);
    }

    public double y2lat(int y, int zoom) {
        return row2lat((double)y/getTileHeight(), zoom);
    }

    public static double col2lng(double col, int zoom) {
        return col / (2 << zoom) * 360.0 - 180;
    }

    public static double row2lat(double row, int zoom) {
        double n = Math.PI - (2.0 * Math.PI * row) / (2 << zoom);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    public void start() {
        servers = createServers();
        if (servers != null) {
            for (TileServer server: servers) {
                server.start();
            }
        }
    }

    public void stop() {
        if (servers != null) {
            for (TileServer server: servers) {
                server.interrupt();
            }
            for (TileServer server: servers) {
                try {
                    server.join(3000);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            servers = null;
        }
    }

    public final void addListener(TileListener listener) {
        listeners.addListener(listener);
    }

    public final void removeListener(TileListener listener) {
        listeners.removeListener(listener);
    }

    public BufferedImage getTile(int zoom, int col, int row)
            throws IOException {
        File file = tileFile(zoom, col, row);
        if (file.isFile()) {
            return ImageIO.read(file);
        } else {
            return null;
        }
    }

    public void requestTile(int zoom, int col, int row) throws IOException {
        File file = tileFile(zoom, col, row);
        if (!file.isFile()) {
            LOG.log(Level.INFO, "Request download of c={0}, r={1}, z={2}",
                    new Object[]{col, row, zoom});
            requestQueue.add(new TileRequest(zoom, col, row));
        }
    }

    public void clearQueue() {
        requestQueue.clear();
    }

    private File tileDir(int zoom) {
        return new File(baseDir, Util.formatNumber(zoom, "00"));
    }

    private String tileName(int col, int row) {
        return col + "-" + row;
    }

    File tileFile(int zoom, int col, int row) {
        File dir = tileDir(zoom);
        return new File(dir, tileName(col, row));
    }

    private TileServer[] createServers() {
        List<TileServer> list = new ArrayList<>();
        String s = conf.getString("tiles.server", null);
        if (s != null) {
            list.add(createServer(s));
        }
        int i = 0;
        while (true) {
            ++i;
            s = conf.getString("tiles.server." + i, null);
            if (s == null) {
                break;
            }
            list.add(createServer(s));
        }
        return list.toArray(new TileServer[list.size()]);
    }

    private TileServer createServer(String pattern) {
        return new TileServer(pattern);
    }

    static class TileRequest {
        protected final int zoom;
        protected final int col;
        protected final int row;

        public TileRequest(int zoom, int col, int row) {
            super();
            this.zoom = zoom;
            this.col = col;
            this.row = row;
        }
    }

    class TileServer extends Thread {
        private final String pattern;

        TileServer(String pattern) {
            this.pattern = pattern;
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    final TileRequest req = requestQueue.take();
                    try {
                        processRequest(req);
                    } catch (Throwable ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                notifier.tileFailed(req.zoom, req.col, req.row);
                            }
                        });
                    }
                }
            } catch (InterruptedException ex) {
                LOG.info("Tile server was interrupted");
            }
        }

        private void processRequest(final TileRequest req) throws IOException {
            String surl = MessageFormat.format(
                    pattern, req.zoom, req.col, req.row);
            LOG.log(Level.INFO, "Downloading c={0}, r={1}, z={2}: [{3}]",
                    new Object[]{req.col, req.row, req.zoom, surl});
            URL url = new URL(surl);
            try (InputStream in = url.openStream()) {
                File dir = tileDir(req.zoom);
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new IOException("Could not create directory " + dir);
                }
                File tmp = File.createTempFile("til", null, dir);
                Files.save(in, tmp);
                final File file = tileFile(req.zoom, req.col, req.row);
                if (!tmp.renameTo(file)) {
                    throw new IOException("Could not rename " + tmp + " to "
                            + file);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        notifier.tileLoaded(req.zoom, req.col, req.row);
                    }
                });
            }
        }
    }
}
