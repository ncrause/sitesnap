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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import lombok.Data;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class Limit {
	
	public static final String MINUTE = "minute".intern();
	
	public static final String HOUR = "hour".intern();
	
	public static final String DAY = "day".intern();
	
	public static final String MONTH = "month".intern();
	
	private long id;
	
	private long packageId;
	
	private String temporalUnit;
	
	private int temporalAmount;
	
	private long usageCap;
	
	private Timestamp dateCreated;
	
	/**
	 * Using the current date+time, this method will create a SQL timestamp
	 * representing the precending timeframe (i.e. "10 minutes" means 10
	 * minutes ago FROM NOW).
	 * 
	 * @return 
	 */
	public Timestamp toTimestamp() {
		Calendar cal = Calendar.getInstance();
		
		if (temporalUnit.equalsIgnoreCase(MINUTE)) {
			cal.add(Calendar.MINUTE, -temporalAmount);
		}
		else if (temporalUnit.equalsIgnoreCase(HOUR)) {
			cal.add(Calendar.MINUTE, -(temporalAmount * 60));
		}
		else if (temporalUnit.equalsIgnoreCase(DAY)) {
			cal.add(Calendar.HOUR, -(temporalAmount * 24));
		}
		else if (temporalUnit.equalsIgnoreCase(MONTH)) {
			cal.add(Calendar.DAY_OF_MONTH, -(temporalAmount * 30));
		}
		else {
			throw new RuntimeException("Unexpected temporal unit: " + temporalUnit);
		}
		
		return new Timestamp(cal.getTimeInMillis());
	}
	
	public static ArrayList<Limit> listByPackage(Package pkg, Database db, Connection conn) {
		DBCommand cmd = db.createCommand();
		DBReader reader = new DBReader();

		cmd.select(db.limits.getColumns());
		cmd.where(db.limits.packageId.is(pkg.getId()));
		reader.open(cmd, conn);

		try {
			return reader.getBeanList(Limit.class, 1);
		}
		finally {
			reader.close();
		}
	}
	
	public static ArrayList<Limit> listByPackage(Package pkg) throws SQLException {
		return Database.apply((db, conn) -> listByPackage(pkg, db, conn));
	}
	
}
