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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

/**
 * This is the primary worker class which invokes wkhtmltoimage and returns
 * lossless raw image back.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class Photographer {
	
	public BufferedImage click(PhotographRequest request) throws IOException, InterruptedException {
		BufferedImage monitorView = expose(request);
		// only invoke scaling if it's epxlicitly needed.
		return request.requiresScaling() 
				? scale(request, monitorView) 
				: monitorView;
	}
	
	/**
	 * Gets the initial, unscaled, uncompressed snapshot of the requested
	 * website URL.
	 * 
	 * @param request
	 * @return 
	 */
	private BufferedImage expose(PhotographRequest request) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(getExecuteablePath(),
				"--width", Integer.toString(request.getMonitorSize().getWidth()),
				"--height", Integer.toString(request.getMonitorSize().getHeight()),
				"--format", "png",
				"--quality", "100",
				// this breaks on Linux, but is really required for responsive websites to function correctly :'(
				"--disable-smart-width",
//				"--log-level", "none",
				"--quiet",
				request.getTarget().toExternalForm(),
				"/dev/stdout");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ByteArrayOutputStream error = new ByteArrayOutputStream();
		Process process = builder.start();

		IOUtils.copy(process.getInputStream(), output);
		IOUtils.copy(process.getErrorStream(), error);
		
		process.waitFor(2500, TimeUnit.MILLISECONDS);
		
		if (process.exitValue() != 0) {
			throw new RuntimeException(error.toString());
		}
		
		// now we can convert it to an image!
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

		// We can't simply return the image as-is because some websites will
		// ignore the monitor size, and this seems to result in a larger
		// image than expected. So we actually need to actively clip it.
//		return ImageIO.read(input);
		return ImageIO.read(input).getSubimage(0, 0, request.getMonitorSize().getWidth(), request.getMonitorSize().getHeight());
	}

	/**
	 * Takes in the raw snapshot data, and scales it down to match the requested
	 * maximum bounds, while preserving the aspect ratio (by using the request
	 * instance's <code>getTargetBounds()</code> method.
	 * 
	 * @param request
	 * @param monitorView
	 * @return 
	 */
	private BufferedImage scale(PhotographRequest request, BufferedImage monitorView) {
		Dimension targetSize = request.getTargetBounds();
		BufferedImage boundView = new BufferedImage(targetSize.getWidth(), targetSize.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = boundView.createGraphics();
		
		// possibly BICUBIC would yield cleaner results, but the cost from 4 neightbours to 9 means significant CPU, and at no real benefit when downsizing.
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		graphics.drawImage(monitorView, 0, 0, targetSize.getWidth(), targetSize.getHeight(), null);
		graphics.dispose();
		
		return boundView;
	}
	
	/**
	 * The below is the list of directories that will be checked for the
	 * wkhtmltoimage binary file. This list was determined based on the location
	 * into which it is installed on Ubuntu, FreeBSD and MacOS X
	 */
	private static final String[] EXECUTABLE_DIRECTORIES = new String[] {
		"/usr/local/bin",
		"/usr/bin"
	};
	
	private static String executablePath;
	
	protected static String getExecuteablePath() throws FileNotFoundException {
		if (executablePath != null) {
			return executablePath;
		}
		
		for (String dir : EXECUTABLE_DIRECTORIES) {
			File test = new File(dir, "wkhtmltoimage");
			
			if (test.exists() && test.canExecute()) {
				return (executablePath = test.getAbsolutePath());
			}
		}
		
		// if it wasn't found at all, throw an exception
		throw new FileNotFoundException("Unable to locate the 'wkhtmltoimage' executable");
	}
	
}
