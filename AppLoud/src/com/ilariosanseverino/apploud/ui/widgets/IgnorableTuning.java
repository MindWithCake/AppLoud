package com.ilariosanseverino.apploud.ui.widgets;

import com.ilariosanseverino.apploud.R;
import com.ilariosanseverino.apploud.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class IgnorableTuning extends RelativeLayout implements OnLongClickListener, OnClickListener {
	private int popupIcon = 0;
	private String popupText;
	private boolean enabled = false;
	private boolean hasChild = false;
	
	private ImageView checkView;
	private ImageView popView;
	private OnActivationChangedListener activationListener;
	
	//--------------- Constructors -----------------------

	public IgnorableTuning(Context context, AttributeSet attrs){
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, styleable.IgnorableTuning, 0, 0);
		try{
			final int N = a.getIndexCount();

			for(int i = 0; i < N; ++i){
				int attr = a.getIndex(i);
				switch(attr){
				case styleable.IgnorableTuning_popup_text:
					popupText = a.getString(attr);
					break;
				case styleable.IgnorableTuning_popup_icon:
					popupIcon = a.getResourceId(attr, 0);
					break;
				case styleable.IgnorableTuning_enabled:
					enabled = a.getBoolean(attr, false);
					break;
				}
			}
		}
		finally {
			a.recycle();
		}
		
		setGravity(Gravity.CENTER_VERTICAL);

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.ignorable_tuning, this, true);
	}
	
	public IgnorableTuning(Context context, Drawable popDrawable, String popText, View customControl) {
		super(context);
		popView = new ImageView(context);
		popView.setId(R.id.pop_icon);
		
		popupText = popText;
		
		setGravity(Gravity.CENTER_VERTICAL);
		
		LayoutParams lp = new RelativeLayout.LayoutParams(generateDefaultLayoutParams());
		lp.addRule(ALIGN_PARENT_LEFT);
		addView(popView, lp);
		
		checkView = new ImageView(context);
		checkView.setId(R.id.check);
		checkView.setImageResource(R.drawable.btn_check_buttonless_off);
		checkView.setOnClickListener(this);
		lp = new RelativeLayout.LayoutParams(generateDefaultLayoutParams());
		lp.addRule(ALIGN_PARENT_LEFT);
		addView(checkView, lp);
		
		lp = settableViewParams();
		addView(customControl, lp);
	}
	
	public IgnorableTuning(Context context, int popDrawId, String popText, View customControl){
		this(context, context.getResources().getDrawable(popDrawId), popText, customControl);
	}
	
	//------------------- Overrides -------------------------
	
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();

		popView = (ImageView)findViewById(R.id.pop_icon);
		if(popupIcon != 0)
			popView.setImageResource(popupIcon);
		popView.setOnLongClickListener(this);
		
		checkView = (ImageView)findViewById(R.id.check);
		checkView.setImageResource(R.drawable.btn_check_buttonless_off);
		checkView.setOnClickListener(this);
		
		changeBackground(enabled);
	}
	
	@Override
	public void addView (View child, ViewGroup.LayoutParams params){
		int id = child.getId();
		if(id != R.id.pop_icon && id != R.id.check){
			if(hasChild)
				throw new IllegalArgumentException("IgnorableTuning vuole un solo figlio!");
			hasChild = true;
			params = settableViewParams();
		}
		super.addView(child, params);
	}
	
	@Override
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		if(enabled != this.enabled){
			this.enabled = enabled;
			changeBackground(enabled);
		}
		if(activationListener != null)
			activationListener.onActivationChanged(this, enabled);
	}
	
	//------------------ Public methods ----------------------
	
	public void setOnActivationChangedListener(OnActivationChangedListener listener){
		activationListener = listener;
	}
	
	//------------------ Private methods ---------------------
	
	private void changeBackground(boolean active){
		int checkRes, colorRes;
		if(active){
			checkRes = R.drawable.btn_check_buttonless_on;
			colorRes = R.color.sanse_enabled_background;
		}
		else{
			checkRes = R.drawable.btn_check_buttonless_off;
			colorRes = R.color.sanse_disabled_background;
		}
		checkView.setImageResource(checkRes);
		setBackgroundResource(colorRes);
	}
	
	private LayoutParams settableViewParams(){
		LayoutParams lp = new LayoutParams(generateDefaultLayoutParams());
		lp.addRule(LEFT_OF, R.id.check);
		lp.addRule(RIGHT_OF, R.id.pop_icon);
		lp.addRule(CENTER_VERTICAL);
		return lp;
	}
	
	//------------- implemented interfaces ---------------

	@Override
	public boolean onLongClick(View v){
		Toast t = Toast.makeText(getContext(), popupText, Toast.LENGTH_SHORT);
		int[] coords = new int[2];
		v.getLocationOnScreen(coords);
		t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, coords[1]-v.getHeight());
		t.show();
		return true;
	}

	@Override
	public void onClick(View v){
		setEnabled(!enabled);
	}
}
