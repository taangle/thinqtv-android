package com.thinqtv.thinqtv_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinqtv.thinqtv_android.data.model.InviteUsModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;

public class inviteus_fragment extends Fragment {
    private static final String THINQTV_ROOM_NAME = "ThinqTV";
    private static final String screenNameKey = "com.thinqtv.thinqtv_android.SCREEN_NAME";
    private static String lastScreenNameStr = "";
    private View view;

    private InviteUsModel inviteUsModel;

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
                    makePhoneCall();
                }
            });
        }
    }

    protected void makePhoneCall() {
        String number = "5208343218";
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        startActivity(callIntent);
        startActivity(callIntent);
    }


    private class WebElementsTask extends AsyncTask<Void, Void, Void> {
        String title = "";

        String sectionTitle1 = "";
        String sectionContent1 = "";

        ArrayList<Element> elements = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            try{
                Document doc = Jsoup.connect("https://www.thinq.tv/drschaeferspeaking").get();

                //Get Title value
                Elements titleElements = doc.getElementsByClass("text-white pt-5");
                title = parseTag(titleElements.get(0).toString());

                // Red text view
                Elements redTextSectionTitle = doc.getElementsByClass("maroon");
                sectionTitle1 = parseWhiteClass(redTextSectionTitle.get(0).toString());
                Elements redTextSectionContent = doc.getElementsByClass("h5 text-left");
                sectionContent1 = parseRerContent(redTextSectionContent.get(0).toString());

            } catch (Exception e) {
                System.out.println("FAILED");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            setInviteUsModel(title, sectionTitle1, sectionContent1);
        }

        private String parseWhiteClass(String tag) {
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

        private String parseTag(String tag) {
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

        private String parseRerContent(String tag) {
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
