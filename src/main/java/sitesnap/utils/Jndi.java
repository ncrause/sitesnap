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

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class Jndi {
	
	private static Context initialContext;
	
	public static Context getInitialContext() throws NamingException {
		if (initialContext == null) {
			initialContext = new InitialContext();
		}
		
		return initialContext;
	}
	
	private static Context environmentContext;
	
	public static Context getEnvironmentContext() throws NamingException {
		if (environmentContext == null) {
			environmentContext = (Context) getInitialContext().lookup("java:comp/env");
		}
		
		return environmentContext;
	}
	
	public static Session getMailSession() throws NamingException {
		return (Session) getEnvironmentContext().lookup("mail/Session");
	}
	
}
