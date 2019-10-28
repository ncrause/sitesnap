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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import org.apache.empire.db.DBDatabase;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class Database extends DBDatabase {
	
	public final Snaps snaps = new Snaps(this);
	public final Requests requests = new Requests(this);
	public final RequestData requestData = new RequestData(this);
	public final ApiUsers apiUsers = new ApiUsers(this);
	public final Packages packages = new Packages(this);
	public final Limits limits = new Limits(this);
	
	public Database() {
		addRelation(requestData.requestId.referenceOn(requests.id));
		addRelation(requests.snapId.referenceOn(snaps.id));
		addRelation(requests.apiUserId.referenceOn(apiUsers.id));
		addRelation(limits.packageId.referenceOn(packages.id));
	}
	
	public static void with(BiConsumer<Database, Connection> func) throws SQLException {
		Connection c = Connections.get();
		Database db = Connections.getDatabase();

		db.open(Connections.getDriver(), c);

		try {
			func.accept(db, c);
		}
		finally {
			db.close(c);

			c.close();
		}
	}
	
	/**
	 * Slightly more complex version of <code>with</code>, but this one
	 * is more specifically tailored to returning values.
	 * 
	 * @param <R>
	 * @param type
	 * @param func
	 * @return
	 * @throws SQLException 
	 */
	public static <R> R apply(ConnectionFunction<R> func) throws SQLException {
		Connection c = Connections.get();
		Database db = Connections.getDatabase();

		db.open(Connections.getDriver(), c);

		try {
			return func.apply(db, c);
		}
		finally {
			db.close(c);

			c.close();
		}
	}
	
	
}
