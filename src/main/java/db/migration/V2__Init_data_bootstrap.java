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
package db.migration;

import db.beans.Limit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import sitesnap.ApiLimits;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class V2__Init_data_bootstrap extends BaseJavaMigration {
	
	private static final String UNIT = "unit".intern();
	
	private static final String AMOUNT = "amount".intern();
	
	private static final String CAP = "cap".intern();

	@Override
	public void migrate(Context context) throws Exception {
		AtomicLong packageId = new AtomicLong();
		
		// For now, only need one free package
		try (PreparedStatement statement = 
				context.getConnection()
						.prepareStatement("insert into packages (name) values ('Free');", new String[] {"id"})) {
			statement.execute();
			
			try (ResultSet results = statement.getGeneratedKeys()) {
				results.next();
				packageId.set(results.getLong(1));
			}
		}

		// basic limits for the free package
		for (Map<String, Object> limits : getLimits()) {
			try (PreparedStatement statement = 
					context.getConnection()
							.prepareStatement("insert into limits (package_id, temporal_unit, temporal_amount, usage_cap) values(?, ?, ?, ?);")) {
				statement.setLong(1, packageId.get());
				statement.setString(2, (String) limits.get(UNIT));
				statement.setInt(3, (Integer) limits.get(AMOUNT));
				statement.setLong(4, (Long) limits.get(CAP));
				
				statement.execute();
			}
		}
		
		// Just me - password is "change"
		try (PreparedStatement statement = 
				context.getConnection()
						.prepareStatement("insert into api_users (email_address, password_hash, package_id) values ('nathan@crause.name', '057ba03d6c44104863dc7361fe4578965d1887360f90a0895882e58a6248fc86', ?);")) {
			statement.setLong(1, packageId.get());
			
			statement.execute();
		}
				
	}
	
	private List<Map> getLimits() {
		List<Map> list = new ArrayList<>();
		
		list.add(limit(Limit.MINUTE, 1, ApiLimits.PER_MINUTE));
		list.add(limit(Limit.HOUR, 1, ApiLimits.PER_HOUR));
		list.add(limit(Limit.DAY, 1, ApiLimits.PER_DAY));
		list.add(limit(Limit.MONTH, 1, ApiLimits.PER_MONTH));
		
		return list;
	}
	
	private Map<String, Object> limit(String temporalUnit, int temporalAmount, long usageCap) {
		Map<String, Object> map = new HashMap<>();
		
		map.put(UNIT, temporalUnit);
		map.put(AMOUNT, temporalAmount);
		map.put(CAP, usageCap);
		
		return map;
	}
	
}
