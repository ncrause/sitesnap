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

import org.apache.empire.data.DataMode;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBDatabase;
import org.apache.empire.db.DBTable;
import org.apache.empire.db.DBTableColumn;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class ApiUsers extends DBTable {
	
	public DBTableColumn id;
	public DBTableColumn emailAddress;
	public DBTableColumn passwordHash;
	public DBTableColumn packageId;
	public DBTableColumn dateCreated;
	
	public ApiUsers(DBDatabase db) {
		super("api_users", db);
		
		id = addColumn("id", DataType.AUTOINC, 0, true, DataMode.AutoGenerated);
		emailAddress = addColumn("email_address", DataType.TEXT, 255, true);
		passwordHash = addColumn("password_hash", DataType.TEXT, 64, true);
		packageId = addColumn("package_id", DataType.INTEGER, 0, true);
		dateCreated = addColumn("date_created", DataType.DATETIME, 0, false);
		
		setPrimaryKey(id);
	}
	
}
