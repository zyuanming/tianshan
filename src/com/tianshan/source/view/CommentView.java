package com.tianshan.source.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.tianshan.R;

public class CommentView implements View.OnClickListener
{
	private EditText commentEdit;
	private ImageView commentTag;
	private Context context;

	public CommentView(Context paramContext, ViewGroup paramViewGroup,
			String paramString)
	{
		_init(paramContext, paramViewGroup, paramString);
	}

	public void _init(Context paramContext, ViewGroup paramViewGroup,
			String paramString)
	{
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.entry_toolbar, null);
		this.context = paramContext;
		this.commentTag = ((ImageView) localView.findViewById(R.id.commenttag));
		if (paramString.equals("comment"))
			this.commentTag.setImageResource(R.drawable.comment_view);
		else
		{
			if (paramString.equals("message"))
				this.commentTag.setImageResource(R.drawable.message_view);
		}
		this.commentEdit = ((EditText) localView
				.findViewById(R.id.comment_entry));
		this.commentTag.setOnClickListener(this);
		paramViewGroup.addView(localView, new ViewGroup.LayoutParams(-1, -2));
	}

	public void finishComment()
	{
		this.commentTag.setVisibility(0);
		this.commentEdit.setVisibility(8);
		((InputMethodManager) this.context.getSystemService("input_method"))
				.hideSoftInputFromWindow(this.commentEdit.getWindowToken(), 0);
		this.commentEdit.setText("");
		this.commentEdit.clearFocus();
	}

	public String getCommentString()
	{
		return this.commentEdit.getText().toString();
	}

	public void onClick(View paramView)
	{
		if (paramView.getId() == R.id.commenttag)
		{
			this.commentTag.setVisibility(8);
			this.commentEdit.setVisibility(0);
			this.commentEdit.requestFocus();
			InputMethodManager localInputMethodManager = (InputMethodManager) this.context
					.getSystemService("input_method");
			localInputMethodManager.toggleSoftInput(0, 2);
			localInputMethodManager.showSoftInput(this.commentEdit, 0);
		}
	}
}