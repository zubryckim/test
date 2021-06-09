package com.zebra.basicintent1;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Locals2Activity extends AppCompatActivity {

    Globals glob;

    ListView lv;
    ArrayAdapter<String> adapter;
    EditText inputSearch;
    DbHelper dataHelper;
    ArrayList<String> locales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locals2);

        glob = new Globals();
        this.setTitle("Skaner ZTM v" + glob.getVersionName(this));

        //pobranie listy pól spisowych z MainActivity
        //Intent intent = getIntent();
        //locales = intent.getStringArrayListExtra("lokalizacje");

        dataHelper = new DbHelper(this);
        locales = dataHelper.getLocations();

        lv = (ListView) findViewById(R.id.list_view);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        //ukrycie klawiatury
        inputSearch.setInputType(InputType.TYPE_NULL);

        //pokazanie klawiatury po kliknięciu
        inputSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) inputSearch.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputSearch, InputMethodManager.SHOW_IMPLICIT);
            }});


        //Adding item to listViev
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.location, locales);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(Locals2Activity.this, locals[i], Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lokalizacja", (String)adapterView.getItemAtPosition(i));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            }
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                 Locals2Activity.this.adapter.getFilter().filter(cs);
            }
            @Override
            public void afterTextChanged(Editable editable) {
                }
            });
        }


}
