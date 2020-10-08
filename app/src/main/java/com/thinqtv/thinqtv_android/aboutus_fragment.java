package com.thinqtv.thinqtv_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.thinqtv.thinqtv_android.data.model.AboutUsModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link aboutus_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class aboutus_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    final String strMessage = "https://en.wikipedia.org/";


    // TODO: Rename and change types of parameters
    private AboutUsModel aboutUsModel;

    public aboutus_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment aboutus_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static aboutus_fragment newInstance(String param1, String param2) {
        aboutus_fragment fragment = new aboutus_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.aboutus_fragment, container, false);
        ((RelativeLayout) view.findViewById(R.id.rlAboutUs)).setVisibility(View.GONE);

        new WebElementsTask().execute();
        return view;
    }

    private void setAboutUsModel(){
        aboutUsModel = new AboutUsModel();

        aboutUsModel.Title = "ABOUT US";
        aboutUsModel.DetailsSection1Title = "Our Mission";
        aboutUsModel.DetailsSection1Content = "Blah blah blah blah blah";

        aboutUsModel.DetailsSection2Title = "Get Involved";
        aboutUsModel.DetailsSection2Content = "Blah blah blah blah blah";

        TextView title = view.findViewById(R.id.about_us_title);
        TextView section1Title = view.findViewById(R.id.text_view_details1_title);
        TextView section1Content = view.findViewById(R.id.text_view_details1_content);
        TextView section2Title = view.findViewById(R.id.text_view_details2_title);
        TextView section2Content = view.findViewById(R.id.text_view_details2_content);

        title.setText(aboutUsModel.Title);
        section1Title.setText(aboutUsModel.DetailsSection1Title);
        section1Content.setText(aboutUsModel.DetailsSection1Content);
        section2Title.setText(aboutUsModel.DetailsSection2Content);
        section2Content.setText(aboutUsModel.DetailsSection2Title);
        ((RelativeLayout) view.findViewById(R.id.rlAboutUs)).setVisibility(View.VISIBLE);
    }

    private class WebElementsTask extends AsyncTask<Void, Void, Void> {
        String result;
        ArrayList<Element> elements = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            try{
                Document doc = Jsoup.connect("https://www.thinq.tv/").get();
                Elements bigTextElements = doc.getElementsByClass("text-center maroon mt-5 pl-2 pt-5");
                for (Element element: bigTextElements) {
                    String s = element.child(0).toString();
                    System.out.println(s);
                    // TODO: Remove the tags and use resulting string for the AboutUs Model
                }
                System.out.println("elements.size() = " + elements.size());
            } catch (Exception e) {
                System.out.println("FAILED");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            setAboutUsModel();
        }
    }
}
