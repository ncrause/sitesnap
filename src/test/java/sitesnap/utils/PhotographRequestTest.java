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

import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class PhotographRequestTest {
	
	public PhotographRequestTest() {
	}
	
	private URL url;

	@Before
	public void setUp() throws Exception {
		url = new URL("http://test.net/");
	}

	@Test
	public void testSetMonitorResolution() {
		PhotographRequest instance = new PhotographRequest(url);
		
		instance.setMonitorResolution(ScreenResolution.SVGA);
		
		assertEquals(800, instance.getMonitorSize().getWidth());
		assertEquals(600, instance.getMonitorSize().getHeight());
	}

	@Test
	public void testSetBoundsResolution() {
		PhotographRequest instance = new PhotographRequest(url);
		
		instance.setBoundsResolution(ScreenResolution.SVGA);
		
		assertEquals(800, instance.getMaximumBounds().getWidth());
		assertEquals(600, instance.getMaximumBounds().getHeight());
	}

	@Test
	public void testGetTargetBoundsWithPerfectAspectRatio() {
		PhotographRequest instance = new PhotographRequest(url);
		
		instance.setMonitorSize(new Dimension(8000, 6000));
		instance.setMaximumBounds(new Dimension(800, 600));
		
		Dimension result = instance.getTargetBounds();
		
		assertEquals(800, result.getWidth());
		assertEquals(600, result.getHeight());
	}
	
	@Test
	public void testGetTargetBoundsWithWiderTarget() {
		PhotographRequest instance = new PhotographRequest(url);
		
		instance.setMonitorSize(new Dimension(8000, 6000));
		instance.setMaximumBounds(new Dimension(1000, 500));
		
		Dimension result = instance.getTargetBounds();
		
		assertEquals(666, result.getWidth());
		assertEquals(500, result.getHeight());
	}
	
	@Test
	public void testGetTargetBoundsWithTallerTarget() {
		PhotographRequest instance = new PhotographRequest(url);
		
		instance.setMonitorSize(new Dimension(8000, 6000));
		instance.setMaximumBounds(new Dimension(500, 1000));
		
		Dimension result = instance.getTargetBounds();
		
		assertEquals(500, result.getWidth());
		assertEquals(375, result.getHeight());
	}
	
}
