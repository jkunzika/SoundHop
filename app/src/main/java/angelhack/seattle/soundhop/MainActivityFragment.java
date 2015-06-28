package angelhack.seattle.soundhop;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    TextView groupTitle;
    View tabLayout;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Light.ttf");
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //Set the font of all the TextViews to Avenir
        ((TextView)v.findViewById(R.id.group_title)).setTypeface(font);
        ((TextView)v.findViewById(R.id.tab_name)).setTypeface(font);
        ((TextView)v.findViewById(R.id.tab_artist)).setTypeface(font);

        //Map References
        groupTitle = (TextView)v.findViewById(R.id.group_title);
        tabLayout = v.findViewById(R.id.tab_layout);
        YoYo.with(Techniques.FadeOut).duration(1).playOn(tabLayout);

        //Listeners
        groupTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                groupTitle.clearFocus();
            }
        });
        return v;
    }
}
