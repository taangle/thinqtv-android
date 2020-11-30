package com.thinqtv.thinqtv_android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;

import com.thinqtv.thinqtv_android.data.model.InviteUsModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.ArrayList;

public class inviteus_fragment extends Fragment {
    private View view;

    private InviteUsModel inviteUsModel;
    private String emailBody = "";

    private ActionBarDrawerToggle mDrawerToggle; //toggle for sidebar button shown in action bar

    public inviteus_fragment() {
        // Required empty public constructor
    }

    public static inviteus_fragment newInstance() {
        inviteus_fragment fragment = new inviteus_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.inviteus_fragment, container, false);
        ((LinearLayout) view.findViewById(R.id.pageWrapper)).setVisibility(View.GONE);

        new WebElementsTask().execute();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    private void setInviteUsModel(String title, String sMessageTitle1, String sMessage1){
        inviteUsModel = new InviteUsModel();

        inviteUsModel.Title = title;
        inviteUsModel.MessageTitle1 = sMessageTitle1;
        inviteUsModel.Message1 = sMessage1;

        TextView tvTitle = view.findViewById(R.id.tvPageTitle);
        TextView tvMessageTitle1 = view.findViewById(R.id.tvMessageTitle1);
        TextView tvMessage1 = view.findViewById(R.id.tvMessage1);
        TextView buttonSendMessage = view.findViewById(R.id.sendMessage);

        if (title.length() == 0 || sMessageTitle1.length() == 0 || sMessage1.length() == 0){
            //((RelativeLayout)view.findViewById(R.id.rl_Error)).setVisibility(View.VISIBLE);
            ((LinearLayout) view.findViewById(R.id.pageWrapper)).setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText(inviteUsModel.Title);
            tvMessageTitle1.setText(inviteUsModel.MessageTitle1);
            tvMessage1.setText("\t" + inviteUsModel.Message1.replace("amp;", ""));
            ((LinearLayout) view.findViewById(R.id.pageWrapper)).setVisibility(View.VISIBLE);
            ((LinearLayout) view.findViewById(R.id.pageWrapper)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (view.getId() != (view.findViewById(R.id.pageWrapper)).getId()) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    }
                }
            });
            buttonSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMessage();
                    makeEmail();
                }
            });
        }
    }

    private void setMessage() {
        String fullName = ((EditText) view.findViewById(R.id.editTextFullName)).getText().toString();
        String phoneNumber = ((EditText) view.findViewById(R.id.editTextPhone)).getText().toString();
        String message = ((EditText) view.findViewById(R.id.editTextMessage)).getText().toString();

        if (!fullName.isEmpty()) {
            emailBody += getContext().getString(R.string.email_name) + fullName + "\n\n";
        }
        if (!phoneNumber.isEmpty()) {
            emailBody += getContext().getString(R.string.email_phone) + phoneNumber + "\n\n";
        }
        if (!message.isEmpty()) {
            emailBody += getContext().getString(R.string.email_message) + message ;
        }
    }

    protected void makeEmail() {
        String[] TO = {"info@ThinQ.tv"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.invite_us_to_speak));
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), getContext().getString(R.string.no_email_client), Toast.LENGTH_SHORT).show();
        }
    }

    protected void maintenanceMessage() {
        view.findViewById(R.id.rl_Error).setVisibility(View.VISIBLE);
    }

    private class WebElementsTask extends AsyncTask<Void, Void, Void> {
        String title = "";

        String sectionTitle1 = "";
        String sectionContent1 = "";

        Boolean success = false;
        ArrayList<Element> elements = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            try{
                Document doc = Jsoup.connect(getContext().getString(R.string.invite_us_url)).get();

                // ids = appTitle1, appTitle2, appContent1

                Element title1 = doc.getElementById(getContext().getString(R.string.app_title_1));
                Element title2 = doc.getElementById(getContext().getString(R.string.app_title_2));
                Element content1 = doc.getElementById(getContext().getString(R.string.app_content_1));

                title = parseTitle(title1.toString());
                sectionTitle1 = parseSectionTitle(title2.toString());
                sectionContent1 = parseSectionContent(content1.toString());

                success = true;
            } catch (Exception e) {
                System.out.println(getContext().getString(R.string.failed));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (success) {
                setInviteUsModel(title, sectionTitle1, sectionContent1);
            } else {
                maintenanceMessage();
            }
        }

        private String parseSectionTitle(String tag) {
            String result = "";
            String[] arrOfStr1 = tag.split(">",2);
            if (arrOfStr1[1].contains("<strong>")) {
                String[] arrOfStr2 = arrOfStr1[1].split("<strong>",2);
                String[] arrOfStr3 = arrOfStr2[1].split("</strong>", 2);
                return arrOfStr3[0];
            } else {
                String[] arrOfStr2 = arrOfStr1[1].split("</", 2);
                return arrOfStr2[0];
            }
        }

        private String parseTitle(String tag) {
            String[] arrOfStr = tag.split("<b>",2);
            String[] arrResult = arrOfStr[1].split("<",2);
            String value = arrResult[0];
            if (arrResult.length >= 2) {
                value += parseBTag(arrResult[1]);
            }
            return value;
        }

        private String parseBTag(String tag) {
            String[] arrOfStr = tag.split("br>",2);
            String[] arrResult = arrOfStr[1].split("<a",2);
            String value = arrResult[0];
            if (arrResult.length >1) {
                String[] emailArray1 = arrResult[1].split(" href=",2);
                String[] emailArray2 = emailArray1[1].split(">",2);
                String[] emailArray3 = emailArray2[1].split("</a>", 2);
                value += emailArray3[0];
            }
            return value;
        }

        private String parseSectionContent(String tag) {
            String ret = "";
            String[] arrOfStr1 = tag.split(">", 2);
            String[] arrOfStr2 = arrOfStr1[1].split("<", 2);
            ret += arrOfStr2[0];
            String[] arrOfStr3 = arrOfStr1[1].split(">", 2);
            ret += arrOfStr3[1].split("<",2)[0];
            String[] arrOfStr4 = arrOfStr1[1].split("</a>", 2);
            ret += arrOfStr4[1].split("</p>")[0];
            return ret;
        }
    }

}
