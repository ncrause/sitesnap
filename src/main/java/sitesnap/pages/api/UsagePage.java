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
package sitesnap.pages.api;

import java.text.NumberFormat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import sitesnap.ApiLimits;
import sitesnap.GeneralPage;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class UsagePage extends GeneralPage implements ApiLimits {
	
	public UsagePage(PageParameters parameters) {
		super(parameters);
		
		init();
	}

	private void init() {
		add(new Label("perMinute", format(PER_MINUTE)));
		add(new Label("perHour", format(PER_HOUR)));
		add(new Label("perDay", format(PER_DAY)));
		add(new Label("perMonth", format(PER_MONTH)));
	}
	
	private static String format(int value) {
		NumberFormat fmt = NumberFormat.getIntegerInstance();
		
		return fmt.format(value);
	}
	
}