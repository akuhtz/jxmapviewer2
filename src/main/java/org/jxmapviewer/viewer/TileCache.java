package org.jxmapviewer.viewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

/**
 * in memory tile cache
 * 
 * @author maoanapex88@163.com alexmao86
 *
 */
public interface TileCache {
	public void put(URI uri, byte[] bimg, BufferedImage img);

	public BufferedImage get(URI uri) throws IOException;
}
