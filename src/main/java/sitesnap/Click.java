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
package sitesnap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sitesnap.utils.ImageEncoder;
import sitesnap.utils.PhotographRequest;
import sitesnap.utils.Photographer;

/**
 * This servlet actually takes the snapshot of a website and send it to the
 * browser. In order to prevent abuse of this URL, however, we cannot simply
 * take in parameter ad-doc and generate output. Instead, we are passed a
 * single parameter (nonce) which references a session variable (of a HashMap)
 * which contains the actual parameters. In this way, we can at least make it
 * a <i>little</i> more challenging for abusers to misuse it.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class Click extends HttpServlet {
	
	public static final String PARAM_NONCE = "nonce".intern();
	
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
		String nonce = request.getParameter(PARAM_NONCE);
		
		if (nonce == null) {
			response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Missing 'nonce'");
			return;
		}
		
		Map<String, Object> siteParams = (Map) request.getSession().getAttribute(nonce);
		
		if (siteParams == null) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "No such 'nonce'");
			return;
		}
		
		PhotographRequest photoRequest = PhotographRequest.fromMap(siteParams);
		
		// remove the session variable right away, so we prevent reload/playback
		request.getSession().removeAttribute(PARAM_NONCE);
		
		// Now go get the snapshot!
		Photographer photographer = new Photographer();
		BufferedImage image = photographer.click(photoRequest);
		ImageEncoder encoder = new ImageEncoder();
		
		response.setContentType("image/jpeg");
		response.getOutputStream().write(encoder.toJPG(image));
	}
	
	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Takes a snapshot of a website, and renders it as a JPG";
	}

}
