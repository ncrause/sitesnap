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

/**
 * This is just the default "free" limits. Although this data exists in the
 * "limits" table, it's just easier to quickly reference them here when
 * rendering the HTML page on the public-facing side.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public interface ApiLimits {
	
	public static final long PER_MINUTE = 10;
	
	/**
	 * Actually 60 minutes
	 */
	public static final long PER_HOUR = 500;
	
	/**
	 * Actually 24 hours
	 */
	public static final long PER_DAY = 10000;
	
	/**
	 * Actually 30 days
	 */
	public static final long PER_MONTH = 250000;
	
}
