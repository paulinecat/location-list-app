package com.example.locationlab;
import android.app.ListActivity;
import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;



public class LocationManagerActivity extends ListActivity {

	private static final int ADD_LOCATION_ITEM_REQUEST = 0;
	public static final int ERROR_NOT_FOUND = 1;
	public static final int ERROR_DUPLICATE = 2;
	public static final int ERROR_INPUT = 3;
	private static final int MENU_DELETE = Menu.FIRST;

	LocationListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create new LocationListAdapter for this ListActivity's ListView
		mAdapter = new LocationListAdapter(getApplicationContext());

		getListView().setFooterDividersEnabled(true);

		// inflate footerView
		TextView footerView = (TextView)getLayoutInflater().inflate(R.layout.footer_view, null);

		// add footerView
		getListView().addFooterView(footerView);

		footerView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LocationManagerActivity.this, AddLocationActivity.class);
				startActivityForResult(intent, ADD_LOCATION_ITEM_REQUEST);
			}

		});

		getListView().setAdapter(mAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == ADD_LOCATION_ITEM_REQUEST){
			if(resultCode == RESULT_OK){
				LocationItem item = new LocationItem(data);

				if(mAdapter.checkDup(item)){
					Toast.makeText(getApplicationContext(), "PlaceBadge already exists for that location", Toast.LENGTH_SHORT).show();
				} else {
					mAdapter.add(item);
					mAdapter.notifyDataSetChanged();
				}
			} else if(resultCode == ERROR_NOT_FOUND){
				Toast.makeText(getApplicationContext(), "No country exists at that locaton", Toast.LENGTH_SHORT).show();
			} else if(resultCode == ERROR_INPUT){
				Toast.makeText(getApplicationContext(), "Invalid Lat/Long Input", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_DELETE){
			mAdapter.clear();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	// Load stored LocationItems
}
