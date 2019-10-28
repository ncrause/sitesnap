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

import db.beans.ActiveConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.sql.DataSource;
import lombok.extern.java.Log;
import org.apache.empire.db.DBDatabaseDriver;
import org.apache.empire.db.h2.DBDatabaseDriverH2;
import org.apache.empire.db.postgresql.DBDatabaseDriverPostgreSQL;
import org.apache.empire.db.sqlserver.DBDatabaseDriverMSSQL;
import org.apache.wicket.RuntimeConfigurationType;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Utility class which provides a data source based on the environment
 * within which we are running.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Log
public class Connections {
	
	public static final ThreadLocal<ActiveConnection> ACTIVE_CONNECTION = new ThreadLocal<ActiveConnection>() {
		@Override
		protected ActiveConnection initialValue() {
			try {
				return new ActiveConnection();
			}
			catch (SQLException ex) {
				log.log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex);
			}
		}
	};
	
	private static RuntimeConfigurationType configuration;
	
	public static void init(RuntimeConfigurationType configuration) {
		Connections.configuration = configuration;
	}
	
	public static DataSource getDataSource() {
		if (configuration == null) {
			throw new IllegalStateException("Not configured");
		}
		
		if (configuration == RuntimeConfigurationType.DEVELOPMENT) {
			JdbcDataSource source = new JdbcDataSource();
			
			source.setURL("jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE");
			source.setUser("sa");
			source.setPassword("");
			
			return source;
		}
		
		// TODO: in production, we use a JNDI resource
		return null;
	}
	
	public static Connection get() throws SQLException {
		return getDataSource().getConnection();
	}
	
	private static DBDatabaseDriver driver;
	
	public static DBDatabaseDriver getDriver() throws SQLException {
		if (driver == null) {
			driver = determineDriver();
		}
		
		return driver;
	}

	private static DBDatabaseDriver determineDriver() throws SQLException {
		try (Connection c = get()) {
			DatabaseMetaData meta = c.getMetaData();
			
			switch (meta.getDatabaseProductName()) {
				case "H2":
					return new DBDatabaseDriverH2();
				case "PostgreSQL":
					return new DBDatabaseDriverPostgreSQL();
				default:
					throw new SQLException(String.format("Unable to determine drive for product '%s'", meta.getDatabaseProductName()));
			}
		}
	}
	
	private static Database database;
	
	public static Database getDatabase() {
		if (database == null) {
			database = new Database();
		}
		
		return database;
	}
	
	public static String getBinaryType() throws SQLException {
		if (getDriver() instanceof DBDatabaseDriverPostgreSQL) {
			return "bytea";
		}
		
		return "blob";
	}
	
	public static String getTimestampType() throws SQLException {
		if (getDriver() instanceof DBDatabaseDriverPostgreSQL) {
			return "timestamp with timezone";
		}
		else if (getDriver() instanceof DBDatabaseDriverMSSQL) {
			return "datetime";
		}
		
		return "timestamp";
	}
	
	public static String getNetworkAddressType() throws SQLException {
		if (getDriver() instanceof DBDatabaseDriverPostgreSQL) {
			return "inet";
		}
		
		return "varchar(255)";
	}
	
}
