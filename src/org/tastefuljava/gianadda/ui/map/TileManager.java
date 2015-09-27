package org.tastefuljava.gianadda.ui.map;

import java.io.File;
import java.io.IOException;
import org.tastefuljava.gianadda.util.Configuration;

public class TileManager {
    private final Configuration conf;
    private final File baseDir;

    public TileManager(Configuration conf) throws IOException {
        this.conf = conf;
        String base = conf.getString("tiles.base-dir", null);
        if (base != null) {
            this.baseDir = new File(base);
        } else {
            File confDir = new File(
                    System.getProperty("user.home"), ".gianadda-ui");
            this.baseDir = new File(confDir, "tiles");
        }
        if (!this.baseDir.mkdirs()) {
            throw new IOException("Could not create dir " + this.baseDir);
        }
    }
}
