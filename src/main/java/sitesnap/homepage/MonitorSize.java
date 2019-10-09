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
package sitesnap.homepage;

import java.util.Arrays;
import java.util.List;
import sitesnap.utils.Dimension;
import sitesnap.utils.ScreenResolution;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public enum MonitorSize {
	
	EXTRA_LARGE("Extra Large", ScreenResolution.HD),
	LARGE("Large", ScreenResolution.XGA),
	MEDIUM("Medium", ScreenResolution.SVGA),
	IPAD("iPad", ScreenResolution.IPAD_PRO_9IN),
	IPHONE_PLUS("iPhone 7/8 Plus", ScreenResolution.IPHONE_8_PLUS),
	IPHONE("iPhone 7/8", ScreenResolution.IPHONE_7);
	
	private final String humanized;
	
	private final Dimension size;
	
	MonitorSize(String humanized, Dimension size) {
		this.humanized = humanized;
		this.size = size;
	}
	
	MonitorSize(String humanized, int width, int height) {
		this(humanized, new Dimension(width, height));
	}
	
	MonitorSize(String humanized, ScreenResolution resolution) {
		this(humanized, resolution.size());
	}
	
	public String humanized() {
		return humanized;
	}
	
	public Dimension size() {
		return size;
	}
	
	public static List<MonitorSize> list() {
		return Arrays.asList(values());
	}
	
}
