package ek.de.keepasskeyboard.wizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import ek.de.keepasskeyboard.R;
import ek.de.keepasskeyboard.wizard.OnWizardNavigation;
import ek.de.keepasskeyboard.wizard.Wizard;

public class SelectAuthentification extends Fragment implements View.OnClickListener{
    int selectedElement = 0;
    int[] radio_buttons = new int[3];

    OnWizardNavigation onNavigation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.activity_select_authentification, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        radio_buttons[0] = R.id.rb_masterpw;
        radio_buttons[1] = R.id.rb_pw;
        radio_buttons[2] = R.id.rb_pin;

        for (int i=0; i<radio_buttons.length;i++){
            getView().findViewById(radio_buttons[i]).setOnClickListener(this);
        }

        FloatingActionButton btn_next = (FloatingActionButton) getView().findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = -1;
                int navigation = -1;
                switch (selectedElement){
                    case R.id.rb_masterpw:
                        mode = 0;
                        navigation = Wizard.FRAG_MASTER_PW;
                        break;
                    case R.id.rb_pw:
                        mode = 1;
                        navigation = Wizard.FRAG_PW;
                        break;
                    case R.id.rb_pin:
                        mode = 2;
                        navigation = Wizard.FRAG_PIN;
                        break;
                    default:
                        mode = -1;
                        break;
                }

                if (mode != -1 && navigation != -1) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("ENCRYPTION_MODE", navigation);
                    editor.commit();


                    onNavigation.goToFragment(navigation);
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
       selectedElement = v.getId();
        for (int i=0; i<radio_buttons.length;i++){
            if (selectedElement != radio_buttons[i]) {
                ((RadioButton) getView().findViewById(radio_buttons[i])).setChecked(false);
            }else{
                ((RadioButton) getView().findViewById(radio_buttons[i])).setChecked(true);
            }
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            onNavigation = (OnWizardNavigation) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
