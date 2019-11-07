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
package sitesnap.pages.api.signuppage;

import db.beans.ActiveConnection;
import db.beans.ApiUser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.PropertyModel;
import sitesnap.utils.Mailer;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Log
public class SignupForm extends Form {
	
	private final SignupRequest signupRequest;
	
	private CheckBox acceptTermsField;
	
	private EmailTextField emailAddressField;
	
	public SignupForm(String id) {
		super(id);
		
		signupRequest = new SignupRequest();
		
		init();
	}

	@Override
	protected void onSubmit() {
		// although it looks a little off, we need a double "try" block like
		// this so we can explicitly rollback if the mailer fails.
		try (ActiveConnection activeConnection = new ActiveConnection()) {
			try {
				String password = UUID.randomUUID().toString();
				ApiUser user = new ApiUser();

				user.setEmailAddress(signupRequest.getEmailAddress());
				user.setPasswordHash(DigestUtils.sha256Hex(password));
				user.setPackageId(db.beans.Package.findByName("Free", activeConnection).getId());

				user.save(activeConnection);

				Mailer.configure((m) -> {
					m.setFrom("root@localhost")
							.setRecipient(signupRequest.getEmailAddress())
							.setSubject("SiteSnap API Key")
							.setContent("Your API Key: " + password);
				}).send();

				// only commit the change 
				activeConnection.getDatabase().commit(activeConnection.getConnection());

				getPage().replace(new Label("success", "Your API key has been emailed."));
			}
			// setRecipient above will throw a runtime exception, so catch it
			catch (RuntimeException | MessagingException ex) {
				activeConnection.getDatabase().rollback(activeConnection.getConnection());
				log.log(Level.SEVERE, null, ex);
				getPage().replace(new Label("error", ex.getMessage()));
			}
		} catch (SQLException | IOException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}

	private void init() {
		add(createAcceptTermsField());
		add(createEmailAddressField());
	}

	private FormComponent createAcceptTermsField() {
		acceptTermsField = new CheckBox("acceptTerms", new PropertyModel(signupRequest, "acceptTerms"));
		
		acceptTermsField.setRequired(true);
		
		return acceptTermsField;
	}

	private FormComponent createEmailAddressField() {
		emailAddressField = new EmailTextField("emailAddress", new PropertyModel(signupRequest, "emailAddress"));
		
		emailAddressField.setRequired(true);
		
		return emailAddressField;
	}
	
}
