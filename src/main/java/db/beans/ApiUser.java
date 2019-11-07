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

import db.ApiUsers;
import db.Database;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBRecord;
import org.apache.empire.db.DBRowSet;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class ApiUser implements Serializable {
	
	private static final long serialVersionUID = -777L;
	
	private Long id;
	
	private String emailAddress;
	
	private String passwordHash;
	
	private Long packageId;
	
	private Timestamp dateCreated;
	
	public boolean isPasswordValid(String password) {
		String hash = DigestUtils.sha256Hex(password);
		
		return getPasswordHash().equalsIgnoreCase(hash);
	}
	
	public Package getPackage() throws SQLException, IOException {
		return Package.find(packageId);
	}
	
	public ApiUser save(ActiveConnection activeConnection) {
		DBRecord rec = new DBRecord();
		ApiUsers table = activeConnection.getDatabase().apiUsers;
		
		if (id == null) {
			rec.create(table);
		}
		else {
			rec.read(table, id, activeConnection.getConnection());
		}
		
		rec.setValue(table.emailAddress, emailAddress);
		rec.setValue(table.passwordHash, passwordHash);
		rec.setValue(table.packageId, packageId);
		
		rec.update(activeConnection.getConnection());
		
		return this;
	}
	
	public ApiUser save() throws SQLException, IOException {
		try (ActiveConnection activeConnection = new ActiveConnection()) {
			return save(activeConnection);
		}
	}
	
	public static ApiUser find(String emailAddress, ActiveConnection activeConnection) {
		Database db = activeConnection.getDatabase();
		Connection conn = activeConnection.getConnection();
		DBCommand cmd = db.createCommand();
		DBReader reader = new DBReader();

		cmd.select(db.apiUsers.getColumns());
		cmd.where(db.apiUsers.emailAddress.is(emailAddress));
		reader.open(cmd, conn);

		try {
			ArrayList<ApiUser> users = reader.getBeanList(ApiUser.class, 1);

			if (users.size() > 0) {
				return users.get(0);
			}
		}
		finally {
			reader.close();
		}
		
		return null;
	}
	
	public static ApiUser find(String emailAddress) throws SQLException, IOException {
		try (ActiveConnection activeConnection = new ActiveConnection()) {
			return find(emailAddress, activeConnection);
		}
	}
	
}
