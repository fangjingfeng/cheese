package codingpark.net.cheesecloud;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.CompoundButton;

public class Settings extends Activity {
    private boolean mHiddenChanged              = false;
    private boolean mThumbnailChanged           = false;
    private boolean mSortChanged                = false;

    private boolean hidden_state                = false;
    private boolean thumbnail_state             = false;
    // Default text color black
    private int color_state                     = 0xFF000000;
    private int sort_state                      = 0;
    private Intent is                           = new Intent();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Intent i = getIntent();
        hidden_state        = i.getExtras().getBoolean("HIDDEN");
        thumbnail_state     = i.getExtras().getBoolean("THUMBNAIL");
        sort_state          = i.getExtras().getInt("SORT");

        final CheckBox hidden_bx        = (CheckBox)findViewById(R.id.setting_hidden_box);
        final CheckBox thumbnail_bx     = (CheckBox)findViewById(R.id.setting_thumbnail_box);
        final ImageButton sort_bt       = (ImageButton)findViewById(R.id.settings_sort_button);

        hidden_bx.setChecked(hidden_state);
        thumbnail_bx.setChecked(thumbnail_state);

        hidden_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hidden_state = isChecked;

                is.putExtra("HIDDEN", hidden_state);
                mHiddenChanged = true;
            }
        });

        thumbnail_bx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                thumbnail_state = isChecked;

                is.putExtra("THUMBNAIL", thumbnail_state);
                mThumbnailChanged = true;
            }
        });

        sort_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                CharSequence[] options = {getResources().getString(R.string.None),
                        getResources().getString(R.string.Alphabetical),
                        getResources().getString(R.string.Type)};

                builder.setTitle(getResources().getString(R.string.Sort_by));
                builder.setIcon(R.drawable.filter);
                builder.setSingleChoiceItems(options, sort_state, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch(index) {
                            case 0:
                                sort_state = 0;
                                mSortChanged = true;
                                is.putExtra("SORT", sort_state);
                                break;

                            case 1:
                                sort_state = 1;
                                mSortChanged = true;
                                is.putExtra("SORT", sort_state);
                                break;

                            case 2:
                                sort_state = 2;
                                mSortChanged = true;
                                is.putExtra("SORT", sort_state);
                                break;
                        }
                    }
                });

                builder.create().show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mHiddenChanged)
            is.putExtra("HIDDEN", hidden_state);

        // TODO Support custom text color
        is.putExtra("COLOR", color_state);

        if(!mThumbnailChanged)
            is.putExtra("THUMBNAIL", thumbnail_state);

        if(!mSortChanged)
            is.putExtra("SORT", sort_state);

        setResult(RESULT_CANCELED, is);
    }
}
