package mgps.app.gpsstatus.fragments;
import mgps.app.gpsstatus.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class Sats extends Fragment {
	View rootView ;
	ImageView  map;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_satellites, container, false);
		map=(ImageView) rootView.findViewById(R.id.ivMap);
		map.setImageResource(R.drawable.sectors);
		
		return super.onCreateView(inflater, container, savedInstanceState);
		
		
	}

	
	
	
}
