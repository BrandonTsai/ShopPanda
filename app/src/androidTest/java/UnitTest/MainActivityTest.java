package UnitTest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.EditText;

import com.example.brandon.jpbestbuy.MainActivity;
import com.example.brandon.jpbestbuy.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ty on 2015/7/30.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mMainActivity;


	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		//Intent it = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
		//startActivity(it, null, null);
		injectInstrumentation(InstrumentationRegistry.getInstrumentation());
		mMainActivity = getActivity();
	}


	public void testnoMockupInit() throws Exception {

		SharedPreferences.Editor prefEditor = mMainActivity.getPreferences(mMainActivity.MODE_PRIVATE).edit();
		prefEditor.putBoolean("mockup_inited", true);
		prefEditor.commit();
		Button et = (Button) mMainActivity.findViewById(R.id.ComputeBestResult);
		//et.setText("yes");
		assertEquals("Compute", et.getText().toString());
		boolean reality = mMainActivity.noMockupInit();
		final boolean expected = false;
		assertEquals(expected, reality);

	}

	public void testnoComputeResultButton() throws Exception {
		onView(withId(R.id.ComputeBestResult))
				.perform(click());
	}


	@Override
	public void tearDown() throws Exception {
		mMainActivity = null;
		super.tearDown();
	}

}
