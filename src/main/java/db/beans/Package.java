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

import db.Database;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import lombok.Data;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class Package implements Serializable {
	
	private static final long serialVersionUID = -666L;
	
	private long id;
	
	private String name;
	
	private Timestamp dateCreated;
	
	public ArrayList<Limit> getLimits() throws SQLException {
		return Limit.listByPackage(this);
	}
	
	public static Package find(long id, Database db, Connection conn) {
		DBCommand cmd = db.createCommand();
		DBReader reader = new DBReader();

		cmd.select(db.packages.getColumns());
		cmd.where(db.packages.id.is(id));
		reader.open(cmd, conn);

		try {
			ArrayList<Package> packages = reader.getBeanList(Package.class, 1);

			if (packages.size() > 0) {
				return packages.get(0);
			}
		}
		finally {
			reader.close();
		}
		
		return null;
	}
	
	public static Package find(long id) throws SQLException {
		return Database.apply((db, conn) -> find(id, db, conn));
	}
	
}