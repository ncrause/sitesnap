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

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
@Data
public class Dimension implements Serializable {
	
	private static final long serialVersionUID = 889819754762705608L;
	
	private final int width;
	
	private final int height;
	
	public double getAspectRatio() {
		return (double) width / (double) height;
	}
	
}
