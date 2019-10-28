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
import db.beans.ApiUser;
import db.beans.Limit;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;

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
	
	public ArrayList<Limit> getLimits() throws SQLException {
		if (limits == null) {
			limits = user.getPackage().getLimits();
		}
		
		return limits;
	}
	
	public ArrayList<Limit> getLimitsByTemporalUnit(String unit) throws SQLException {
		ArrayList<Limit> result = new ArrayList<>();
		
		for (Limit limit : getLimits()) {
			if (limit.getTemporalUnit().equalsIgnoreCase(unit)) {
				result.add(limit);
			}
		}
		
		return result;
	}
	
	public ArrayList<Limit> getMinuteLimits() throws SQLException {
		return getLimitsByTemporalUnit(Limit.MINUTE);
	}
	
	public ArrayList<Limit> getHourLimits() throws SQLException {
		return getLimitsByTemporalUnit(Limit.HOUR);
	}
	
	public ArrayList<Limit> getDayLimits() throws SQLException {
		return getLimitsByTemporalUnit(Limit.DAY);
	}
	
	public ArrayList<Limit> getMonthLimits() throws SQLException {
		return getLimitsByTemporalUnit(Limit.MONTH);
	}
	
	public void validatePerMinuteLimits(Database db, Connection conn) throws LimitsExceededException {
		DBCommand cmd = db.createCommand();

		cmd.select(db.requests.count());
		cmd.where(db.requests.remoteIp.is(request.getRemoteAddr())
				.or(db.requests.apiUserId.is(user.getId())),
				);
		
		int count = db.querySingleInt(cmd, 0, conn);
		
	}
	
}
