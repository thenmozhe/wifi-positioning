package edu.uta.wifisignalstrengthcollector;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ReportActivity extends ListActivity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(
				R.layout.report_list_header, lv, false);
		lv.addHeaderView(header, null, false);

		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
		// Get a cursor with all people
		AccessPointModel model = new AccessPointModel(this);
		model.open();
		accessPoints = model.fetchAll();
		model.close();
		
		if (accessPoints != null) {
			AccessPointArrayAdapter adapter = new AccessPointArrayAdapter(this,
					accessPoints);
			
			setListAdapter(adapter);
		}

	}

	ArrayList<AccessPoint> accessPoints;

	private class AccessPointArrayAdapter extends ArrayAdapter<AccessPoint> {
		private final Context context;
		private final ArrayList<AccessPoint> accessPoints;

		public AccessPointArrayAdapter(Context context,
				ArrayList<AccessPoint> accessPoints) {
			super(context, R.layout.report_list_item, accessPoints);
			this.context = context;
			this.accessPoints = accessPoints;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.report_list_item, parent,
					false);
			final AccessPoint accessPoint = accessPoints.get(position);

			TextView accessPointNameTextView = (TextView) rowView
					.findViewById(R.id.accessPointNameTextView);
			accessPointNameTextView.setText(accessPoint.getMacAddress());

			TextView signalLevelTextView = (TextView) rowView
					.findViewById(R.id.signalLevelTextView);
			int signalLevel = accessPoint.getSignalLevel();

			signalLevelTextView.setText(String.valueOf(signalLevel));

			final TextView distanceTextView = (TextView) rowView
					.findViewById(R.id.distanceTextView);
			distanceTextView.setText(String.valueOf(accessPoint.getDistance()));
			
			TextView timestampTextView = (TextView)rowView.findViewById(R.id.timestampTextView);
			timestampTextView.setText(accessPoint.getTimestamp().toString());
			return rowView;
		}
	}

	Button cancelButton;

	// Button startButton;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancelButton:
			finish();

			break;
		}

	}

}