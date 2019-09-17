package in.becandid.app.becandid.ui.filter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.becandid.app.becandid.R;
import in.becandid.app.becandid.dto.PostsModel;
import in.becandid.app.becandid.onBoarding.WatchVideoAdActivity;
import in.becandid.app.becandid.ui.base.BaseActivity;
import in.becandid.app.becandid.ui.base.Constants;

public class NewFilterActivity extends BaseActivity implements PremiumSearchResultMvpView {
    private Spinner spinner_age;
    private Spinner spinner_gender;
    private Button search_premium_button;
    private Button search_premium_button_user;
    private EditText premium_post_search_value;
    private String age_selected;
    private String gender_selected;

    @Inject
    PremiumSearchResultMvpPresenter<PremiumSearchResultMvpView> mPresenter;


    private static final String[] age_groups = {
            "Select age",
            "13-17 age",
            "18-24 age",
            "25-34 age",
            "35-44 age",
            "45+ age"
    };

    private static final String[] gender_groups = {
            "Select Gender",
            "Male",
            "Female",
            "LGBT"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_filter);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(NewFilterActivity.this);

        toolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewFilterActivity.this, WatchVideoAdActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        spinner_age = (Spinner) findViewById(R.id.spinner_age);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, age_groups);


        spinner_age.setAdapter(adapter);

        spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age_selected = String.valueOf(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                age_selected = null;

            }
        });

      //  spinner_age.setItems(age_groups);
        search_premium_button = (Button) findViewById(R.id.search_premium_button);
        premium_post_search_value = (EditText) findViewById(R.id.premium_post_search_value);
        search_premium_button_user = (Button) findViewById(R.id.search_premium_button_user);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Premium Search");
        toolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);

        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, gender_groups);


        spinner_gender.setAdapter(gender_adapter);


        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_selected = String.valueOf(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender_selected = null;

            }
        });

        search_premium_button_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (age_selected == null || gender_selected == null){
                    Toast.makeText(NewFilterActivity.this, "Please select both drop down", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(NewFilterActivity.this, PremiumSearchResultActivity.class);

                    intent.putExtra(Constants.AGE, age_selected);
                    intent.putExtra(Constants.GENDER, gender_selected);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        search_premium_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (premium_post_search_value.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(NewFilterActivity.this, "Please enter text to search", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(NewFilterActivity.this, PremiumSearchResultActivity.class);
                    intent.putExtra(Constants.SEARCH, premium_post_search_value.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NewFilterActivity.this, WatchVideoAdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();

        super.onDestroy();
    }

    @Override
    protected void setUp() {

    }

    @Override
    public void getAgeGenderPosts(List<PostsModel> postsModels) {

    }

    @Override
    public void getAgeGenderPosts02(List<PostsModel> postsModels) {

    }

    @Override
    public void getSearchPost(List<PostsModel> postsModels) {

    }

    @Override
    public void getSearchPost02(List<PostsModel> postsModels) {

    }

   /* public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_group_image:
                Intent intent = new Intent(getBaseContext(), SearchGroupPremiumActivity.class);
                startActivityForResult(intent, Constants.GROUP_ID);
                break;
            case R.id.search_group_text:

                Intent intent02 = new Intent(getBaseContext(), SearchGroupPremiumActivity.class);
                startActivityForResult(intent02, Constants.GROUP_ID);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.GROUP_ID){

            search_group_text.setText(data.getStringExtra(Constants.GROUP_ID_NAME));

            group_id = data.getStringExtra(Constants.GROUP_ID_RETURN);

        }
    }
    */



}
