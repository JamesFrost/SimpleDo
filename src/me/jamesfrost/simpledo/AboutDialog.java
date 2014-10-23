package me.jamesfrost.simpledo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * About activity that shows information about the app.
 *
 * Created by James Frost on 18/09/2014.
 */
public class AboutDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        ((TextView) findViewById(R.id.textView3)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.textView3)).setText(Html.fromHtml(getResources().getString(R.string.about_license)));

        ((TextView) findViewById(R.id.textView4)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.textView4)).setText(Html.fromHtml(getResources().getString(R.string.about_madeby)));

        TextView textViewVersionNumber = (TextView) findViewById(R.id.textViewVersionNumber);
        try {
            textViewVersionNumber.setText("v" + this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Button buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
