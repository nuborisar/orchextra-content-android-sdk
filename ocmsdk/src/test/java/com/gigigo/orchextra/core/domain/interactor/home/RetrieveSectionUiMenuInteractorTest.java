package com.gigigo.orchextra.core.domain.interactor.home;

import com.gigigo.interactorexecutor.interactors.InteractorResponse;
import com.gigigo.interactorexecutor.responses.BusinessError;
import com.gigigo.interactorexecutor.responses.BusinessObject;
import com.gigigo.orchextra.core.domain.data.MenuNetworkDataSource;
import com.gigigo.orchextra.core.domain.entities.menus.MenuContentData;
import com.gigigo.orchextra.core.domain.interactor.errors.GenericResponseDataError;
import com.gigigo.orchextra.core.domain.interactor.errors.NoNetworkConnectionError;
import com.gigigo.orchextra.core.domain.utils.ConnectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveSectionUiMenuInteractorTest {

  @Mock ConnectionUtils connectionUtils;

  @Mock MenuNetworkDataSource menuNetworkDataSource;

  private GetMenuDataInteractor interactor;

  @Before public void setUp() throws Exception {
    interactor = new GetMenuDataInteractor(connectionUtils, menuNetworkDataSource);
  }

  @Test public void shouldReturnNoNetworkConnectionErrorWhenDeviceNoHasInternetConnection()
      throws Exception {

    when(connectionUtils.hasConnection()).thenReturn(false);

    InteractorResponse<MenuContentData> response = interactor.call();

    assertThat(response.hasError()).isTrue();
    assertThat(response.getError()).isExactlyInstanceOf(NoNetworkConnectionError.class);
  }

  @Test public void shouldReturnValueListWhenHasConnectionAndRequestWasSuccessful() throws Exception {
    BusinessObject<MenuContentData> fakeSuccessfulBoMenuContentData = getFakeSuccessfulMenuContentData();

    when(connectionUtils.hasConnection()).thenReturn(true);
    when(menuNetworkDataSource.getMenuContentData()).thenReturn(fakeSuccessfulBoMenuContentData);

    InteractorResponse<MenuContentData> response = interactor.call();

    assertThat(response.hasError()).isFalse();
    assertThat(response.getResult()).isEqualTo(fakeSuccessfulBoMenuContentData.getData());
  }

  @Test public void shouldReturnGenericInteractorErrorWhenRequestFailed() throws Exception {
    BusinessObject<MenuContentData> fakeFailedBoMenuContentData = getFakeFailedMenuContentData();

    when(connectionUtils.hasConnection()).thenReturn(true);
    when(menuNetworkDataSource.getMenuContentData()).thenReturn(fakeFailedBoMenuContentData);

    InteractorResponse<MenuContentData> response = interactor.call();

    assertThat(response.hasError()).isTrue();
    assertThat(response.getError()).isExactlyInstanceOf(GenericResponseDataError.class);
  }

  private BusinessObject<MenuContentData> getFakeSuccessfulMenuContentData() {
    MenuContentData menuContentData = new MenuContentData();

    return new BusinessObject<>(menuContentData, BusinessError.createOKInstance());
  }

  private BusinessObject<MenuContentData> getFakeFailedMenuContentData() {
    return new BusinessObject<>(null, BusinessError.createKOInstance(""));
  }
}