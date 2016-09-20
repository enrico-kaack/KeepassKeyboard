package ek.de.keepasskeyboard.wizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ek.de.keepasskeyboard.Constants;
import ek.de.keepasskeyboard.R;

/**
 * Set up the pre shared key for the connection to the arduino device
 * Created by Enrico on 16.06.2016.
 */


public class EncryptionKeyInput extends Fragment {
    OnWizardNavigation onNavigation;
    Context context;


    @Override
    public void onResume() {
        super.onResume();
        final EditText in_encryptionKey = (EditText) getView().findViewById(R.id.in_encryptionKey);
        FloatingActionButton btn_next = (FloatingActionButton) getView().findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (in_encryptionKey.getText().toString().length() > 0) {
                    WizardData data = new WizardData();
                    data.deviceEncryptionPw = in_encryptionKey.getText().toString();
                    data.commit();
                    onNavigation.goToFragment(Wizard.EXIT);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_encryption_key, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWizardNavigation) {
            onNavigation = (OnWizardNavigation) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}
