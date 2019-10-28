package sitesnap;

import sitesnap.pages.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.flywaydb.core.Flyway;
import db.Connections;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.wicket.RuntimeConfigurationType;
import org.flywaydb.core.api.configuration.FluentConfiguration;

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
		
		doMigration();
	}
	
	

	private void doMigration() throws SQLException {
		FluentConfiguration config = Flyway.configure();
		
		config.dataSource(Connections.getDataSource())
				.placeholders(getPlaceholders());
		
		Flyway flyway = config.load();
		
		flyway.migrate();
	}
	
	/**
	 * Builds a Map of default placeholders to be used during migrations
	 * 
	 * @return 
	 */
	private Map<String, String> getPlaceholders() throws SQLException {
		Map<String, String> placeholders = new HashMap<>();
		
		placeholders.put("blob_type", Connections.getBinaryType());
		placeholders.put("timestamp_type", Connections.getTimestampType());
		placeholders.put("network_addr_type", Connections.getNetworkAddressType());
		
		return placeholders;
	}
}
