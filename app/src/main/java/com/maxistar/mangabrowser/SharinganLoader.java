package com.maxistar.mangabrowser;

import android.content.Context;
import android.view.View;

public class SharinganLoader extends View implements Animator.Animable {
	private Animator animator;

	public SharinganLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onAttachedToWindow() {
		animator = new Animator(this, MStrings.IMAGE_PAGER_ANIMATOR);
		animator.start();
		super.onAttachedToWindow();
	}

	
	class Fraction {
		
	}
	
	class CircrleFraction extends Fraction {
		boolean red = true;
	}
	
	class BagelFraction extends Fraction {
		int width; //count of percents
		int min_widht = 2;
		int max_width = 15;
	}
	
	class LineFraction extends Fraction {
		
	}
	
	class DropFraction extends Fraction {
		
	}
	
	class ArcFraction  extends Fraction {
		
	}

	@Override
	public boolean waitForDraw(long timeout) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void redraw() {
		// TODO Auto-generated method stub
		
	}
}
