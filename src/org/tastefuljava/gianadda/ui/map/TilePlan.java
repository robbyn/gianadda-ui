package org.tastefuljava.gianadda.ui.map;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import org.tastefuljava.gianadda.ui.util.ListenerList;

public class TilePlan implements Closeable, TileListener {
    private final TileManager manager;
    private final int zoom;
    private final ListenerList listeners = new ListenerList();
    private final TileListener notifier
            = listeners.getNotifier(TileListener.class);

    public static TilePlan create(TileManager manager, int zoom) {
        TilePlan plan = new TilePlan(manager, zoom);
        manager.addListener(plan);
        return plan;
    }

    private TilePlan(TileManager manager, int zoom) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        } else if (zoom < 0 || zoom > 19) {
            throw new IllegalArgumentException("Invalid zoom level " + zoom);
        }
        this.manager = manager;
        this.zoom = zoom;
    }

    @Override
    public void close() throws IOException {
        manager.removeListener(this);
    }

    public int getZoom() {
        return zoom;
    }

    public int getRows() {
        return manager.getRows(zoom);
    }

    public int getColumns() {
        return manager.getColumns(zoom);
    }

    public int getWidth() {
        return getColumns()*manager.getTileWidth();
    }

    public int getHeight() {
        return getRows()*manager.getTileHeight();
    }

    public int lng2x(double lng) {
        return manager.lng2x(lng, zoom);
    }

    public int lat2y(double lng) {
        return manager.lng2x(lng, zoom);
    }

    public double x2lng(int x) {
        return manager.x2lng(x, zoom);
    }

    public double y2lat(int y) {
        return manager.y2lat(y, zoom);
    }

    public void addListener(TileListener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(TileListener listener) {
        listeners.removeListener(listener);
    }

    public BufferedImage getTile(int col, int row) throws IOException {
        return manager.getTile(zoom, col, row);
    }

    public void requestRect(int x, int y, int w, int h) throws IOException {
        int tw = manager.getTileWidth();
        int th = manager.getTileHeight();
        int startCol = Math.max(0, x/tw);
        int endCol = Math.min(getColumns(), (x+w+tw-1)/tw);
        int startRow = Math.max(0, y/th);
        int endRow = Math.min(getRows(), (y+h+th-1)/th);
        for (int c = startCol; c < endCol; ++c) {
            for (int r = startRow; r < endRow; ++r) {
                manager.requestTile(zoom, c, r);
            }
        }
    }

    @Override
    public void tileLoaded(int zoom, int col, int row) {
        if (zoom == this.zoom) {
            notifier.tileLoaded(zoom, col, row);
        }
    }

    @Override
    public void tileFailed(int zoom, int col, int row) {
        if (zoom == this.zoom) {
            notifier.tileFailed(zoom, col, row);
        }
    }
}
