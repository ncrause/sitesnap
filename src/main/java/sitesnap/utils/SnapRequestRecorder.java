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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.empire.db.DBRecord;

/**
 * This class provides a mechanism for recording the structures around a
 * snap request.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class SnapRequestRecorder {
	
	private final String url;
	
	private final String via;
	
	public SnapRequestRecorder(String url, String via) {
		this.url = url;
		this.via = via;
	}
	
	@Getter @Setter
	private ApiUser user;
	
	public long writeSnap(HttpServletRequest request) throws SQLException {
		// TODO: Test if this can be changed to AtomicReference instead
//		ThreadLocal<Long> snapId = new ThreadLocal<>();
		AtomicLong snapId = new AtomicLong();
		
		Database.with((db, conn) -> {
			DBRecord rec = new DBRecord();
			
			rec.create(db.snaps);
			rec.setValue(db.snaps.targetUrl, url);
			rec.setValue(db.snaps.via, via);
			rec.update(conn);
			
			snapId.set(rec.getLong(db.snaps.id));
			
			writeRequest(request, db, conn, snapId.get());
		});
		
		return snapId.get();
	}

	protected void writeRequest(HttpServletRequest request, Database db, Connection conn, long snapId) {
		DBRecord rec = new DBRecord();
		
		rec.create(db.requests);
		rec.setValue(db.requests.snapId, snapId);
		rec.setValue(db.requests.remoteIp, request.getRemoteAddr());
		
		if (user != null) {
			rec.setValue(db.requests.apiUserId, user.getId());
		}
		
		rec.update(conn);
			
		long requestId = rec.getLong(db.requests.id);
		
		writeHeaders(request, db, conn, requestId);
		writeParams(request, db, conn, requestId);
	}

	protected void writeHeaders(HttpServletRequest request, Database db, Connection conn, long requestId) {
		for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements(); ) {
			String name = names.nextElement();
			
			for (Enumeration<String> values = request.getHeaders(name); values.hasMoreElements(); ) {
				String value = values.nextElement();
				DBRecord rec = new DBRecord();

				rec.create(db.requestData);
				rec.setValue(db.requestData.requestId, requestId);
				rec.setValue(db.requestData.type, "header");
				rec.setValue(db.requestData.name, name);
				rec.setValue(db.requestData.value, value);
				rec.update(conn);
			}
		}
	}

	protected void writeParams(HttpServletRequest request, Database db, Connection conn, long requestId) {
		for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements(); ) {
			String name = names.nextElement();
			
			for (String value : request.getParameterValues(name)) {
				DBRecord rec = new DBRecord();

				rec.create(db.requestData);
				rec.setValue(db.requestData.requestId, requestId);
				rec.setValue(db.requestData.type, "parameter");
				rec.setValue(db.requestData.name, name);
				rec.setValue(db.requestData.value, value);
				rec.update(conn);
			}
		}
	}
	
}
