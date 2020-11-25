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
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class LoggedInUserTest {
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;

    private LoggedInUser loggedInUser;

    private final String TEST_TOKEN = "TEST_TOKEN";

    @Before
    public void setup() {
        when(mockContext.getSharedPreferences(eq("ACCOUNT"), eq(0)))
                .thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit())
                .thenReturn(mockEditor);
        when(mockEditor.putString(eq("token"), anyString()))
                .thenReturn(mockEditor);
        when(mockEditor.remove(eq("token")))
                .thenReturn(mockEditor);

        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("token", TEST_TOKEN);

        loggedInUser = new LoggedInUser(mockContext, userInfo);
    }

    @Test
    public void WHEN_created_THEN_tokenPutInSharedPrefs() {
        verify(mockEditor, times(1))
                .putString(eq("token"), eq(TEST_TOKEN));
    }

    @Test
    public void WHEN_created_THEN_tokenStoredInUserInfo() {
        assertThat(loggedInUser.getUserInfo().get("token"),
                is(equalTo(TEST_TOKEN)));
    }

    @Test
    public void WHEN_userInfoUpdateAttempted_THEN_userInfoUpdates() {
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("testKey", "testValue");
        loggedInUser.updateUserInfo(updateParams);
        assertThat(loggedInUser.getUserInfo().get("testKey"),
                is(equalTo("testValue")));
    }

    @Test
    public void WHEN_loggedOut_THEN_tokenRemovedFromSharedPrefs() {
        loggedInUser.logout();
        verify(mockEditor, times(1))
                .remove(eq("token"));
    }
}