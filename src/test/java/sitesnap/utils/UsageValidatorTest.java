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

import db.Connections;
import db.Database;
import db.beans.ActiveConnection;
import db.beans.ApiUser;
import db.beans.Limit;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import sitesnap.ApiLimits;

/**
 * <b>Important Note</b> - this testsuite is explicitly linked to the "Free"
 * package, and the default ApiUser. If the default limits are changed, this
 * suite needs to be modified to reflect thus.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class UsageValidatorTest {
	
	public UsageValidatorTest() {
	}
	
//	private static Server server;
//	private static URI serverUri;
	
	private static Connection keepAlive;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Connections.init(RuntimeConfigurationType.DEVELOPMENT);
		
//		server = new Server();
//		ServerConnector connector = new ServerConnector(server);
//		
//		connector.setPort(0);
//		server.addConnector(connector);
//		
//		ServletContextHandler context = new ServletContextHandler();
//		ServletHolder defaultServ = new ServletHolder("default", DefaultServlet.class);
//		
//		defaultServ.setInitParameter("resourceBase", System.getProperty("user.dir"));
//        defaultServ.setInitParameter("dirAllowed", "true");
//        context.addServlet(defaultServ, "/");
//        server.setHandler(context);
//		
//		server.start();
//		
//		String host = coalesce(connector.getHost(), "localhost");
//		int port = connector.getLocalPort();
//		
//		serverUri = new URI(String.format("http://%s:%d/", host, port));

		// open a persistent DB connection so that
		// we can keep any changes and data. Closing all connections causes all
		// of this to be lost
		keepAlive = Connections.get();

		Connections.doMigration();
	}
	
//	private static <T> T coalesce(T ... ts) {
//		for (T t : ts) {
//			if (t != null) {
//				return t;
//			}
//		}
//		
//		return null;
//	}

	@AfterClass
	public static void tearDownClass() throws Exception {
//		server.stop();

		keepAlive.close();
	}
	
	private ApiUser user;
	@Mock
	private HttpServletRequest request;
	private UsageValidator subject;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(request.getRemoteAddr()).thenReturn("192.168.1.128");
		
		user = ApiUser.find("nathan@crause.name");
		subject = new UsageValidator(user, request);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetLimits() throws Exception {
		assertEquals(4, subject.getLimits().size());
	}

	@Test
	public void testGetMinuteLimits() throws Exception {
		ArrayList<Limit> limits = subject.getMinuteLimits();
		
		assertEquals(1, limits.size());
		assertEquals(Limit.MINUTE, limits.get(0).getTemporalUnit());
	}

	@Test
	public void testGetHourLimits() throws Exception {
		ArrayList<Limit> limits = subject.getHourLimits();
		
		assertEquals(1, limits.size());
		assertEquals(Limit.HOUR, limits.get(0).getTemporalUnit());
	}

	@Test
	public void testGetDayLimits() throws Exception {
		ArrayList<Limit> limits = subject.getDayLimits();
		
		assertEquals(1, limits.size());
		assertEquals(Limit.DAY, limits.get(0).getTemporalUnit());
	}

	@Test
	public void testGetMonthLimits() throws Exception {
		ArrayList<Limit> limits = subject.getMonthLimits();
		
		assertEquals(1, limits.size());
		assertEquals(Limit.MONTH, limits.get(0).getTemporalUnit());
	}

	@Test(expected = Test.None.class)
	public void testValidatePerMinuteLimitsWithinCap() throws Exception {
		subject.validatePerMinuteLimits();
	}
	
	@Test(expected = LimitsExceededException.class)
	public void testValidatePerMinuteLimitsExceedingCap() throws Exception {
		SnapRequestRecorder recorder = new SnapRequestRecorder("https://google.ca", "api");
		
		recorder.setUser(user);
		
		for (int i = 0; i <= ApiLimits.PER_MINUTE; ++i) {
			recorder.writeSnap(request);
		}
		
		subject.validatePerMinuteLimits();
	}
	
}
