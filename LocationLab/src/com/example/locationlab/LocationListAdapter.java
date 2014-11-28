package com.example.locationlab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationListAdapter extends BaseAdapter {

	private final List<LocationItem> mItems = new ArrayList<LocationItem>();
	private final Context mContext;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;
	
	public LocationListAdapter(Context context){
		mContext = context;
	}

	public void add(LocationItem item){
		mItems.add(item);
		notifyDataSetChanged();
	}
	
	public void clear(){
		mItems.clear();
		notifyDataSetChanged();
	}
	
	public boolean checkDup(LocationItem item){
		for(LocationItem lItems : mItems){
			if(lItems.isSame(item))
				return true;
		}
		
		return false;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		
		// inflate the layout
		if(itemView == null){
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = mInflater.inflate(R.layout.loc_item, parent, false);
			holder = new ViewHolder();
			holder.flag = (ImageView) itemView.findViewById(R.id.flag);
			holder.name = (TextView) itemView.findViewById(R.id.place_name);
			holder.country = (TextView) itemView.findViewById(R.id.country_name);
			itemView.setTag(holder);
		} else {
			holder = (ViewHolder) itemView.getTag();
		}

		// item to be rendered
		final LocationItem locationItem = mItems.get(position);
		
		// update the ViewHolder
		holder.name.setText(locationItem.getName());
		holder.country.setText(locationItem.getCountry());
		new DownloadImageTask(holder, itemView).execute(locationItem.getFlag());
		
		return itemView;
	}
	
	public static class ViewHolder {
		TextView name, country;
		ImageView flag;
		int position;
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		private ViewHolder holder;
		
		public DownloadImageTask(ViewHolder holder, View itemView){
			this.holder = holder;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			String URL = params[0];
			Bitmap icon = null;
			HttpURLConnection httpURLConnection = null;
			
			try{
				httpURLConnection = (HttpURLConnection) new URL(URL).openConnection();
				InputStream in = httpURLConnection.getInputStream();
				icon = BitmapFactory.decodeStream(in);
			} catch (IOException e) {
				e.printStackTrace();
		    } finally {
		        if (null != httpURLConnection)
		        	httpURLConnection.disconnect();
		    }
			
			return icon;
		}
		
		protected void onPostExecute(Bitmap result){
			holder.flag.setImageBitmap(result);
		}
		
	}
}
