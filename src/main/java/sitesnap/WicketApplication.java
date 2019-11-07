package sitesnap;

import sitesnap.pages.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import db.Connections;
import java.sql.Connection;
import lombok.SneakyThrows;
import org.apache.wicket.RuntimeConfigurationType;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 *
 * @see sitesnap.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	
	// Special use connection just to keep memory database alive
	private Connection keepOpen;

	/**
	 * @return the class which will be used to render the default homepage
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@SneakyThrows
	@Override
	public void init() {
		super.init();

		// do any necessary database work, including running migrations and
		// configuring EmpireDB
		Connections.init(getConfigurationType());
		
		// if we're in development, open a persistent DB connection so that
		// we can keep any changes and data. Closing all connections causes all
		// of this to be lost
		if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
			keepOpen = Connections.get();
		}
		
		Connections.doMigration();
	}
}
