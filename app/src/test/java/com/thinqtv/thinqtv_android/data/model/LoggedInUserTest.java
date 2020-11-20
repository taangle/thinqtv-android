package com.thinqtv.thinqtv_android.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class LoggedInUserTest {
    @Mock
    private Context mMockContext;
    @Mock
    private SharedPreferences mMockSharedPreferences;
    @Mock
    private SharedPreferences.Editor mMockEditor;

    private LoggedInUser mLoggedInUser;

    private String TEST_TOKEN = "TEST_TOKEN";

    @Before
    public void setup() {
        when(mMockContext.getSharedPreferences(eq("ACCOUNT"), eq(0)))
                .thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.edit())
                .thenReturn(mMockEditor);
        when(mMockEditor.putString(eq("token"), anyString()))
                .thenReturn(mMockEditor);
        when(mMockEditor.remove(eq("token")))
                .thenReturn(mMockEditor);

        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("token", TEST_TOKEN);

        mLoggedInUser = new LoggedInUser(mMockContext, userInfo);
    }

    @Test
    public void tokenSetAtConstruction() {
        verify(mMockEditor, times(1))
                .putString(eq("token"), eq(TEST_TOKEN));
        HashMap<String, String> userInfo = mLoggedInUser.getUserInfo();
        assertThat(userInfo.get("token"), is(equalTo(TEST_TOKEN)));
    }
}