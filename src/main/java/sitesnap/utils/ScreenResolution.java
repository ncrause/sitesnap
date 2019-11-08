/*
 * Copyright (C) 2019 Nathan Crause <nathan@crause.name>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sitesnap.utils;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public enum ScreenResolution {
	
	nHD(640, 360),
	SVGA(800, 600),
	XGA(1024, 768),
	WXGA(1280, 768),
	WXGA_Taller(1280, 800),
	SXGA(1280, 1024),
	HD(1360, 768),
	HD_Wider(1366, 768),
	WXGA_Plus(1440, 900),
	WSCGA_Plus(1680, 1050),
	FHD(1920, 1080),
	WUXGA(1920, 1200),
	QWXGA(2048, 1152),
	QHD(2560, 1440),
	FOUR_K_YHD(3840, 2160),
	// the following come from https://developer.apple.com/library/archive/documentation/DeviceInformation/Reference/iOSDeviceCompatibility/Displays/Displays.html
	IPHONE_X(375, 812),
	IPHONE_8_PLUS(414, 736),
	IPHONE_7_PLUS(414, 736),
	IPHONE_6S_PLUS(375, 667),
	IPHONE_6_PLUS(375, 667),
	IPHONE_7(375, 667),
	IPHONE_6S(375, 667),
	IPHONE_6(375, 667),
	IPHONE_SE(320, 568),
	IPAD_PRO_12IN(1024, 1336),
	IPAD_PRO_10IN(1112, 834),
	IPAD_PRO_9IN(768, 1024),
	IPAD_AIR_2(768, 1024),
	IPAD_MINI_4(768, 1024);
	
	private final Dimension size;

	ScreenResolution(int width, int height) {
		this.size = new Dimension(width, height);
	}
	
	public int width() {
		return size.getWidth();
	}
	
	public int height() {
		return size.getHeight();
	}
	
	public Dimension size() {
		return size;
	}
	
}
