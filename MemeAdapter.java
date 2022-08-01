package com.ms.memes;

import static android.content.Context.VIBRATOR_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.ViewHolder> {
File root;
    static File dir;
static FileOutputStream fos;
    private static ArrayList<Bitmap> localDataSet;
    Bitmap mIcon ;
  //  private  ArrayList<Integer> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

  //layout Var
      private final ImageView memeimage;
      private static  ImageButton dwnld,share;
      private  static  View temp;

        public ViewHolder(View view) {
            super(view);
            temp = view;
              //var link
             memeimage = view.findViewById(R.id.meme_image);
             dwnld = view.findViewById(R.id.dwnld);
             share = view.findViewById(R.id.shar);


            Vibrator vb = (Vibrator) dwnld.getContext().getSystemService(VIBRATOR_SERVICE);
             dwnld.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Dexter.withContext(dwnld.getContext()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                         @Override
                         public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                             new Thread(){
                                 @Override
                                 public void run() {
                                     downloadimage(view);
                                 }
                             }.start();


                             vb.vibrate(300);
                         }

                         @Override
                         public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                         }

                         @Override
                         public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                           permissionToken.continuePermissionRequest();
                         }
                     }).check();


                 }
             });

             //share option is in devlopment
/*
             share.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                  /*   Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                     // type of the content to be shared
                     sharingIntent.setType("Image/P");
                     sharingIntent.setType("Image/PNG");

                     // Body of the content
                     String shareBody = "Meme";

                     // subject of the content. you can share anything
                     String shareSubject = "Meme";

                     // passing body of the content
                     sharingIntent.putExtra(Intent.EXTRA_SUBJECT, localDataSet.get(getAdapterPosition()));

                     // passing subject of the content
                     sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                     share.getContext().startActivity(Intent.createChooser(sharingIntent, "Share using"));

                     String image_path;

                     image_path =  new File(dir,"image-"+Integer.toString(getAdapterPosition())+".png").getAbsolutePath();
                     File file = new File(image_path);
                     android.net.Uri uri = Uri.fromFile(file);
                     Intent intent = new Intent(Intent.ACTION_SEND);
                     intent .setType("image/*");
                     intent .putExtra(Intent.EXTRA_STREAM, uri);
                     share.getContext().startActivity(intent );
                 }*/

             }

        private  void downloadimage(View v){
            try {
                File tmp = new File(dir,"image-"+Integer.toString(getAdapterPosition())+".png");
                fos = new FileOutputStream(tmp);
                Bitmap b = localDataSet.get(getAdapterPosition());
                b.compress(Bitmap.CompressFormat.PNG,100,fos);
                fos.close();

                Snackbar.make(v.findViewById(R.id.dwnld),"Image Downloaded -> "+tmp.getAbsolutePath(),Snackbar.LENGTH_INDEFINITE).setAction("OK!", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MemeAdapter(ArrayList<Bitmap> dataSet) {
        this.localDataSet = dataSet;
        root  = Environment.getExternalStorageDirectory();
        dir = new File(root.getAbsolutePath(),"/Download");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_meme_adapter, viewGroup, false);

        return new ViewHolder(view);
    }


    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.memeimage.setImageBitmap(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
