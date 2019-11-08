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
package db.beans;

import db.Connections;
import db.Database;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * This class encapsulates an open EmpireDB connection
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Log
public class ActiveConnection implements AutoCloseable {
	
	@Getter
	private final Connection connection;
	
	@Getter
	private final Database database;
	
	public ActiveConnection() throws SQLException {
		log.info("Opening connection");
		connection = Connections.get();
		log.info("Acquiring database");
		database = new Database();

		log.info("Opening database");
		database.open(Connections.getDriver(), connection);
		
		if (!database.isOpen()) {
			throw new RuntimeException("Unexpectedly unopened database!");
		}
	}

	@Override
	public void close() throws IOException {
		log.info("Closing database");
		database.close(connection);

		try {
			log.info("Closing connection");
			connection.close();
		}
		catch (SQLException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}
	
}
