package org.jxmapviewer.bing;

import org.jxmapviewer.viewer.TileFactoryInfo;

/**
 * ClassName: MSBingTileFactoryInfo <br/>
 * 微软bing地图瓦片抓取器
 * 
 * https://en.wikipedia.org/wiki/Tiled_web_map
 * https://msdn.microsoft.com/en-us/library/bb259689.aspx
 * 
 * date: 2018年4月27日 下午3:28:54 <br/>
 * 
 * @author anping.mao@cn.bosch.com mna1szh
 * @version
 * @since JDK 1.8
 */
public class BingTileFactoryInfo extends TileFactoryInfo {

	private static final int MS_MIN_ZOOM = 0;
	private static final int MS_MAX_ZOOM = 21;

	public static final String baseURL1 = "t.ssl.ak.dynamic.tiles.virtualearth.net/comp/ch/";
	// only for china mainland
	public static final String baseURL2 = "dynamic.t1.tiles.ditu.live.com/comp/ch/";

	public static int MAP_TYPE_ROAD = 0;
	public static int MAP_TYPE_AERIAL = 1;

	private String lang = "en-SG";

	private boolean https = false;
	private boolean mainlangOnly = false;

	private int mapType = MAP_TYPE_ROAD;

	private int allowedMinimunZoom = MS_MIN_ZOOM;
	private int allowedMaximunZoom = MS_MAX_ZOOM;

	public BingTileFactoryInfo() {
		super("MicrosoftBingMap", 0, MS_MAX_ZOOM, MS_MAX_ZOOM, 256, true, true, "BaseURL", "x", "y", "z");
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		StringBuilder builder = new StringBuilder(256);
		builder.append(getBaseURL());

		if (zoom > allowedMaximunZoom)
			zoom = allowedMaximunZoom;
		if (zoom < allowedMinimunZoom)
			zoom = allowedMinimunZoom;

		builder.append(BingTileUtil.TileXYToQuadKey(x, y, MS_MAX_ZOOM-zoom));// MS is on doing reversed
		builder.append("?mkt=").append(lang);

		if (mapType == MAP_TYPE_ROAD) {
			builder.append("&it=G,BX,RL");
		} else if (mapType == MAP_TYPE_AERIAL) {
			builder.append("&it=A,G,RL");
		}
		builder.append("&shading=hill&og=526&n=z&c4w=1&cstl=vb&src=h");

		//System.out.println(x + ", " + y + ", " + zoom);
		//System.out.println(builder);

		return builder.toString();
	}

	public final int getAllowedMinimunZoom() {
		return allowedMinimunZoom;
	}

	public final void setAllowedMinimunZoom(int allowedMinimunZoom) {
		if (allowedMinimunZoom < MS_MIN_ZOOM) {
			allowedMinimunZoom = MS_MIN_ZOOM;
		}
		this.allowedMinimunZoom = allowedMinimunZoom;
	}

	public final int getAllowedMaximunZoom() {
		return allowedMaximunZoom;
	}

	public final void setAllowedMaximunZoom(int allowedMaximunZoom) {
		if (allowedMaximunZoom > MS_MAX_ZOOM) {
			allowedMaximunZoom = MS_MAX_ZOOM;
		}
		this.allowedMaximunZoom = allowedMaximunZoom;
	}

	public final String getLang() {
		return lang;
	}

	public final void setLang(String lang) {
		this.lang = lang;
	}

	public final boolean isHttps() {
		return https;
	}

	public final void setHttps(boolean https) {
		this.https = https;
	}

	public final boolean isMainlangOnly() {
		return mainlangOnly;
	}

	public final void setMainlangOnly(boolean mainlangOnly) {
		this.mainlangOnly = mainlangOnly;
	}

	public final int getMapType() {
		return mapType;
	}

	public final void setMapType(int mapType) {
		this.mapType = mapType;
	}

	@Override
	public String getBaseURL() {
		if (mainlangOnly) {
			return (https ? "https://" : "http://") + baseURL2;
		}
		return (https ? "https://" : "http://") + baseURL1;
	}

}
