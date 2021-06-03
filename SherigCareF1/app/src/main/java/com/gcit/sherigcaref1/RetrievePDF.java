package com.gcit.sherigcaref1;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RetrievePDF extends AppCompatActivity {
    ListView listview;
    DatabaseReference databaseReference;

    List<putPDF> uploadedPDF;
    EditText editTextStd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_p_d_f);

        listview=findViewById(R.id.listView);
        editTextStd=findViewById(R.id.editText);
        uploadedPDF=new ArrayList<>();
        String Details=getIntent().getStringExtra("Details");



        retrievePDFFiles();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                putPDF putPDF=uploadedPDF.get(i);

                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(putPDF.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void retrievePDFFiles() {


        databaseReference= FirebaseDatabase.getInstance().getReference("ResultUpload");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
//        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                putPDF putPDF=ds.getValue(putPDF.class);
                uploadedPDF.add(putPDF);
                }

                String[] uploadsName=new String[uploadedPDF.size()];
//
                for (int i=0; i<uploadsName.length;i++){
                    uploadsName[i]=uploadedPDF.get(i).getName();

                }
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1,uploadsName){

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view=super.getView(position, convertView, parent);
                        TextView textView=(TextView) view.findViewById(android.R.id.text1);
                        textView.setTextSize(20);
                        return view;
                    }
                };
                listview.setAdapter(arrayAdapter);
            }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        System.out.println("Failed to Retrieve "+ databaseReference.getCode());
                    }
        });
    }
}