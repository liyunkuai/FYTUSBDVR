package com.syu.dvr.widget;

import java.util.ArrayList;
import java.util.List;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.PublicClass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingLayout extends LinearLayout{
	
	private ImageView imageView ;
	public TextView mTittle;
	public TextView mSummary;
	private LinearLayout layout;
	private LinearLayout.LayoutParams params;
	private AlertDialog.Builder builder ;
	private Context context;
	public String[] mValues;
	public int mAbleValue;
	public String text;
	public SettingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initLayout(context);
	}
	private void initLayout(Context context){
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater fInflater=LayoutInflater.from(context);
		layout=(LinearLayout) fInflater.inflate(R.layout.item_setting_layout, null);
		addView(layout);
		mTittle=(TextView) layout.findViewById(R.id.item_title);
		imageView=(ImageView) layout.findViewById(R.id.item_dialog_ic);
		mSummary=(TextView) findViewById(R.id.item_summary);
		
		View diri=new View(context);
		diri.setBackgroundResource(R.drawable.divide_view);
		params=new LinearLayout.LayoutParams
				(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin=10;
		params.leftMargin=5;
		params.rightMargin=5;
		addView(diri, params);
		
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createSettingDialog();
			}
		});
	}
	private  Dialog mFormatsdDialog;
	private TextView mDialogTittle;
	private Button mSure;
	private ListView mListView;
	private SetSeleAdapter mAdapter;
	public List<RadioButton>mRadioButtons=new ArrayList<RadioButton>();
	private void createSettingDialog(){
		if (mFormatsdDialog==null) {
			int [] size=PublicClass.getInstance().getWindowManeger();
			ViewGroup.LayoutParams params= new ViewGroup.
					LayoutParams(size[0]*3/5, size[1]*3/5);
			View view =LinearLayout.inflate(TheApp.mApp, R.layout.set_selection, null);
			mFormatsdDialog=new Dialog(TheApp.mApp,R.style.add_dialog);
			mFormatsdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			mFormatsdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mFormatsdDialog.setContentView(view,params);
			mDialogTittle=(TextView) mFormatsdDialog.findViewById(R.id.dialog_title);
			mSure=(Button) mFormatsdDialog.findViewById(R.id.dialog_sure);
		}
		mFormatsdDialog.setCanceledOnTouchOutside(true);
		mFormatsdDialog.show();
		mListView=(ListView) mFormatsdDialog.findViewById(R.id.selection_listview);
		mRadioButtons.clear();
		mListView.setAdapter(new SetSeleAdapter());
		mDialogTittle.setText(text);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position==mAbleValue) {
					return;
				}
				LogCatUtils.showString("===value=="+mValues[position]);
				if (mValues.length>position) {
					updataSetting(position);
				}
			}
		});
		mSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mFormatsdDialog.dismiss();
			}
		});

	}
	public void sendBroadcast(){
		Intent intent = new Intent();
		intent.setAction("com.syu.dvr.settingclick");
		context.sendBroadcast(intent);
	}
	public void updataSummary(int value,int[] setValue){
		
	}
	public void updataSetting(int value){
		
	}
	 class SetSeleAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;
		
		public SetSeleAdapter() {
			// TODO Auto-generated constructor stub
			mInflater=LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return mValues.length;
		}

		@Override
		public Object getItem(int position) {
			return mValues[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			return handlerVideo(position, convertView, parent);
		}
		
		class ViewHolder{
			RadioButton mRadioButton;
		}
		
		public View handlerVideo(final int position, View convertView, ViewGroup parent){
			final ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.item_selection, null);
				holder = new ViewHolder();
				holder.mRadioButton=(RadioButton) convertView.findViewById(R.id.radio_button);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mRadioButton.setText(mValues[position]);
			mRadioButtons.add(holder.mRadioButton);
			if (mAbleValue==position) {
				holder.mRadioButton.setChecked(true);
			}else{
				holder.mRadioButton.setChecked(false);
			}
			return convertView;
		}
		
	}
}
	