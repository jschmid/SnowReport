package pro.schmid.android.snowreport.view;

import java.util.List;

import pro.schmid.android.snowreport.R;
import pro.schmid.android.snowreport.model.Resort;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResortAdapter extends ArrayAdapter<Resort> {

	private List<Resort> resorts;
	private LayoutInflater mInflater;

	public ResortAdapter(Context context, int textViewResourceId, List<Resort> items) {
		super(context, textViewResourceId, items);
		this.resorts = items;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.resort_item, null);
		}
		Resort r = resorts.get(position);
		if (r != null) {
			TextView tv;
			
			tv = (TextView) v.findViewById(R.id.resort_name);
			if (tv != null) {
				tv.setText(r.getName());
			}
			
			tv = (TextView) v.findViewById(R.id.slopes);
			if(tv != null){
				tv.setText(r.getSlopes());
			}
			
			tv = (TextView) v.findViewById(R.id.artificialSnow);
			if(tv != null){
				tv.setText(r.getArtificialSnow());
			}
			
			tv = (TextView) v.findViewById(R.id.snowState);
			if(tv != null){
				tv.setText(r.getSnowState());
			}
			
			tv = (TextView) v.findViewById(R.id.slopesState);
			if(tv != null){
				tv.setText(r.getSlopesState());
			}
			
			tv = (TextView) v.findViewById(R.id.slopesToResort);
			if(tv != null){
				tv.setText(r.getSlopesToResort());
			}
			
			tv = (TextView) v.findViewById(R.id.lastUpdate);
			if(tv != null){
				tv.setText(r.getLastUpdate());
			}
		}
		return v;
	}

}
