# ThinQ.tv Manual Testing Procedures

## Profile Testing
**Test #1 - Sign in Functionality**
- If already logged in, log out and verify that you are navigated to the Welcome page
- On the profile fragment, press Log In. 
- Verify that you are navigated to the log in page.
- Log in with the following credentials:
  - trucker@1.com
  - Password1
- Verify you are navigated to the Profile fragment and the UI loads as expected with no issues. 

**Test #2 - Edit Profile Functionality**
- Following the previous instruction, press “Edit Profile” and verify you are navigated to the Edit Profile fragment.
- Tap on the “About You” text entry, and enter “Capstone”
- Add a profile image.
- Add a banner image.
- Click “Save Changes”
- Navigate back to the Profile fragment.
- Click sign out. 
- Sign back in using the same credentials as before. 
- Verify you are navigated to the Profile page and the UI loads as expected with no issues.
- Press “Edit Profile” and verify you are navigated to the Edit Profile page, and all the information and images you is populated.

**Test #3 - Edit Account functionality**
- Navigate back to the Profile fragment.
- Press “Edit Account Settings.”
- Change your password to Password
- Click “Save Changes.”
- Verify that you’re navigated out the Edit Account Settings page.
- Navigate to the Profile page if not already there.
- Press Sign out.
- Verify that you are navigated to the welcome page and are logged out
- Press “Log In”
- Try to log in with the credentials from Test #1
- Verify that you are not logged in and receive an error message.
- Try logging in with new credentials and verify that you are logged in.

**Test #4 - Schedule a Conversation**
- Navigate to the Profile fragment if not already there.
- Press “Schedule a Conversation”
- Verify that you are navigated to the “Schedule a Conversation” page.
- Enter a conversation title, and description. 
- Press the Date selector.
- Verify that a calendar shows up.
- Select a date a week from todays date on the calendar.
- Verify that the calendar closes with no issues.
- Press the Time selector.
- Verify that a clock shows up.
- Select any time on the clock, but make a note of it.
- Verify that the clock closes with no issues.
- Press Save Changes.
- Navigate to the Conversations page.
- Click the Conversations drop down menu and select “Next Week”
- Verify that the conversation you scheduled is listed.

**Test #5 - Sign Out**
- Navigate to the Profile fragment if not already there.
- Press the sign out button. 
- Verify that the Profile fragment switches to the Welcome page fragment. 
- Navigate to any of the other 3 fragments.
- Navigate back to the Profile fragment and verify that the Welcome page is displayed.

## Conversations Fragment
**Test #1**
- Press the “Conversations” button.
  - The Calendar icon.
- Verify that a list of Events appears with no issues.
- Select a listing
- Verify that you are navigated to 

**Test #2 - Conversation sorting**
- Navigate to the Conversations fragment if not already.
- Verify the Conversations drop down menu value is set to “All Events”
- Click the Conversations drop down menu. 
- Verify that a drop down menu appears with the expected options. 
- Select “This Week”.
- Verify that the conversations list updates and the dates of the conversations are within this week.
- Click the Conversations drop down menu. 
- Select “Next Week”.
- Verify that the conversations list updates and the dates of the conversations belong in the next week.
- Click the Conversations drop down menu. 
- Select “Future”.
- Verify that the conversations list updates and the dates of the conversations are more than a week away.
- *RSVP Conversation sorting is tested in Test #3*

**Test #3 - Conversation Details**
- Navigate to the Conversations fragment if not already there.
- Select any conversation, and note the details of it.
- Verify that the details of the conversation are displayed correctly and neatly. 
- RSVP for that conversation. 
- Press the phones back button. 
- Verify that you are navigated back to the Conversations fragment. 
- Click the Conversations drop down menu. 
- Select “RSVP’s”.
- Verify that the conversations list updates and the conversation you RSPV’d for appears. 

## Invite Us to Speak Fragment
**Test #1**
- Press the “Invite Us to Speak” button
- The conversation bubble icon.
- Verify that the UI looks/responds as expected.
- Scroll down to the bottom.
- Fill out the entries as you please.
- Press “Send Message”
- Verify that a BottomSheetDialog appears with options on how to send the message.
- Select Gmail.
- Verify that you are navigated to a pre-written email composed with the information you filled out.

## About Us Fragment
**Test #1**
- Press the “About Us” button. 
- The question mark icon.
- Verify that the UI loads/responds as expected.

## Miscellaneous
**Hardware back button test**
- Start the app.
- Verify that the app loads as expected. 
- Press the phone's back button. 
- Verify that neither the activity nor the fragment change.
- Navigate to the Conversation’s fragment, and select a conversation.
- Verify that you have been taken the Conversation’s detail page. 
- Press the phone’s back button. 
- Verify that you are navigated to the Conversation’s fragment.
- Press the phone’s back button at least twice.
- Verify that the app does not crash, and that neither the activity nor the fragment change.
- Navigate to the Profile Fragment and log in if not already. 
- Press the phone’s back button.
- Verify that the app does not crash, and that neither the activity nor the fragment change.
- Press “Edit Profile” and verify that you are navigated to the Edit Profile page.
- Press the phone’s back button and verify that you are taken to back to the Profile fragment.
