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
package sitesnap.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class Login {
	
	private String username;
	
	private String password;
	
	@Override
	public String toString() {
		return getUsername() + ":" + getPassword();
	}
	
	public static Login fromHeader(String authorization) {
		Pattern pattern = Pattern.compile("^Basic (.+)$");
		Matcher matcher = pattern.matcher(authorization);
		
		if (!matcher.find()) {
			return null;
		}
		
		String[] parts = new String(Base64.decodeBase64(matcher.group(1))).split(":");
		Login instance = new Login();
		
		instance.setUsername(parts[0]);
		instance.setPassword(parts[1]);
		
		return instance;
	}
	
}
