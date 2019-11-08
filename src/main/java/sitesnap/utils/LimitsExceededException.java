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

/**
 * Throws if an API user is attempting to exceed their usage limits.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class LimitsExceededException extends Exception {

	/**
	 * Creates a new instance of <code>LimitsExceededException</code> without
	 * detail message.
	 */
	public LimitsExceededException() {
	}

	/**
	 * Constructs an instance of <code>LimitsExceededException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public LimitsExceededException(String msg) {
		super(msg);
	}
}
