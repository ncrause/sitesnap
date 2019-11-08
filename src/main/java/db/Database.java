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

}
