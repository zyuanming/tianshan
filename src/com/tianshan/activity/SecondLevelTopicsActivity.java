package com.tianshan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.view.Navbar.OnNavigateListener;

public class SecondLevelTopicsActivity extends SecondLevelActivity
  implements Navbar.OnNavigateListener
{
  protected View btnComment = null;
  protected ImageView btnFavImageView = null;
  protected View btnFavourite = null;
  protected View btnShare = null;

  protected void _initNavBar(boolean paramBoolean)
  {
    super._initNavBar(paramBoolean);
    Navbar localNavbar = new Navbar(this, this.navbarbox);
    localNavbar.setOnNavigate(this);
    localNavbar.setTitle("查看专题");
    localNavbar.setCommitBtnVisibility(false);
  }

  public void onBack()
  {
    finish();
  }

  public void onCommit()
  {
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    _initNavBar(true);
  }
}

/* Location:           D:\yuanming\反编译\反编译工具\天上-论坛\tianshan-dex2jar.jar
 * Qualified Name:     com.tianshan.activity.SecondLevelTopicsActivity
 * JD-Core Version:    0.6.2
 */
//R.id.head 