package com.thinqtv.thinqtv_android.data;

import com.thinqtv.thinqtv_android.data.model.LoggedInUser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

public class UserRepositoryTest {
    @Mock
    private LoggedInUser mockLoggedInUser;
    @Mock
    private DataSource mockDataSource;

    private UserRepository userRepository;

    @Before
    public void setup() {
        userRepository = UserRepository.getInstance();
        userRepository.setDataSource(mockDataSource);
    }

    @Test
    public void WHEN_getInstance_THEN_returnsConsistentInstance() {
        assertThat(userRepository, is(sameInstance(UserRepository.getInstance())));
    }

    @Test
    public void WHEN_setLoggedInUser_THEN_getLoggedInUserReturnsThatUser() {
        userRepository.setLoggedInUser(mockLoggedInUser);
        assertThat(userRepository.getLoggedInUser(), is(sameInstance(mockLoggedInUser)));
    }
}