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

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import lombok.extern.java.Log;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Log
public class Mailer {
	
	private final Message message;
	
	private Mailer() throws NamingException {
		message = new MimeMessage(Jndi.getMailSession());
	}
	
	public static Mailer configure(Consumer<Mailer> func) {
		try {
			Mailer instance = new Mailer();
			
			func.accept(instance);
			
			return instance;
		} 
		catch (NamingException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}
	
	public Mailer setFrom(String address) {
		try {
			message.setFrom(new InternetAddress(address));
		} 
		catch (MessagingException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
		
		return this;
	}
	
	public Mailer setRecipient(String address) {
		try {
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
		} 
		catch (MessagingException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
		
		return this;
	}
	
	public Mailer setSubject(String subject) {
		try {
			message.setSubject(subject);
		} 
		catch (MessagingException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
		
		return this;
	}
	
	public Mailer setContent(String body) {
		try {
			message.setContent(body, "text/plain");
		} 
		catch (MessagingException ex) {
			log.log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
		
		return this;
	}
	
	public void send() throws MessagingException {
		Transport.send(message);
	}
	
}
