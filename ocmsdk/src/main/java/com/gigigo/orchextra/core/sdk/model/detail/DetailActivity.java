package com.gigigo.orchextra.core.sdk.model.detail;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.gigigo.ggglib.device.AndroidSdkVersion;
import com.gigigo.orchextra.core.controller.model.detail.DetailPresenter;
import com.gigigo.orchextra.core.controller.model.detail.DetailView;
import com.gigigo.orchextra.core.sdk.di.base.BaseInjectionActivity;
import com.gigigo.orchextra.core.sdk.di.injector.Injector;
import com.gigigo.orchextra.core.sdk.model.detail.viewtypes.youtube.YoutubeWebviewActivity;
import com.gigigo.orchextra.core.sdk.utils.ImageGenerator;
import com.gigigo.orchextra.ocm.OCManager;
import com.gigigo.orchextra.ocm.views.UiDetailBaseContentData;
import com.gigigo.orchextra.ocmsdk.R;
import com.gigigo.ui.imageloader.ImageLoader;
import orchextra.javax.inject.Inject;

public class DetailActivity extends BaseInjectionActivity<DetailActivityComponent>
    implements DetailView {

  private static final String EXTRA_ELEMENT_URL = "EXTRA_ELEMENT_URL";
  private static final String EXTRA_IMAGE_TO_EXPAND_URL = "EXTRA_IMAGE_TO_EXPAND_URL";
  private static final String EXTRA_WIDTH_IMAGE_TO_EXPAND_URL = "EXTRA_WIDTH_IMAGE_TO_EXPAND_URL";
  private static final String EXTRA_HEIGHT_IMAGE_TO_EXPAND_URL = "EXTRA_HEIGHT_IMAGE_TO_EXPAND_URL";

  @Inject DetailPresenter presenter;
  @Inject ImageLoader imageLoader;

  private ImageView animationImageView;
  private UiDetailBaseContentData uiContentView;

  public static void open(Activity activity, String elementUrl, String urlImageToExpand, int width,
      int height, final View view) {
    final Intent intent = new Intent(activity, DetailActivity.class);
    intent.putExtra(DetailActivity.EXTRA_ELEMENT_URL, elementUrl);
    intent.putExtra(DetailActivity.EXTRA_IMAGE_TO_EXPAND_URL, urlImageToExpand);
    intent.putExtra(DetailActivity.EXTRA_WIDTH_IMAGE_TO_EXPAND_URL, width);
    intent.putExtra(DetailActivity.EXTRA_HEIGHT_IMAGE_TO_EXPAND_URL, height);

    if (activity != null) {
      if (view != null) {
        ActivityOptionsCompat optionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "thumbnail");
        activity.startActivity(intent, optionsCompat.toBundle());
      } else {
        activity.startActivity(intent);
      }
    }
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_layout);

    animationImageView = (ImageView) findViewById(R.id.animationImageView);

    //CoordinatorDetail with click event recreate the view and preview is showed when return in video activity, so dont move
    presenter.attachView(this);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT) @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_FULLSCREEN;

      if (AndroidSdkVersion.hasKitKat19()) {
        flags = flags | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
      }

      getWindow().getDecorView().setSystemUiVisibility(flags);
    }
  }

  @Override protected void initDI() {
    Injector injector = OCManager.getInjector();
    if (injector != null) {
      injector.injectDetailActivity(this);
    }
  }

  @Override public void initUi() {
    setAnimationImageView();

    String elementUrl = getIntent().getStringExtra(EXTRA_ELEMENT_URL);
    presenter.loadSection(elementUrl);
  }

  @Override public void setView(UiDetailBaseContentData uiContentView) {
    this.uiContentView = uiContentView;
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.content_detail_view, uiContentView)
        .commit();
  }

  @Override public void showError() {
    finish();
  }

  @Override public void finishView() {
    onBackPressed();
  }

  @Override public void setAnimationImageView() {
    String url = getIntent().getStringExtra(EXTRA_IMAGE_TO_EXPAND_URL);
    int width = getIntent().getIntExtra(EXTRA_WIDTH_IMAGE_TO_EXPAND_URL, 0);
    int height = getIntent().getIntExtra(EXTRA_HEIGHT_IMAGE_TO_EXPAND_URL, 0);

    if (!TextUtils.isEmpty(url)) {
      String generateImageUrl = ImageGenerator.generateImageUrl(url, width, height);
      imageLoader.load(generateImageUrl).into(animationImageView).override(width, height).build();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == YoutubeWebviewActivity.RESULT_CODE_YOUTUBE_PLAYER && uiContentView != null) {
    //  uiContentView.setTopScroll();
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }
}
