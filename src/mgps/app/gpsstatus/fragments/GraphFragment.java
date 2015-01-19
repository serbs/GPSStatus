package mgps.app.gpsstatus.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mgps.app.gpsstatus.BarGraphView;
import mgps.app.gpsstatus.GraphView;
import mgps.app.gpsstatus.GraphView.GraphViewData;
import mgps.app.gpsstatus.GraphViewDataInterface;
import mgps.app.gpsstatus.GraphViewSeries.GraphViewSeriesStyle;
import mgps.app.gpsstatus.GraphViewSeries;
import mgps.app.gpsstatus.GraphViewStyle.GridStyle;
import mgps.app.gpsstatus.R;
import mgps.app.gpsstatus.ValueDependentColor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GraphFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private int Satellites;
	private LocationManager locManager;
	private double Lat = 0;
	private double Long = 0;
	private GraphView gv;
	private double Altitude = 0;
	private double Speed = 0;
	private float Accuracy = 0;
	Iterable<GpsSatellite> sats;
	List<GpsSatellite> satsInFix;
	List<GpsSatellite> satsAll;
	protected float Bearing;
	protected long GpsTime;
	View rootView;
	Timer tmr;

	GraphView graphView;
	protected Boolean lastFixed=false;
	protected Boolean isGPSFix;
	protected long mLastLocationMillis;
	final GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			 switch (event) {
	            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	                if (mLastLocation != null)
	                    isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;

	                if (isGPSFix!=lastFixed) { // A fix has been acquired.
	                	Intent i =new Intent();
						i.setAction("mgps.app.gpsstatus.ACTION_FIXCHANGE");
						i.putExtra("Fix", isGPSFix);
						LocalBroadcastManager.getInstance(rootView.getContext()).sendBroadcast(i);
						lastFixed=isGPSFix;
	                }

	                break;
	            case GpsStatus.GPS_EVENT_FIRST_FIX:
	                // Do something.
	                isGPSFix = true;

	                break;
	        }
			if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS
					|| event == GpsStatus.GPS_EVENT_FIRST_FIX) {
				GpsStatus status = locManager.getGpsStatus(null);
				sats = status.getSatellites();
				// Check number of satellites in list to determine fix state
				Satellites = 0;
				int ii = 0;
				satsInFix = new ArrayList<GpsSatellite>(255);
				satsAll= new ArrayList<GpsSatellite>(255);
				double maxSnr=50;
				for (GpsSatellite sat : sats) {
					if( sat.getSnr()==0){
						continue;
					}
					if (sat.usedInFix()) {

						satsInFix.add(ii, sat);
						ii++;
					}
					
					satsAll.add(Satellites,sat);
					Satellites++;

				}
				if (Satellites > 0) {
					// GraphViewData[] dataFix = new
					// GraphViewData[satsInFix.size()];
					GraphViewData[] data = new GraphViewData[Satellites];
					int i = 1;
					
					String[] xLabels=new String[Satellites];
					
					for (GpsSatellite sat : sats) {
						/*
						 * if(sat.usedInFix()) { dataFix[j]= new
						 * GraphViewData(i, sat.getSnr());
						 * 
						 * j++; } else { data[k] = new GraphViewData(i,
						 * sat.getSnr()); k++; }
						 */
						if( sat.getSnr()==0){
							continue;
						}
						data[i-1] = new GraphViewData(i, sat.getSnr());
						xLabels[i-1]=String.valueOf(sat.getPrn());
						
						if(sat.getSnr()>50){
							maxSnr=100;
						}
						i++;
					}

					/*
					 * GraphViewSeriesStyle style = new GraphViewSeriesStyle();
					 * GraphViewSeriesStyle styleFix = new
					 * GraphViewSeriesStyle(); style.color=Color.GRAY;
					 * style.thickness=3; styleFix.color=Color.GREEN;
					 * styleFix.thickness=3;
					 * 
					 * GraphViewSeries gvsFix=new
					 * GraphViewSeries("Used in fix",styleFix,dataFix);
					 */
					// graph with dynamically genereated horizontal and
					// vertical
					// labels
					GraphViewSeriesStyle style = new GraphViewSeriesStyle();
					style.setValueDependentColor(new ValueDependentColor() {

						@Override
						public int get(GraphViewDataInterface data) {
							if (satsAll.size() > 0) {
								if (satsAll.get((int) data.getX()-1).usedInFix()) {
									return Color.rgb(0, (int)(250-((data.getY()/50)*250)), 0);

								} else {
									return Color.GRAY;
								}
							}
							return Color.GREEN;
						}
					});
					GraphViewSeries gvs = new GraphViewSeries(rootView.getContext().getString(R.string.txtSat),
							style,
							data);
					graphView.removeAllSeries();
					graphView.addSeries(gvs);
					graphView.setHorizontalLabels(xLabels);
					graphView.setManualYMaxBound(maxSnr);
					graphView.setManualYAxis(true);
					graphView.getGraphViewStyle().setNumHorizontalLabels(Satellites);
					
					// graphView.addSeries(gvsFix);
					// set view port, start=2, size=10
				//	int maX=(int)(satsAll.size()/2)+2;
					graphView.setViewPort(0, Satellites);
				
					
					graphView.getGraphViewStyle().setNumVerticalLabels(11);
					
					Intent intent =new Intent();
					intent.setAction("mgps.app.gpsstatus.ACTION_NEWSTATUS");
					String satss=String.valueOf(satsAll.size())+"|"+String.valueOf(satsInFix.size());
					intent.putExtra("Status",satss );
					LocalBroadcastManager.getInstance(rootView.getContext()).sendBroadcast(intent);
					
					
					
			
				
				}

			}
		}
	};
	protected Location mLastLocation;
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static GraphFragment newInstance(int sectionNumber) {
		GraphFragment fragment = new GraphFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public GraphFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main, container, false);

		graphView = new BarGraphView(rootView.getContext() // context

				, rootView.getContext().getString(R.string.txtSat) // heading

		);
		graphView.setViewPort(0, 15);
		graphView.getGraphViewStyle().setNumVerticalLabels(11);
		graphView.getGraphViewStyle().setNumHorizontalLabels(16);
		graphView.setManualYAxisBounds(50, 0);
		graphView.setManualYAxis(true);
		graphView.getGraphViewStyle().setTextSize(15f);
	
		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph);
		layout.addView(graphView);
		
		locManager = (LocationManager) rootView.getContext().getSystemService(
				Context.LOCATION_SERVICE);
		// Set up the action bar.
		locManager.requestLocationUpdates(locManager.GPS_PROVIDER, 0, 0,
				new LocationListener() {

					@Override
					public void onLocationChanged(Location location) {
						Lat = location.getLatitude();
						Long = location.getLongitude();
						Altitude = location.getAltitude();
						Accuracy = location.getAccuracy();
						Speed = location.getSpeed() * 3.6;
						Bearing = location.getBearing();
						GpsTime = location.getTime();
						
						Intent i =new Intent();
						i.setAction("mgps.app.gpsstatus.ACTION_NEWLOC");
						i.putExtra("Location", location);
						LocalBroadcastManager.getInstance(rootView.getContext()).sendBroadcast(i);
						mLastLocation=location;
						mLastLocationMillis = SystemClock.elapsedRealtime();;
						
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						
						

					}

					@Override
					public void onProviderEnabled(String provider) {
						Intent i =new Intent();
						i.setAction("mgps.app.gpsstatus.ACTION_STATUSCHANGE");
						i.putExtra("Enabled", true);
						LocalBroadcastManager.getInstance(rootView.getContext()).sendBroadcast(i);

					}

					@Override
					public void onProviderDisabled(String provider) {
						Intent i =new Intent();
						i.setAction("mgps.app.gpsstatus.ACTION_STATUSCHANGE");
						i.putExtra("Enabled", false);
						LocalBroadcastManager.getInstance(rootView.getContext()).sendBroadcast(i);

					}
				});
		tmr = new Timer();
		/*
		 * tmr.schedule(new TimerTask() {
		 * 
		 * @Override public void run() {
		 * 
		 * getActivity().runOnUiThread(new Runnable() {
		 * 
		 * @Override public void run() { if (Satellites > 0) { int num = 1000;
		 * GraphViewData[] dataFix = new GraphViewData[satsInFix.size()];
		 * GraphViewData[] data = new
		 * GraphViewData[Satellites-satsInFix.size()]; int i = 0; int j=0; int
		 * k=0;
		 * 
		 * for (GpsSatellite sat : sats) { if(sat.usedInFix()) { dataFix[j]= new
		 * GraphViewData(i, sat.getSnr()); j++; } else { data[k] = new
		 * GraphViewData(i, sat.getSnr()); k++; } i++; } GraphViewSeries gvs=new
		 * GraphViewSeries(data); GraphViewSeries gvsFix=new
		 * GraphViewSeries(dataFix); // graph with dynamically genereated
		 * horizontal and // vertical // labels gvs.getStyle().color=Color.GRAY;
		 * gvsFix.getStyle().color=Color.GREEN; graphView.removeAllSeries();
		 * graphView.addSeries(gvs); graphView.addSeries(gvsFix); // set view
		 * port, start=2, size=10 graphView.setViewPort(1, 20); //
		 * graphView.setScalable(true); // graphView.setDrawBackground(true);
		 * 
		 * }
		 * 
		 * } }); } }, 1000, 20000);
		 */
		locManager.addGpsStatusListener(gpsStatusListener);
		return rootView;
	}
}
