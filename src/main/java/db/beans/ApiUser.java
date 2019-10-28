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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class ApiUser implements Serializable {
	
	private static final long serialVersionUID = -777L;
	
	private long id;
	
	private String emailAddress;
	
	private String passwordHash;
	
	private long packageId;
	
	private Timestamp dateCreated;
	
	public boolean isPasswordValid(String password) {
		String hash = DigestUtils.sha256Hex(password);
		
		return getPasswordHash().equalsIgnoreCase(hash);
	}
	
	public Package getPackage() throws SQLException {
		return Package.find(packageId);
	}
	
	public static ApiUser find(String emailAddress, Database db, Connection conn) {
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
	
	public static ApiUser find(String emailAddress) throws SQLException {
////		ThreadLocal<ApiUser> rec = new ThreadLocal<>();
//		AtomicReference<ApiUser> rec = new AtomicReference<>();
//		
//		Database.with((db, conn) -> {
////			DBCommand cmd = db.createCommand();
////			DBReader reader = new DBReader();
////
////			cmd.select(db.apiUsers.getColumns());
////			cmd.where(db.apiUsers.emailAddress.is(emailAddress));
////			reader.open(cmd, conn);
////			
////			ArrayList<ApiUser> users = reader.getBeanList(ApiUser.class, 1);
////			
////			if (users.size() > 0) {
////				rec.set(users.get(0));
////			}
////			
////			reader.close();
//			rec.set(find(emailAddress, db, conn));
//		});
//		
//		return rec.get();
		
		return Database.apply((db, conn) -> find(emailAddress, db, conn));
	}
	
}