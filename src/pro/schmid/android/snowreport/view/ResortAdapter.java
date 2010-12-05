package pro.schmid.android.snowreport.view;

import java.util.ArrayList;
import java.util.List;

import pro.schmid.android.snowreport.Constants;
import pro.schmid.android.snowreport.R;
import pro.schmid.android.snowreport.controller.FavoritesChangedListener;
import pro.schmid.android.snowreport.controller.FavoritesManager;
import pro.schmid.android.snowreport.model.Resort;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

public class ResortAdapter extends ArrayAdapter<Resort> implements FavoritesChangedListener {

	private List<Resort> resorts;
	private List<Resort> filtered;
	private LayoutInflater mInflater;
	private Activity activity;
	private SharedPreferences prefs;
	private ResortFilter filter;

	public ResortAdapter(Activity activity, int textViewResourceId, List<Resort> items) {
		super(activity, textViewResourceId, items);

		this.resorts = items;
		this.filtered = new ArrayList<Resort>(items);
		this.activity = activity;

		mInflater = LayoutInflater.from(activity);

		prefs = PreferenceManager.getDefaultSharedPreferences(activity);

		FavoritesManager.setFavoritesListener(this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.resort_item, null);
		}
		final Resort r = getItem(position);
		if (r != null) {
			TextView tv;

			tv = (TextView) v.findViewById(R.id.resort_name);
			if (tv != null) {
				tv.setText(r.getName());
			}

			tv = (TextView) v.findViewById(R.id.slopes);
			if(tv != null){
				tv.setText(r.getSlopesKm());
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

			ImageView i = (ImageView) v.findViewById(R.id.favoriteImage);

			if(i != null) {
				if(FavoritesManager.isFavorite(activity, r.getId())) {
					i.setImageResource(R.drawable.star_on);
				} else {
					i.setImageResource(R.drawable.star_off);
				}
			}

			TableLayout tl = (TableLayout) v.findViewById(R.id.resort_clicker);

			if(tl != null) {
				tl.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FavoritesManager.toggleFavorite(activity, r.getId());
					}
				});
			}
		}

		return v;
	}

	@Override
	public int getCount() {
		return showFavorites() ? FavoritesManager.size(activity) : filtered.size();
	}

	@Override
	public Resort getItem(int position) {

		if(showFavorites()) {
			String rId = FavoritesManager.getId(activity, position);

			for(Resort r : filtered) {
				if(rId.equals(r.getId())) {
					return r;
				}
			}

		} else {
			return filtered.get(position);
		}

		return null;
	}

	private boolean showFavorites() {
		String region = prefs.getString("region", "000");

		return Constants.favoritesKey.equals(region);
	}

	@Override
	public void favoritesChanged() {
		this.notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		Log.d(ResortAdapter.class.toString(), "Filter");
		
		if(filter == null)
			filter = new ResortFilter();
		
		return filter;
	}


	private class ResortFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			
			Log.d(ResortAdapter.class.toString(), "Filtering with <" + constraint + ">");
			
			 // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Resort> filt = new ArrayList<Resort>();
                ArrayList<Resort> lItems = new ArrayList<Resort>();
                synchronized (this)
                {
                    lItems.addAll(resorts);
                }
                for(int i = 0, l = lItems.size(); i < l; i++)
                {
                    Resort m = lItems.get(i);
                    if(m.getName().toLowerCase().contains(constraint))
                        filt.add(m);
                }
                result.count = filt.size();
                result.values = filt;
            }
            else
            {
                synchronized(this)
                {
                    result.values = resorts;
                    result.count = resorts.size();
                }
            }
            return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filtered = (List<Resort>) results.values;
			notifyDataSetInvalidated();
		}

	}

}
