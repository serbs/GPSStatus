package mgps.app.gpsstatus.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mgps.app.gpsstatus.NetworkManager;
import mgps.app.gpsstatus.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Status extends Fragment {
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Boolean fixed = false;

	/**
	 * The fragment argument representing the section number for this fragment.
	 */

	private class locReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.getAction() == "mgps.app.gpsstatus.ACTION_NEWLOC") {
					Bundle b = intent.getExtras();
					Location loc = (Location) b.get("Location");
					location = loc;
					if (loc != null){
						
						fixed = true;
					}
					else
						fixed = false;
				} else if (intent.getAction() == "mgps.app.gpsstatus.ACTION_NEWSTATUS") {

					Bundle b = intent.getExtras();
					String status = String.valueOf(b.get("Status"));
					sats = status;

				} else if (intent.getAction() == "mgps.app.gpsstatus.ACTION_FIXCHANGE") {

					Bundle b = intent.getExtras();
					Boolean status = b.getBoolean("Fix");
					fixed = status;
				} else {
					Bundle b = intent.getExtras();
					if (b.get("newstatus") != null) {
						int Status = Integer.valueOf((String) b
								.get("newstatus"));
					}

				}
				RefreshUI();
			} catch (Exception ee) {

			}
		}

	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	private void RefreshUI() {
		// tvSats.setText(String);
		try {
			if (location != null) {
				tvAcc.setText(String.valueOf(round(location.getAccuracy(),2))+" m");
				tvAlt.setText(String.valueOf(round(location.getAltitude(),2)));
				tvLat.setText(String.valueOf(round(location.getLatitude(),6)));
				tvLon.setText(String.valueOf(round(location.getLongitude(),6)));
				tvSpd.setText(String.valueOf(round(location.getSpeed() * 3.6f,2))
						+ "km/h");
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", Locale.US);
				tvDate.setText(sdf.format(new Date(location.getTime())));
			}
			tvSats.setText(sats);
			if (fixed) {
				tvFixed.setText(rootView.getContext()
						.getText(android.R.string.ok));
			} else {
				tvFixed.setText(rootView.getContext().getText(
						R.string.txtFixFail));
			}
			try {
				LocationManager locManager = (LocationManager) rootView.getContext()
						.getSystemService(Context.LOCATION_SERVICE);
				if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					tvEnabled.setText(rootView.getContext().getText(
							R.string.txtOn));
				} else {
					fixed = false;
					tvFixed.setText(rootView.getContext().getText(
							R.string.txtFixFail));
					tvEnabled.setText(rootView.getContext().getText(
							R.string.txtOff));
				}
			} catch (Exception bb) {
			}
		} catch (Exception e) {

		}
	}

	public static void downloadGPSXtra(Context context) {

		LocationManager locationmanager = (LocationManager) context
				.getSystemService("location");
		Bundle bundle = new Bundle();
		locationmanager.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_xtra_injection", bundle);
		locationmanager.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_time_injection", bundle);

	}

	public static void deleteExtras(Context context) {
		LocationManager locationmanager = (LocationManager) context
				.getSystemService("location");
		locationmanager.sendExtraCommand(LocationManager.GPS_PROVIDER,
				"delete_aiding_data", null);
	}

	private static final String ARG_SECTION_NUMBER = "section_number";
	private Location location;
	private String sats;
	private int status;
	Status me;

	Button btnDelAgps;
	Button btnDownloadAgps;
	TextView tvSats;
	TextView tvAcc;
	TextView tvAlt;
	TextView tvLat;
	TextView tvLon;
	TextView tvSpd;
	TextView tvDate;
	TextView tvEnabled;
	TextView tvFixed;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Status newInstance(int sectionNumber) {
		Status fragment = new Status();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private View rootView;

	public Status() {
		me = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_status, container, false);
		 btnDelAgps=(Button) rootView.findViewById(R.id.btnClearAGPS);
		 btnDownloadAgps=(Button) rootView.findViewById(R.id.btnDownLodAGPS);
		 btnDelAgps.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try{
					Status.deleteExtras(rootView.getContext());
					Toast.makeText(rootView.getContext(),rootView.getContext().getText(R.string.txtDelAGPSDel), Toast.LENGTH_LONG).show();
				}
				catch(Exception e)
				{
					Toast.makeText(getActivity(),"Failed!", Toast.LENGTH_LONG).show();
				}
			}});
		 
		 btnDownloadAgps.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					try{
						if(!NetworkManager.isInetAvailable(getActivity())){
							Toast.makeText(getActivity(),getActivity().getText(R.string.txtNoInetAvailable), Toast.LENGTH_LONG).show();
							return;
						}
						Status.downloadGPSXtra(getActivity());
						Toast.makeText(getActivity(),getActivity().getText(R.string.txtGetAGPSGot), Toast.LENGTH_LONG).show();
					}
					catch(Exception e)
					{
						Toast.makeText(getActivity(),"Failed!", Toast.LENGTH_LONG).show();
					}
				}});
		tvSats = (TextView) rootView.findViewById(R.id.tvValSats);
		tvAcc = (TextView) rootView.findViewById(R.id.tvValAcc);
		tvAlt = (TextView) rootView.findViewById(R.id.tvValAlt);
		tvLat = (TextView) rootView.findViewById(R.id.tvValLat);
		tvLon = (TextView) rootView.findViewById(R.id.tvValLon);
		tvSpd = (TextView) rootView.findViewById(R.id.tvValSpd);
		tvDate = (TextView) rootView.findViewById(R.id.tvValDate);
		tvEnabled = (TextView) rootView.findViewById(R.id.tvValEnabled);
		tvFixed = (TextView) rootView.findViewById(R.id.tvValFixed);
		locReceiver lr = new locReceiver();
		IntentFilter ifilt = new IntentFilter();
		ifilt.addAction("mgps.app.gpsstatus.ACTION_NEWLOC");
		ifilt.addAction("mgps.app.gpsstatus.ACTION_STATUSCHANGE");
		ifilt.addAction("mgps.app.gpsstatus.ACTION_NEWSTATUS");
		ifilt.addAction("mgps.app.gpsstatus.ACTION_FIXCHANGE");

		LocalBroadcastManager.getInstance(rootView.getContext())
				.registerReceiver(lr, ifilt);

		return rootView;
	}


}
