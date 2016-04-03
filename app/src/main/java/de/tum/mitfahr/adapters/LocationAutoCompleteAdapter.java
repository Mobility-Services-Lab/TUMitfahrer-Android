package de.tum.mitfahr.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import de.tum.mitfahr.R;
import de.tum.mitfahr.util.GooglePlacesAPIHelper;

/**
 * Authored by abhijith on 22/06/14.
 */
public class LocationAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;
    private final LayoutInflater mInflater;

    public LocationAutoCompleteAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(resultList!=null) return resultList.size();
        else return 0;
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup view = null;

        if (convertView == null) {
            view = (ViewGroup) mInflater.inflate(R.layout.list_item_autocomplete, parent, false);
        } else {
            view = (ViewGroup) convertView;
        }
        String text = getItem(position);
        ((TextView) view.findViewById(R.id.autocomplete_list_text)).setText(text);
        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
//                    resultList = GooglePlacesAPIHelper.autocomplete(constraint.toString());
                    new LocationDownloadTask().execute(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    Log.e("ArrayList", "" + resultList);
                    if(resultList != null)
                    { filterResults.count = resultList.size();}
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private class LocationDownloadTask extends AsyncTask<String, Void, ArrayList<String>> {
        protected ArrayList<String> doInBackground(String... strings) {
            return GooglePlacesAPIHelper.autocomplete(strings[0]);
        }

        protected void onPostExecute(ArrayList<String> result) {
            resultList = result;
            notifyDataSetChanged();
        }
    }

}
