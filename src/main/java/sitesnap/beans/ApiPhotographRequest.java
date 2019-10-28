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
package sitesnap.beans;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import sitesnap.utils.Dimension;
import sitesnap.utils.PhotographRequest;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class ApiPhotographRequest {
	
	public static final String PARAM_WIDTH = "width".intern();
	
	public static final String PARAM_HEIGHT = "height".intern();
	
	public static final String PARAM_URL = "url".intern();
	
	private int width;
	
	private int height;
	
	private URL url;
	
	public PhotographRequest toPhotographRequest() {
		PhotographRequest instance = new PhotographRequest(getUrl());
		Dimension size = new Dimension(getWidth(), getHeight());
		
		instance.setMonitorSize(size);
		instance.setMaximumBounds(size);
		
		return instance;
	}
	
	public static ApiPhotographRequest parse(HttpServletRequest request) 
			throws NoSuchFieldException, IllegalArgumentException, MalformedURLException {
		ApiPhotographRequest instance = new ApiPhotographRequest();
		String sourceWidth = request.getParameter(PARAM_WIDTH);
		String sourceHeight = request.getParameter(PARAM_HEIGHT);
		String sourceURL = request.getParameter(PARAM_URL);
		
		if (sourceWidth == null) {
			throw new NoSuchFieldException(PARAM_WIDTH);
		}
		if (!sourceWidth.matches("\\d+")) {
			throw new IllegalArgumentException(PARAM_WIDTH);
		}
		if (sourceHeight == null) {
			throw new NoSuchFieldException(PARAM_HEIGHT);
		}
		if (!sourceHeight.matches("\\d+")) {
			throw new IllegalArgumentException(PARAM_HEIGHT);
		}
		if (sourceURL == null) {
			throw new NoSuchFieldException(PARAM_URL);
		}
		
		instance.setWidth(Integer.valueOf(sourceWidth));
		instance.setHeight(Integer.valueOf(sourceHeight));
		instance.setUrl(new URL(sourceURL));
		
		return instance;
	}
	
}
