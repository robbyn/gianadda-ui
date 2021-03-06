package org.tastefuljava.gianadda.ui.map;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.tastefuljava.gianadda.geo.LatLngBounds;
import org.tastefuljava.gianadda.geo.TrackPoint;

public class MapView extends JComponent implements TileListener {
    private static final Logger LOG = Logger.getLogger(MapView.class.getName());

    private TileManager manager;
    private TilePlan plan;
    private int zoom;
    private int top;
    private int left;

    public MapView() {
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
        setMinimumSize(new Dimension(256, 256));
    }

    public TileManager getManager() {
        return manager;
    }

    public void setManager(TileManager manager) {
        clearPlan();
        this.manager = manager;
        setupPlan();
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
        checkPlan();
        refresh();
    }

    public void setView(int zoom, double lat, double lng) {
        this.zoom = zoom;
        checkPlan();
        if (plan != null) {
            int x = plan.lng2x(lng);
            int y = plan.lat2y(lat);
            left = x - getWidth()/2;
            top = y - getHeight()/2;
        }
        refresh();
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
        refresh();
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
        refresh();
    }

    public Point getPosition() {
        return new Point(left, top);
    }

    public void setPosition(int left, int top) {
        this.left = left;
        this.top = top;
        refresh();
    }

    private void checkPlan() {
        if (plan == null || plan.getZoom() != zoom) {
            clearPlan();
            setupPlan();
        }
    }

    private void clearPlan() {
        if (plan != null) {
            try {
                plan.removeListener(this);
                plan.close();
                plan = null;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setupPlan() {
        if (manager != null) {
            plan = TilePlan.create(manager, zoom);
            plan.addListener(this);
        }
    }

    private void refresh() {
        if (manager != null && plan != null) {
            try {
                manager.clearQueue();
                plan.requestRect(left, top, getWidth(), getHeight());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        repaint();
    }

    @Override
    public void tileLoaded(int zoom, int col, int row) {
        if (plan != null) {
            int tw = plan.getTileWidth();
            int th = plan.getTileHeight();
            int tl = col*tw - left;
            int tt = row*th - top;
            int tr = tl + tw;
            int tb = tt + th;
            tl = Math.max(0, tl);
            tr = Math.min(getWidth(), tr);
            tt = Math.max(0, tt);
            tb = Math.min(getHeight(), tb);
            if (tl < tr && tt < tb) {
                repaint(tl, tt, tr-tl, tb-tt);
            }
        }
    }

    @Override
    public void tileFailed(int zoom, int col, int rom) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (plan != null) {
            Rectangle rc = g.getClipBounds();
            int x = rc.x+left;
            int y = rc.y+top;
            int w = rc.width;
            int h = rc.height;
            int tw = plan.getTileWidth();
            int th = plan.getTileHeight();
            int nx = plan.normalizeX(x);
            int startCol = nx/tw;
            int endCol = (nx+w+tw-1)/tw;
            int startRow = Math.max(0, y/th);
            int endRow = Math.min(plan.getRows(), (y+h+th-1)/th);
            for (int c = startCol; c < endCol; ++c) {
                int tx = (c%plan.getColumns())*th - left;
                for (int r = startRow; r < endRow; ++r) {
                    int ty = r*tw - top;
                    try {
                        BufferedImage img = plan.getTile(c, r);
                        if (img != null) {
                            g.drawImage(img, tx, ty, null);
                        }
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        if (e.getComponent() == this
                && e.getID() == ComponentEvent.COMPONENT_RESIZED) {
            refresh();
        }
    }

    public void fitBounds(LatLngBounds bounds) {
        if (manager != null) {
            Rectangle rc = manager.bounds2rect(bounds, 0);
            int z = 0;
            do {
                ++z;
                Rectangle r = manager.bounds2rect(bounds, z);
                if (r.width > getWidth() || r.height > getHeight()) {
                    break;
                }
                rc = r;
            } while (z < 20);
            --z;
            zoom = z;
            left = rc.x+(rc.width-getWidth())/2;
            top = rc.y+(rc.height-getHeight())/2;
            checkPlan();
            refresh();
        }
    }

    public void addTrack(TrackPoint[] points) {
        if (manager != null) {
            LatLngBounds bounds = LatLngBounds.build(points);
            fitBounds(bounds);
        }
    }
}
