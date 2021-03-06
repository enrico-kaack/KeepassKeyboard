package ek.de.keepasskeyboard.wizard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ek.de.keepasskeyboard.R;

public class Wizard extends AppCompatActivity implements OnWizardNavigation {
    public static final int EXIT = 0;
    public static final int FRAG_MASTER_PW = 2;
    public static final int FRAG_PW = 3;
    public static final int FRAG_PIN = 4;
    public static final int FRAG_BLUETOOTH_DEVICE_LIST = 5;
    public static final int FRAG_ENCRYPTION_KEY = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        new WizardData().context = getApplicationContext();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SelectAuthentification firstFragment = new SelectAuthentification();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    private Fragment getFragmentByNumber(int fragmentNumber){
        Fragment frag;
        switch (fragmentNumber){
            case FRAG_MASTER_PW:
                frag = null;
                break;
            case FRAG_PW:
                frag = new SetUpMasterPw();
                break;
            case FRAG_BLUETOOTH_DEVICE_LIST:
                frag = new DeviceList();
                break;
            case EXIT:
                finish();
                frag = null;
                break;
            case FRAG_ENCRYPTION_KEY:
                frag = new EncryptionKeyInput();
                break;
            default:
                frag = null;
                break;
        }
        return frag;
    }

    @Override
    public void goToFragment(int fragmentNumber) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

     // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
        Fragment target = getFragmentByNumber(fragmentNumber);
        if (target != null){
            transaction.replace(R.id.fragment_container, target);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }else{
            finish();
        }


    }
}
