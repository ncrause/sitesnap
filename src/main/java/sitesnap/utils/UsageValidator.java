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

import db.Database;
import db.beans.ActiveConnection;
import db.beans.ApiUser;
import db.beans.Limit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.apache.empire.db.DBCommand;

/**
 * This class provides a mechanism for querying recent usages of the API,
 * including both API user ID as well as IP, and validating if the current
 * request is still with usage limits.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class UsageValidator {
	
	private final ApiUser user;
	
	private final HttpServletRequest request;
	
	public UsageValidator(ApiUser user, HttpServletRequest request) {
		this.user = user;
		this.request = request;
	}
	
	private ArrayList<Limit> limits;
	
	public ArrayList<Limit> getLimits() throws SQLException, IOException {
		if (limits == null) {
			limits = user.getPackage().getLimits();
		}
		
		return limits;
	}
	
	public ArrayList<Limit> getLimitsByTemporalUnit(String unit) 
			throws SQLException, IOException {
		ArrayList<Limit> result = new ArrayList<>();
		
		for (Limit limit : getLimits()) {
			if (limit.getTemporalUnit().equalsIgnoreCase(unit)) {
				result.add(limit);
			}
		}
		
		return result;
	}
	
	public ArrayList<Limit> getMinuteLimits() 
			throws SQLException, IOException {
		return getLimitsByTemporalUnit(Limit.MINUTE);
	}
	
	public ArrayList<Limit> getHourLimits() throws SQLException, IOException {
		return getLimitsByTemporalUnit(Limit.HOUR);
	}
	
	public ArrayList<Limit> getDayLimits() throws SQLException, IOException {
		return getLimitsByTemporalUnit(Limit.DAY);
	}
	
	public ArrayList<Limit> getMonthLimits() throws SQLException, IOException {
		return getLimitsByTemporalUnit(Limit.MONTH);
	}
	
	public void validatePerMinuteLimits() 
			throws LimitsExceededException, SQLException, IOException {
		validateLimits(getMinuteLimits());
	}
	
	public void validatePerHourLimits() 
			throws LimitsExceededException, SQLException, IOException {
		validateLimits(getHourLimits());
	}
	
	public void validatePerDayLimits() 
			throws LimitsExceededException, SQLException, IOException {
		validateLimits(getDayLimits());
	}
	
	public void validatePerMonthLimits() 
			throws LimitsExceededException, SQLException, IOException {
		validateLimits(getMonthLimits());
	}
	
	public void validate() 
			throws LimitsExceededException, SQLException, IOException {
		validatePerMinuteLimits();
		validatePerHourLimits();
		validatePerDayLimits();
		validatePerMonthLimits();
	}
	
	private void validateLimits(ArrayList<Limit> limits) 
			throws LimitsExceededException, SQLException, IOException {
		try (ActiveConnection activeConnection = new ActiveConnection()) {
			Database db = activeConnection.getDatabase();
			Connection conn = activeConnection.getConnection();
			
			DBCommand cmd = db.createCommand();
			for (Limit limit : limits) {
				cmd.clear();		// always reset the command between iterations

				cmd.select(db.requests.count());
				cmd.where(db.requests.remoteIp.is(request.getRemoteAddr())
						.or(db.requests.apiUserId.is(user.getId())),
						db.requests.dateCreated.isMoreOrEqual(limit.toTimestamp()));

				long count = db.querySingleLong(cmd, 0, conn);

				if (count > limit.getUsageCap()) {
					throw new LimitsExceededException();
				}
			}
		}
	}
	
}
