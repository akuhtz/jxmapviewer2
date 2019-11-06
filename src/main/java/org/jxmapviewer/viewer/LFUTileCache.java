/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jxmapviewer.viewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * LFU（Least Frequently Used）最近最少使用算法。它是基于“如果一个数据在最近一段时间内使用次数很少，那么在将来一段时间内被使用的可能性也很小”的思路。
 * 为了节约内存，byte不缓存，只缓存AWT要用的BufferedImage. byte 由 responseCache缓存
 * An implementation only class for now. For internal use only.
 * enhance for thread safe
 */
public class LFUTileCache extends TileCache {
    private static final Logger LOGGER = Logger.getLogger(TileCache.class.getName());
    private final static Map<String, TimestampedBufferedImage> MAPCACHE = new ConcurrentHashMap<>();
    public final static int CACHE_SIZE=128;
    private int cacheSize=CACHE_SIZE;
    
    /**
     * Default constructor - Empty
     */
    public LFUTileCache() {
    }
    public LFUTileCache(int cacheSize) {
        this.cacheSize=cacheSize;
    }
    /**
     * Put a tile image into the cache. This puts both a buffered image and
     * array of bytes that make up the compressed image.
     *
     * @param uri URI of image that is being stored in the cache
     * @param bimg bytes of the compressed image, ie: the image file that was
     * loaded over the network
     * @param img image to store in the cache
     */
    @Override
    public void put(URI uri, byte[] bimg, BufferedImage img) {
        if(MAPCACHE.size()>=cacheSize){
            List<Entry<String, TimestampedBufferedImage>> entryList=new ArrayList<>(MAPCACHE.entrySet());
            Collections.sort(entryList, new java.util.Comparator<Entry<String, TimestampedBufferedImage>>() {
                @Override
                public int compare(Entry<String, TimestampedBufferedImage> o1, Entry<String, TimestampedBufferedImage> o2) {
                    return (int)(o1.getValue().ts - o2.getValue().ts);
                }
            });
            Entry<String, TimestampedBufferedImage> oldest=entryList.get(0);
            MAPCACHE.remove(oldest.getKey());
            LOGGER.log(Level.FINE, "removed oldest cache entry{0}", oldest.getKey());
        }
        MAPCACHE.put(uri.toASCIIString(), new TimestampedBufferedImage(img));
    }

    /**
     * Returns a buffered image for the requested URI from the cache. This
     * method must return null if the image is not in the cache. If the image is
     * unavailable but it's compressed version *is* available, then the
     * compressed version will be expanded and returned.
     *
     * @param uri URI of the image previously put in the cache
     * @return the image matching the requested URI, or null if not available
     * @throws IOException if retrieval fails
     */
    @Override
    public BufferedImage get(URI uri) throws IOException {
        TimestampedBufferedImage value = MAPCACHE.get(uri.toString());
        if(value==null){
            return null;
        }
        LOGGER.log(Level.FINE, "get tile image from memory cache {0}", uri.toString());
        value.ts=System.currentTimeMillis();//update usage ts
        return value.image;
    }

    /**
     * Request that the cache free up some memory. How this happens or how much
     * memory is freed is up to the TileCache implementation. Subclasses can
     * implement their own strategy. The default strategy is to clear out all
     * buffered images but retain the compressed versions.
     */
    @Override
    public void needMoreMemory() {
    }
    
    class TimestampedBufferedImage {
        long ts;
        BufferedImage image;
        public TimestampedBufferedImage(BufferedImage image) {
            this.ts = System.currentTimeMillis();
            this.image = image;
        }
    }
}
