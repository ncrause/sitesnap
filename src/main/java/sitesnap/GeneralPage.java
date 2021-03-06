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

import sitesnap.pages.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import sitesnap.pages.api.SignupPage;
import sitesnap.pages.api.UsagePage;

/**
 * Base-class for pages which share the same basic template. It is marked as
 * abstract because we explicitly don't want to be able to instantiate it
 * directly.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public abstract class GeneralPage extends WebPage {
	
	public GeneralPage(final PageParameters parameters) {
		super(parameters);
		
		init();
	}
	
	private void init() {
		addMenuItems();
		addContentItems();
		addFooterItems();
	}

	private void addMenuItems() {
		add(new Link("homeURL") {
			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});
		add(new Link("apiUsageURL") {
			@Override
			public void onClick() {
				setResponsePage(UsagePage.class);
			}
		});
		add(new Link("apiSignupURL") {
			@Override
			public void onClick() {
				setResponsePage(SignupPage.class);
			}
		});
	}
	
	private void addContentItems() {
		Label error = new Label("error");
		
		error.setVisible(false);
		
		add(error);
		
		Label success = new Label("success");
		
		success.setVisible(false);
		
		add(success);
	}
	
	private void addFooterItems() {
		add(new Image("wicketLogo", new PackageResourceReference(GeneralPage.class, "Apache-Wicket.svg")));
		add(new Image("empiredbLogo", new PackageResourceReference(GeneralPage.class, "empiredb.svg")));
	}
	
}
