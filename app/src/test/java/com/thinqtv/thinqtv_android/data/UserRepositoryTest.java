package com.thinqtv.thinqtv_android.data;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.ui.auth.LoginViewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class, Uri.class})
public class UserRepositoryTest {
    @Mock
    private LoggedInUser mockLoggedInUser;
    @Mock
    private DataSource mockDataSource;
    @Mock
    private Context mockContext;
    @Mock
    private Resources mockResources;
    @Mock
    private LoginViewModel mockLoginViewModel;

    private final String TEST_EMAIL = "test@test.com";
    private final String TEST_PASSWORD = "password";
    private final String TEST_URL = "https://test.com";

    private UserRepository userRepository;

    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence a = (CharSequence) invocation.getArguments()[0];
                return !(a != null && a.length() > 0);
            }
        });
        PowerMockito.mockStatic(Uri.class);
        Uri uri = mock(Uri.class);
        try {
            PowerMockito.when(Uri.class, "parse", anyString()).thenReturn(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getString(R.string.login_url)).thenReturn(TEST_URL);
        userRepository = UserRepository.getInstance();
        userRepository.setDataSource(mockDataSource);
    }

    @Test
    public void WHEN_getInstance_THEN_returnsConsistentInstance() {
        assertSame(userRepository, UserRepository.getInstance());
    }

    @Test
    public void WHEN_setLoggedInUser_THEN_getLoggedInUserReturnsThatUser() {
        userRepository.setLoggedInUser(mockLoggedInUser);
        assertSame(userRepository.getLoggedInUser(), mockLoggedInUser);
    }

    @Test
    public void WHEN_login_THEN_POSTRequestIsQueuedForProperURL() {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        userRepository.login(TEST_EMAIL, TEST_PASSWORD, mockContext, mockLoginViewModel);
        verify(mockDataSource).addToRequestQueue(requestCaptor.capture(), eq(mockContext));
        Request request = requestCaptor.getValue();
        assertEquals(request.getMethod(), Request.Method.POST);
        assertEquals(request.getUrl(), TEST_URL);
    }
}