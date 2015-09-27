package org.tastefuljava.gianadda.ui.map;

public interface TileListener {
    public void tileLoaded(int zoom, int col, int row);
    public void tileFailed(int zoom, int col, int rom);
}
