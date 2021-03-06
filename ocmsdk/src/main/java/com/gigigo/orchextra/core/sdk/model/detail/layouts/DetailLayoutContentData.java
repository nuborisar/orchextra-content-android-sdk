package com.gigigo.orchextra.core.sdk.model.detail.layouts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gigigo.orchextra.core.controller.model.detail.DetailElementsView;
import com.gigigo.orchextra.ocm.OCManager;
import com.gigigo.orchextra.ocm.OcmEvent;
import com.gigigo.orchextra.ocmsdk.R;
import com.gigigo.orchextra.core.controller.model.detail.DetailElementsViewPresenter;
import com.gigigo.orchextra.core.controller.views.UiBaseContentData;
import com.gigigo.orchextra.ocm.views.UiDetailBaseContentData;

public class DetailLayoutContentData extends UiDetailBaseContentData
    implements DetailElementsView {

  private String elementUrl;
  private DetailElementsViewPresenter presenter;
  private OnFinishViewListener onFinishListener;
  private Context context;

  public static DetailLayoutContentData newInstance() {
    return new DetailLayoutContentData();
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    this.context = context;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    return inflater.inflate(R.layout.view_detail_elements_layout, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (presenter != null) {
      presenter.attachView(this);
    }
  }

  @Override public void setOnFinishListener(OnFinishViewListener onFinishListener) {
    this.onFinishListener = onFinishListener;
  }

  //@Override public void setTopScroll() {
  //  if (detailCoordinatorLayoutContentData != null) {
  //    detailCoordinatorLayoutContentData.scrollToTop();
  //  }
  //}

  public void setElementUrl(String elementUrl) {
    this.elementUrl = elementUrl;
  }

  public void setPresenter(DetailElementsViewPresenter presenter) {
    this.presenter = presenter;
  }

  @Override public void initUi() {
    presenter.loadSection(elementUrl);

    int contentIdIndex = elementUrl.lastIndexOf("/");
    String idIndex = elementUrl.substring(contentIdIndex+1);

    OCManager.notifyEvent(OcmEvent.CONTENT_START, idIndex);
  }

  @Override public void renderDetailViewWithPreview(UiBaseContentData previewContentData,
      UiBaseContentData detailContentData, boolean canShare) {

    addLayoutToCoordinatorLayoutView(previewContentData, detailContentData, canShare);
  }

  private void addLayoutToCoordinatorLayoutView(UiBaseContentData previewContentData,
      UiBaseContentData detailContentData, boolean canShare) {

    DetailCoordinatorLayoutContentData detailCoordinatorLayoutContentData =
        DetailCoordinatorLayoutContentData.newInstance();

    detailCoordinatorLayoutContentData.setViews(previewContentData, detailContentData);
    detailCoordinatorLayoutContentData.setOnFinishListener(onFinishListener);
    if (canShare) {
      detailCoordinatorLayoutContentData.setOnShareListener(onShareListener);
    }

    ((AppCompatActivity) context).getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.detail_container_layout, detailCoordinatorLayoutContentData)
        .commit();
  }

  @Override public void renderDetailView(UiBaseContentData detailContentData, boolean canShare) {
    addLayoutToView(detailContentData, canShare);
  }

  @Override public void renderPreview(UiBaseContentData previewContentData, boolean canShare) {
    addLayoutToView(previewContentData, canShare);
  }

  @Override public void showProgressView(boolean visible) {

  }

  @Override public void showEmptyView() {
    Activity activity = (Activity) context;
    if (activity != null) {
      activity.finish();
    }
  }

  @Override public void shareElement(String shareText) {
    OCManager.notifyEvent(OcmEvent.SHARE, null);
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, shareText);
    intent.setType("text/plain");
    startActivity(intent);
  }

  private void addLayoutToView(UiBaseContentData uiBaseContentData, boolean canShare) {

    DetailSimpleLayoutContentData detailSimpleLayoutContentData =
        DetailSimpleLayoutContentData.newInstance();

    detailSimpleLayoutContentData.setViews(uiBaseContentData);
    detailSimpleLayoutContentData.setOnFinishListener(onFinishListener);
    if (canShare) {
      detailSimpleLayoutContentData.setOnShareListener(onShareListener);
    }

    ((AppCompatActivity) context).getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.detail_container_layout, detailSimpleLayoutContentData)
        .commit();
  }

  private DetailParentContentData.OnShareListener onShareListener =
      new DetailParentContentData.OnShareListener() {
        @Override public void onShare() {
          presenter.shareElement();
        }
      };

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
