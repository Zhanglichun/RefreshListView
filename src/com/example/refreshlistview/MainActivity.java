package com.example.refreshlistview;

import java.util.ArrayList;

import com.example.refreshlistview.RefreshListView.IRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements IRefreshListener{

	private RefreshListView lv_main;
	private ArrayList<Entity> list;
	private int i = 0;
	private MyAdapter myAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lv_main = (RefreshListView) findViewById(R.id.lv_main);
		
		list = new ArrayList<Entity>();
		fillData();
		
		myAdapter = new MyAdapter();
		lv_main.setAdapter(myAdapter);
		lv_main.setiRefreshListener(this);
	}
	
	private void fillData(){
		Entity entity = new Entity();
		entity.setFirst("默认数据");
		entity.setUser("50W用户");
		entity.setApp("这是一个神奇的应用");
		for (int i = 0; i < 10; i++) {
			list.add(entity);
		}
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			
			Entity entity = list.get(arg0);
			View view = View.inflate(MainActivity.this, R.layout.item, null);
			
			TextView first = (TextView) view.findViewById(R.id.first);
			TextView user = (TextView) view.findViewById(R.id.user);
			TextView app = (TextView) view.findViewById(R.id.app);
			
			first.setText(entity.getFirst());
			user.setText(entity.getUser());
			app.setText(entity.getApp());
			
			return view;
		}
		
	}

	private void fillMoreData(){
		Entity entity = new Entity();
		entity.setFirst("刷新数据");
		entity.setUser("10W用户");
		entity.setApp("这是一个神奇的应用");
		for (int i = 0; i < 2; i++) {
			list.add(0, entity);
		}
	}
	
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				fillMoreData();
				if (myAdapter != null) {
					myAdapter.notifyDataSetChanged();
				}

				lv_main.refreshComplete();
			}
		}, 3000);
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				loadMoreData();
				if (myAdapter != null) {
					myAdapter.notifyDataSetChanged();
				}

				lv_main.loadComplete();
			}
		}, 3000);
	}
	
	private void loadMoreData() {
		// TODO Auto-generated method stub
		Entity entity = new Entity();
		entity.setFirst("加载数据");
		entity.setUser("100W用户");
		entity.setApp("这是一个神奇的应用");
		for (int i = 0; i < 2; i++) {
			list.add(entity);
		}
	}
}
