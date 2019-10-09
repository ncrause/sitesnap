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
package sitesnap.homepage;

import db.Database;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.empire.db.DBRecord;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.PackageResourceReference;
import sitesnap.Click;
import sitesnap.utils.PhotographRequest;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class WebsiteForm extends Form {
	
	private final WebsiteRequest websiteRequest;
	
	private UrlTextField urlField;
	
	private DropDownChoice monitorField;
	
	public WebsiteForm(String id) {
		super(id);
		
		websiteRequest = new WebsiteRequest();
		
		init();
	}

	@Override
	protected void onSubmit() {
		HashMap<String, Object> siteParams = new HashMap<>();
//		String nonce = UUID.randomUUID().toString();
		
		siteParams.put(PhotographRequest.KEY_URL, websiteRequest.getUrl());
		siteParams.put(PhotographRequest.KEY_MONITOR_SIZE, websiteRequest.getMonitorSize().size());
		siteParams.put(PhotographRequest.KEY_IMAGE_WIDTH, 640);
		siteParams.put(PhotographRequest.KEY_IMAGE_HEIGHT, 480);
		
		// the below isn't exposed to the servlet "click", so we can't use it.
//		Session.get().setAttribute(nonce, siteParams);
//		
//		// make absolutely certain the session is bound!
//		Session.get().bind();

		HttpServletRequest request = ((ServletWebRequest) getRequestCycle().getRequest())
				.getContainerRequest();
		HttpSession session = request.getSession();
		
		session.setAttribute(Click.SESSION_PHOTO_REQUEST, siteParams);
		
		try {
			// store it!
			long snapId = writeSnap(request);
			// persist the ID through to "click" so we can actually store the image file
			session.setAttribute(Click.SESSION_SNAP_ID, snapId);
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		
		replace(new ExternalImage("photograph", getWebResponse().encodeURL("/click")));
	}
	
	private void init() {
		add(createPlaceholderImage());
		add(createUrlField());
		add(createMonitorField());
	}
	
	private FormComponent createUrlField() {
		urlField = new UrlTextField("url", new PropertyModel(websiteRequest, "url"));
		
		urlField.setRequired(true);
		
		return urlField;
	}

	private FormComponent createMonitorField() {
		monitorField = new DropDownChoice("monitorSize", new PropertyModel(websiteRequest, "monitorSize"), MonitorSize.list(), new MonitorSizeRenderer());
		
		monitorField.setRequired(true);
		
		return monitorField;
	}

	private WebComponent createPlaceholderImage() {
		return new Image("photograph", new PackageResourceReference(WebsiteForm.class, "camera.svg"));
	}
	
	private long writeSnap(HttpServletRequest request) throws SQLException {
		ThreadLocal<Long> snapId = new ThreadLocal<>();
		
		Database.with((db, conn) -> {
			DBRecord rec = new DBRecord();
			
			rec.create(db.snaps);
			rec.setValue(db.snaps.targetUrl, websiteRequest.getUrl());
			rec.setValue(db.snaps.via, "web");
			rec.update(conn);
			
			snapId.set(rec.getLong(db.snaps.id));
			
			writeRequest(request, db, conn, snapId.get());
		});
		
		return snapId.get();
	}

	private void writeRequest(HttpServletRequest request, Database db, Connection conn, long snapId) {
		DBRecord rec = new DBRecord();
		
		rec.create(db.requests);
		rec.setValue(db.requests.snapId, snapId);
		rec.setValue(db.requests.remoteIp, request.getRemoteAddr());
		rec.update(conn);
			
		long requestId = rec.getLong(db.requests.id);
		
		writeHeaders(request, db, conn, requestId);
		writeParams(request, db, conn, requestId);
	}

	private void writeHeaders(HttpServletRequest request, Database db, Connection conn, long requestId) {
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

	private void writeParams(HttpServletRequest request, Database db, Connection conn, long requestId) {
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
