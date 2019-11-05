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

import java.util.logging.Level;
import javax.mail.MessagingException;
import lombok.extern.java.Log;
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
		try {
			Mailer.configure((m) -> {
				m.setFrom("root@localhost")
						.setRecipient(signupRequest.getEmailAddress())
						.setContent("Your API Key: ");
			}).send();
		}
		catch (MessagingException ex) {
			log.log(Level.SEVERE, null, ex);
			// TODO: make some sort of feedback area
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
