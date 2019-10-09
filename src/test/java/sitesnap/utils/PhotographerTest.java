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

import java.awt.image.BufferedImage;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class PhotographerTest {
	
	public PhotographerTest() {
	}

	@Test
	public void testClick() throws Exception {
		URL url = new URL("https://www.google.ca");
		PhotographRequest request = new PhotographRequest(url);
		
		request.setMaximumBounds(new Dimension(640, 384));
		
		Photographer instance = new Photographer();
		BufferedImage image = instance.click(request);
		
		assertEquals(640, image.getWidth());
		assertEquals(384, image.getHeight());
		
//		// this is just for my own testing to make sure we actually get an image
//		File png = File.createTempFile("SiteSnap", ".png");
//		ImageIO.write(image, "png", png);
//		
//		System.out.println("Temp file: " + png.getAbsolutePath());
	}
	
}
