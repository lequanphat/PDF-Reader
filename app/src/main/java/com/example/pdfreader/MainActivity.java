package com.example.pdfreader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.FileNotFoundException;
import java.io.IOException;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    TextView numberOfTotalPage;
    ImageButton btnPickFile , btnNextPage, btnPrePage;
    PdfRenderer pdfRenderer;
    ImageView pdfView;
    int totalPage = 0;
    int displayPage = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    public void initViews(){
        numberOfTotalPage = findViewById(R.id.page);
        btnPickFile = findViewById(R.id.pick_btn);

        btnNextPage = findViewById(R.id.next_btn);
        btnPrePage = findViewById(R.id.pre_btn);
        pdfView = findViewById(R.id.pdf_view);

        btnPickFile.setOnClickListener(this);
        btnNextPage.setOnClickListener(this);
        btnPrePage.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id==R.id.pick_btn){
            handlePickFile();
        }else if(id==R.id.pre_btn){
            handlePrePage();
        }else if(id==R.id.next_btn){
            handleNextPage();
        }
    }
    public void handlePickFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, 99);
    }
    public void handlePrePage(){
        if(displayPage > 0){
            displayPage--;
            handleDisplayPdfView(displayPage);
        }
    }
    public void handleNextPage(){
        if(displayPage < totalPage - 1){
            displayPage++;
            handleDisplayPdfView(displayPage);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 99 && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                try{
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                    totalPage = pdfRenderer.getPageCount();
                    displayPage = 0;
                    handleDisplayPdfView(displayPage);
                }catch (FileNotFoundException fnfe){

                }catch(IOException e){

                }
            }
        }
    }
    public void handleDisplayPdfView(int n){
        if(pdfRenderer != null){
            PdfRenderer.Page page = pdfRenderer.openPage(n);
            Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pdfView.setImageBitmap(mBitmap);
            page.close();
            numberOfTotalPage.setText((n+1)+"/"+totalPage);
        }
    }
    public void onDestroy(){
        super.onDestroy();
        if(pdfRenderer != null){
            pdfRenderer.close();
        }
    }






}