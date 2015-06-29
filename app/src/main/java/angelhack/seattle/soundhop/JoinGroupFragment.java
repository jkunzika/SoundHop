package angelhack.seattle.soundhop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    static View fbLogin,join,create;
    static TextView joinField;

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
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinField = new EditText(getActivity());
                joinField.setHint("Enter Group Name");

                new AlertDialog.Builder(getActivity())
                        .setView(joinField)
                        .setTitle("Group Name:")
                        .setView(joinField)
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Globals.targetIP = joinField.getText().toString();
                                Globals.role = 1;
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment, new MainActivityFragment()).commit();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinField = new EditText(getActivity());
                joinField.setHint("Enter Group Name");

                new AlertDialog.Builder(getActivity())
                        .setView(joinField)
                        .setTitle("Group Name:")
                        .setView(joinField)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Globals.groupName = joinField.getText().toString();
                                MainActivity.firebase.child("groupName").setValue(Globals.groupName);
                                Globals.role = 0;
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment, new MainActivityFragment()).commit();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();

            }
        });

        //Temporarily here to make testing faster
        animateTransition();

        return v;
    }

    public static void animateTransition(){
        fbLogin.setVisibility(View.INVISIBLE);
        join.setVisibility(View.VISIBLE);
        create.setVisibility(View.VISIBLE);
        /*
        //Animate buttons to slide in, and fb login button to slide out
        YoYo.with(Techniques.SlideOutLeft).duration(1000).delay(1500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

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
        YoYo.with(Techniques.SlideInRight).duration(1000).delay(1500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                join.setVisibility(View.VISIBLE);
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
        }).playOn(join);
        YoYo.with(Techniques.SlideInRight).duration(1000).delay(1500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
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
        }).playOn(create);
        */
    }
}
