package com.example.aqua_v2;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.StorageReference;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    private List<StorageReference> referenceList;
    private Context context;
    private FirebaseFunctions mFunctions;
    private Notification notification;
    ImageButton closeBtn;
    MaterialButton cancelBtn;
    MaterialButton okBtn;
    Future<File> downloading;
    Activity activity;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");


    public void setReferenceList(List<StorageReference> referenceList) {
        this.referenceList = referenceList;
    }

    public ReportAdapter(List<StorageReference> referenceList, Context context, Activity activity, FirebaseFunctions mFunctions) {
        this.referenceList = referenceList;
        this.context = context;
        this.activity = activity;
        this.mFunctions = mFunctions;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView fileName;
        private MaterialCheckBox reportCheckBox;
        private StorageReference storageReference;

        public MyViewHolder(final View view) {
            super(view);
            fileName = view.findViewById(R.id.reportFileName);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.download_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            closeBtn = dialog.findViewById(R.id.closeBtn);
            cancelBtn = dialog.findViewById(R.id.cancel_button);
            okBtn = dialog.findViewById(R.id.logoutBtn);

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloading != null && !downloading.isCancelled()) {
                        resetDownload();
                        return;
                    }
                    storageReference.getDownloadUrl().addOnSuccessListener((result) -> {
                        File newFile = new File(Environment.getExternalStoragePublicDirectory
                                (Environment.DIRECTORY_DOWNLOADS),
                                storageReference.getName());
                        if (newFile.exists()) {
                            int num = 1;
                            String filename = storageReference.getName().split("\\.")[0];
                            newFile = new File(Environment.getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_DOWNLOADS),
                                    String.format("%s (%d).pdf", filename, num));
                            while (newFile.exists()) {
                                newFile = new File(Environment.getExternalStoragePublicDirectory
                                        (Environment.DIRECTORY_DOWNLOADS),
                                        String.format("%s (%d).pdf", filename, num++));
                            }

                        }
                        downloading = Ion.with(context).load(result.toString())
                                .write(newFile);

                        ((ResponseFuture<File>) downloading).setCallback((e, resultFile) -> {
                            resetDownload();
                            if (e != null) {
                                dialog.dismiss();
                                Toast.makeText(context, "Error downloading file", Toast.LENGTH_LONG).show();
                                return;
                            }

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
                            String CHANNEL_ID = UUID.randomUUID().toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
                                // Configure the notification channel.

                                notificationChannel.setDescription("Sample Channel description");

                                notificationChannel.enableLights(true);
                                notificationChannel.setLightColor(Color.RED);
                                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                                notificationChannel.enableVibration(true);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }

                            notification = new NotificationCompat.Builder(activity, CHANNEL_ID)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setWhen(System.currentTimeMillis())
                                    .setContentTitle("Download notification")
                                    .setContentText("Your File " + storageReference.getName() + "Finished Download")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .build();

                            notificationManager.notify(1, notification);

                            addUserLog("User Downloaded Report " + storageReference.getName());
                            dialog.dismiss();
                            Toast.makeText(context, "File Download complete", Toast.LENGTH_LONG).show();
                        });
                    });
                }
            });
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

        void resetDownload() {
            // cancel any pending download
            downloading.cancel(true);
            downloading = null;

            // reset the ui
        }

        void addUserLog(String userActivity) {
            String currentDateAndTime = sdf.format(new Date());
            Map<String, String> data = new HashMap<>();
            data.put("activity", userActivity);
            data.put("datetime", currentDateAndTime);
            mFunctions
                    .getHttpsCallable("logUserActivity")
                    .call(data);
        }
    }

    @NonNull
    @Override
    public ReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_report_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.MyViewHolder holder, int position) {
        String fileName = referenceList.get(position).getName();
        holder.fileName.setText(fileName);
        holder.storageReference = referenceList.get(position);

    }

    @Override
    public int getItemCount() {
        return referenceList.size();
    }

    public interface RecycleViewClickListener {
        void onCLick(View v, int position);
    }


}
