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
package db;

import java.sql.SQLException;
import org.apache.wicket.RuntimeConfigurationType;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class ConnectionsTest {
	
	public ConnectionsTest() {
	}
	
	@Before
	public void setUp() {
		Connections.init(RuntimeConfigurationType.DEVELOPMENT);
	}
	
	@Test
	public void testGetDriver() throws SQLException {
		System.out.println("Data Source: " + Connections.getDataSource());
		System.out.println("Driver: " + Connections.getDriver());
	}
	

//	@Test
//	public void testInit() {
//		System.out.println("init");
//		RuntimeConfigurationType configuration = null;
//		Connections.init(configuration);
//		fail("The test case is a prototype.");
//	}
//
//	@Test
//	public void testGetDataSource() {
//		System.out.println("getDataSource");
//		DataSource expResult = null;
//		DataSource result = Connections.getDataSource();
//		assertEquals(expResult, result);
//		fail("The test case is a prototype.");
//	}
//
//	@Test
//	public void testGet() throws Exception {
//		System.out.println("get");
//		Connection expResult = null;
//		Connection result = Connections.get();
//		assertEquals(expResult, result);
//		fail("The test case is a prototype.");
//	}
//
//	@Test
//	public void testGetDriver() {
//		System.out.println("getDriver");
//		DBDatabaseDriver expResult = null;
//		DBDatabaseDriver result = Connections.getDriver();
//		assertEquals(expResult, result);
//		fail("The test case is a prototype.");
//	}
	
}
