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
package sitesnap.api;

import db.beans.ApiUser;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import sitesnap.beans.ApiPhotographRequest;
import sitesnap.beans.Login;
import sitesnap.utils.ImageEncoder;
import sitesnap.utils.LimitsExceededException;
import sitesnap.utils.Photographer;
import sitesnap.utils.SnapRequestRecorder;
import sitesnap.utils.UsageValidator;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Log
public class PhotographServlet extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get the authorization header
		String auth = request.getHeader("Authorization");
		
		if (auth == null) {
			log.severe("Missing authorization");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Login login = Login.fromHeader(auth);
		
		if (login == null) {
			log.severe("Malformed authorization");
			response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
			return;
		}
		
		ApiUser user;
		
		try {
			user = ApiUser.find(login.getUsername());
		}
		catch (SQLException ex) {
			throw new ServletException(ex);
		}
			
		if (user == null) {
			log.severe("No such email address");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (!user.isPasswordValid(login.getPassword())) {
			log.severe("Authorization failed");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		UsageValidator usageValidator = new UsageValidator(user, request);
		
		try {
			usageValidator.validate();
		}
		catch (LimitsExceededException ex) {
			log.log(Level.SEVERE, "Usage limits exceeded", ex);
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		} 
		catch (SQLException ex) {
			throw new ServletException(ex);
		}
		
		ApiPhotographRequest apiRequest;
		
		try {
			apiRequest = ApiPhotographRequest.parse(request);
		}
		catch (NoSuchFieldException ex) {
			log.log(Level.SEVERE, "Missing parameter when parsing API request", ex);
			response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
			return;
		}
		catch (IllegalArgumentException | MalformedURLException ex) {
			log.log(Level.SEVERE, "Bad parameter when parsing API request", ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		Photographer photographer = new Photographer();
		BufferedImage image;
		
		try {
			image = photographer.click(apiRequest.toPhotographRequest());
		}
		catch (InterruptedException ex) {
			throw new ServletException(ex);
		}
		
		ImageEncoder encoder = new ImageEncoder();
		byte[] bytes = encoder.toPNG(image);
		SnapRequestRecorder recorder = new SnapRequestRecorder(apiRequest.getUrl().toExternalForm(), "api");
		
		recorder.setUser(user);
		recorder.setPhoto(bytes);
		
		try {
			recorder.writeSnap(request);
		} 
		catch (SQLException ex) {
			throw new ServletException(ex);
		}
		
		response.setContentType("image/png");
		response.getOutputStream().write(bytes);
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "API end-point for getting a screenshot";
	}// </editor-fold>

}
