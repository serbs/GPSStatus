package mgps.app.gpsstatus;

import java.util.Date;

import com.google.android.gms.ads.AdView;

import mgps.app.gpsstatus.fragments.GraphFragment;
import mgps.app.gpsstatus.fragments.Sats;
import mgps.app.gpsstatus.fragments.Status;

import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;


public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */

	TabsAdapter mSectionsPagerAdapter;
	Intent share;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mSectionsPagerAdapter = new TabsAdapter(this, mViewPager);

		// Set up the ViewPager with the sections adapter.

		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {

						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		// for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
		// Create a tab with text corresponding to the page title defined by
		// the adapter. Also specify this Activity object, which implements
		// the TabListener interface, as the callback (listener) for when
		// this tab is selected.
		mSectionsPagerAdapter.addTab(actionBar.newTab().setText(getString(R.string.txtSat)),
				GraphFragment.class, null);
		mSectionsPagerAdapter.addTab(actionBar.newTab().setText(getString(R.string.txtStatus)),
				Status.class, null);
		
		// }
		
		locReceiver lr = new locReceiver();
		IntentFilter ifilt = new IntentFilter();
		ifilt.addAction("mgps.app.gpsstatus.ACTION_NEWLOC");
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(lr, ifilt);

	}
	private class locReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.getAction() == "mgps.app.gpsstatus.ACTION_NEWLOC"&&shareItem.isEnabled()==false) {
					Bundle b = intent.getExtras();
					Location loc = (Location) b.get("Location");
					
					if (loc != null){
						shareItem.setEnabled(true);
						share.removeExtra(Intent.EXTRA_TEXT);
						  String lat=String.valueOf(loc.getLatitude());
					      String lon=String.valueOf(loc.getLongitude());
					    
					      String find=lat+","+lon;
					      
					     
					      share.putExtra(Intent.EXTRA_TEXT, " http://maps.google.com/maps?q="+find+"&ll="+find+"&z=17");
						 mShareActionProvider.setShareIntent(share);
					}
					
				}
			} catch (Exception ee) {

			}
		}

	}

	private android.support.v7.widget.ShareActionProvider mShareActionProvider;
	private MenuItem shareItem;

	
	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		return intent;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Set up ShareActionProvider's default share intent

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		shareItem = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = new ShareActionProvider(this);
		MenuItemCompat.setActionProvider(shareItem, mShareActionProvider);
		
		share = getDefaultIntent();
		mShareActionProvider.setShareIntent(share);
		shareItem.setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	

}
