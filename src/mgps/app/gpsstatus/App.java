package mgps.app.gpsstatus;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class App extends Application {
	  public enum TrackerName {
		    APP_TRACKER, // Tracker used only in this app.
		    GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		    ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
		  }

		  HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
		  synchronized Tracker getTracker(TrackerName trackerId) {
			    if (!mTrackers.containsKey(trackerId)) {

			      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			      Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker("UA-56402148-1")
			          : analytics.newTracker("UA-56402148-1") ;
			      mTrackers.put(trackerId, t);

			    }
			    return mTrackers.get(trackerId);
			  }
}
