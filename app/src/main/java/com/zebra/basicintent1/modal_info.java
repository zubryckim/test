package com.zebra.basicintent1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class modal_info extends BottomSheetDialogFragment {

    public int counter;


    private TextView count;
    private BottomSheetListener mListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_info, container, false);


        count = (TextView) v.findViewById(R.id.text_count);
        count.setText("Ten składnik był już sczytany na tym urządzeniu " + counter+ " razy");

        Button button1 = v.findViewById(R.id.button_ok);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("clicked");
                dismiss();
            }
        });

        return v;

    }

    public interface  BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement BottomSheedListener");
        }
    }

}
