package UnitTest;

import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.example.brandon.jpbestbuy.BestShopResult;
import com.example.brandon.jpbestbuy.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ty on 2015/10/28.
 */
public class BestShopResultTest extends ActivityInstrumentationTestCase2<BestShopResult> {

    private BestShopResult mBestShopResultActivity;


    public BestShopResultTest() {
        super(BestShopResult.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        //Intent it = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
        //startActivity(it, null, null);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mBestShopResultActivity = getActivity();
    }


    public void testnoMockupInit() throws Exception {

        SharedPreferences.Editor prefEditor = mBestShopResultActivity.getPreferences(mBestShopResultActivity.MODE_PRIVATE).edit();
        prefEditor.putBoolean("mockup_inited", true);
        prefEditor.commit();
        Button et = (Button) mBestShopResultActivity.findViewById(R.id.ComputeBestResult);
        //et.setText("yes");
        assertEquals("Compute", et.getText().toString());
    }

    public void testnoComputeResultButton() throws Exception {
        onView(withId(R.id.ComputeBestResult))
                .perform(click());
    }


    @Override
    public void tearDown() throws Exception {
        mBestShopResultActivity = null;
        super.tearDown();
    }

}
