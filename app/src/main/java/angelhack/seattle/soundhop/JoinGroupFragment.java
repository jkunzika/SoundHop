package angelhack.seattle.soundhop;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;
import java.util.Random;


/**
 * A placeholder fragment containing a simple view.
 */
public class JoinGroupFragment extends Fragment {
    View fbLogin,join,create;

    public JoinGroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.com_parse_ui_parse_login_fragment, container, false);
        fbLogin = v.findViewById(R.id.facebook_login);
        join = v.findViewById(R.id.login_join);
        create = v.findViewById(R.id.login_create);

        //Configure listeners
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        //Animate buttons to slide in, and fb login button to slide out
        YoYo.with(Techniques.SlideOutLeft).duration(500).delay(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                YoYo.with(Techniques.SlideInLeft).duration(500).playOn(join);
                YoYo.with(Techniques.SlideInLeft).duration(500).playOn(create);
                join.setVisibility(View.VISIBLE);
                create.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(fbLogin);

        return v;
    }
}
