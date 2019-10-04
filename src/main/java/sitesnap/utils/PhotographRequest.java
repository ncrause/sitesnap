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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This is essentially a model containing details of the snapshot we are
 * wanting to take.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@ToString
public class PhotographRequest {
	
	public static final String KEY_URL = "url".intern();
	
	public static final String KEY_MONITOR_SIZE = "monitorSize".intern();
	
	public static final String KEY_MONITOR_WIDTH = "monitorWidth".intern();
	
	public static final String KEY_MONITOR_HEIGHT = "monitorHeight".intern();
	
	public static final String KEY_IMAGE_SIZE = "imageSize".intern();
	
	public static final String KEY_IMAGE_WIDTH = "imageWidth".intern();
	
	public static final String KEY_IMAGE_HEIGHT = "imageHeight".intern();
	
	/**
	 * This is the URL which is to be photographed.
	 */
	@Getter
	private final URL target;

	public PhotographRequest(URL target) {
		this.target = target;
	}
	
	@Getter @Setter
	private Dimension monitorSize = new Dimension(1280, 768);

	@Getter @Setter
	private Dimension maximumBounds = new Dimension(1280, 768);
	
	public void setMonitorResolution(ScreenResolution resolution) {
		setMonitorSize(resolution.size());
	}
	
	public void setBoundsResolution(ScreenResolution resolution) {
		setMaximumBounds(resolution.size());
	}
	
	/**
	 * Using this requests monitor size to determine the aspect ratio, this
	 * method determines the bounds of an image which will be constrained
	 * within the maximum bounds.
	 * 
	 * @return the dimensions of the target image size within the maximum
	 * specified bounds, but with the monitor size's aspect ratio preserved.
	 */
	public Dimension getTargetBounds() {
		double sourceRatio = getMonitorSize().getAspectRatio();
		double boundRatio = getMaximumBounds().getAspectRatio();
		
		if (boundRatio > sourceRatio) {
			double targetHeight = (double) getMaximumBounds().getHeight();
			
			return new Dimension((int) Math.floor(targetHeight * sourceRatio), 
					(int) targetHeight);
		}
		
		double targetWidth = (double) getMaximumBounds().getWidth();

		return new Dimension((int) targetWidth, 
				(int) Math.floor(targetWidth / sourceRatio));
	}
	
	public static PhotographRequest fromMap(Map<String, Object> source) 
			throws MalformedURLException {
		URL url = urlValueOf(source.get(KEY_URL));
		PhotographRequest instance = new PhotographRequest(url);
		
		if (source.containsKey(KEY_MONITOR_SIZE) 
				|| (source.containsKey(KEY_MONITOR_WIDTH) 
						&& source.containsKey(KEY_MONITOR_HEIGHT))) {
			instance.setMonitorSize(
					dimensionValueOf((Dimension) source.get(KEY_MONITOR_SIZE), 
							(Integer) source.get(KEY_MONITOR_WIDTH), 
							(Integer) source.get(KEY_MONITOR_HEIGHT)));
		}
		
		if (source.containsKey(KEY_IMAGE_SIZE) 
				|| (source.containsKey(KEY_IMAGE_WIDTH) 
						&& source.containsKey(KEY_IMAGE_HEIGHT))) {
			instance.setMonitorSize(
					dimensionValueOf((Dimension) source.get(KEY_IMAGE_SIZE), 
							(Integer) source.get(KEY_IMAGE_WIDTH), 
							(Integer) source.get(KEY_IMAGE_HEIGHT)));
		}
		
		return instance;
	}
	
	private static URL urlValueOf(Object value) throws MalformedURLException {
		if (value == null) {
			throw new NullPointerException("There is no value defined");
		}
		
		if (value instanceof URL) {
			return (URL) value;
		}
		
		if (value instanceof String) {
			return new URL((String) value);
		}
		
		throw new IllegalArgumentException(
				String.format("Expected String or URL, received %s", 
						value.getClass().getName()));
	}

	private static Dimension dimensionValueOf(Dimension size, Integer width, Integer height) {
		// size is the authoritive value, so if it's here, just return it as-is
		if (size != null) {
			return size;
		}
		
		return new Dimension(width, height);
	}


}
