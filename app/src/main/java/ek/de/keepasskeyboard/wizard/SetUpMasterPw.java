package ek.de.keepasskeyboard.wizard;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ek.de.keepasskeyboard.encryption.EncryptionModul;
import ek.de.keepasskeyboard.R;


public class SetUpMasterPw extends Fragment {
    OnWizardNavigation onNavigation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pw__input, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

        FloatingActionButton btn_next = (FloatingActionButton) getView().findViewById(R.id.btn_next);
        final EditText pw = (EditText) getView().findViewById(R.id.in_pw);
        final EditText pw_repeat = (EditText) getView().findViewById(R.id.in_pw_repeat);
        final EditText mpw = (EditText) getView().findViewById(R.id.in_mpw);
        final EditText mpw_repeat = (EditText) getView().findViewById(R.id.in_mpw_repeat);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KEEPASS", pw.getText().toString() + " " + pw_repeat.getText().toString());
                Log.d("KEEPASS", mpw.getText().toString() + " " + mpw_repeat.getText().toString());
                if (pw.getText().toString().length() > 0 && mpw.getText().toString().length() > 0 && pw.getText().toString().equals(pw_repeat.getText().toString()) && mpw.getText().toString().equals(mpw_repeat.getText().toString())) {
                    //EncryptionModul encryptionModul = new EncryptionModul(getActivity());
                    //encryptionModul.encryptMPByPassword(pw.getText().toString(), mpw.getText().toString());
                    WizardData wizardData = new WizardData();
                    wizardData.masterPw = mpw.getText().toString();
                    wizardData.userPw = pw.getText().toString();
                    onNavigation.goToFragment(Wizard.FRAG_BLUETOOTH_DEVICE_LIST);
                }


            }
        });
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

    @Override
    public void onDetach() {
        super.onDetach();
        onNavigation = null;
    }


}
