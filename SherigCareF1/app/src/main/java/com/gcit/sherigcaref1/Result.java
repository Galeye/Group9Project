package com.gcit.sherigcaref1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Result extends AppCompatActivity {

    EditText mEditText;
    Button mButton;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.btn);


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("ResultUpload");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }

        });
    }
    private void selectPDF(){
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "PDF File Select"), 12);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==12 && resultCode==RESULT_OK && data!=null ){


            mButton.setEnabled(true);
            mEditText.setText(data.getDataString()
                    .substring(data.getDataString().lastIndexOf("/") + 1));
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDFFileFirebase(data.getData());
                }


            });
        }


    }



    private void uploadPDFFileFirebase(Uri data) {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        StorageReference reference=storageReference.child("Upload" + System.currentTimeMillis()+ ".pdf");


        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri=uriTask.getResult();

                putPDF putPDF=new putPDF(mEditText.getText().toString(), uri.toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);
                //databaseReference.child("ResultUpload").child(details).setValue(putPDF);
                Toast.makeText(Result.this, "Please enter email...", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();



            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Upload.."+(int) progress+ "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Result.this, "File", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void RetrieveFile(View view) {

        Intent intent=new Intent(getApplicationContext(), RetrievePDF.class);

        startActivity(intent);
//        startActivity(new Intent(getApplicationContext(),RetrievePDF.class));
    }
}