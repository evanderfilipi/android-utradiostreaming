package com.evanderfilipi.radiout;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Evander Filipi on 27/04/2017.
 */
public class StreamService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener {

        public static final String URL_STREAM = "http://utradio.ut.ac.id:8000/liv.ogg?_=1";
        private static final int NOTIFICATION_ID = 1;
        PhoneStateListener phoneStateListener;
        TelephonyManager telephonyManager;
        boolean isPausedInCall = false;
        NotificationCompat.Builder builder;

        Intent bufferIntent;
        public static final String BROADCAST_BUFFER = "com.evanderfilipi.utradio.streamingaudio.broadcastbuffer";
        MediaPlayer mediaPlayer = new MediaPlayer();

        public void onCreate() {
                super.onCreate();
                Log.d("create", "service created");

                bufferIntent = new Intent(BROADCAST_BUFFER);

                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setOnBufferingUpdateListener(this);

                mediaPlayer.reset();
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
                Log.d("play", "play streaming");

                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                phoneStateListener = new PhoneStateListener() {
                        @Override
                        public void onCallStateChanged(int state, String incomingNumber) {
                                switch (state) {
                                        case TelephonyManager.CALL_STATE_OFFHOOK:
                                        case TelephonyManager.CALL_STATE_RINGING:
                                                if (mediaPlayer != null) {
                                                        pauseMedia();
                                                        isPausedInCall = true;
                                                }
                                                break;
                                        case TelephonyManager.CALL_STATE_IDLE:
                                                if (mediaPlayer != null) {
                                                        if (isPausedInCall) {
                                                                isPausedInCall = false;
                                                                playMedia();
                                                        }
                                                }
                                                break;
                                }
                        }
                };

                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

                initNotification();

                mediaPlayer.reset();

                if (!mediaPlayer.isPlaying()) {
                        try {
                                Log.d("stream", "" + URL_STREAM);
                                mediaPlayer.setDataSource(URL_STREAM);

                                // sent to UI radio is buffer
                                sendBufferingBroadcast();

                                mediaPlayer.prepareAsync();
                        } catch (IllegalArgumentException e) {
                                Log.d("error", e.getMessage());
                        } catch (IllegalStateException e) {
                                Log.d("error", e.getMessage());
                        } catch (IOException e) {
                                Log.d("error", e.getMessage());
                        }
                }

                return START_STICKY;
        }

        public IBinder onBind(Intent intent) {
                return null;
        }

        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

        }

        public void onCompletion(MediaPlayer mediaPlayer) {
                stopMedia();
                stopSelf();
        }

        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                switch (what) {
                        case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                                Toast.makeText(this, "Radio error", Toast.LENGTH_SHORT).show();
                                bufferIntent.putExtra("buffering", "2");
                                sendBroadcast(bufferIntent);
                                break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                                Toast.makeText(this, "Radio terhenti", Toast.LENGTH_SHORT).show();
                                bufferIntent.putExtra("buffering", "2");
                                sendBroadcast(bufferIntent);
                                break;
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                                Toast.makeText(this, "Tidak dapat terhubung ke UT Radio", Toast.LENGTH_SHORT).show();
                                bufferIntent.putExtra("buffering", "2");
                                sendBroadcast(bufferIntent);
                                break;
                }
                return false;
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
                sendBufferCompleteBroadcast();
                playMedia();
        }

        private void pauseMedia() {
                if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                }
        }

        private void playMedia() {
                if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                }
        }

        private void stopMedia() {
                if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                }
        }

        private void sendBufferingBroadcast() {
                bufferIntent.putExtra("buffering", "1");
                sendBroadcast(bufferIntent);
        }

        private void sendBufferCompleteBroadcast() {
                bufferIntent.putExtra("buffering", "0");
                sendBroadcast(bufferIntent);
        }

        public void onDestroy() {
                super.onDestroy();
                Log.d("tag", "remove notification");
                if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                        }
                        mediaPlayer.release();
                }

                if (phoneStateListener != null) {
                        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
                }

                cancelNotification();
        }

        private void initNotification() {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, TabsActivity.class), 0);
                builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_icon)
                        .setContentTitle("UT Radio")
                        .setContentText("Radio Streaming");
                builder.setContentIntent(intent);
                builder.setOngoing(true);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        private void cancelNotification() {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_ID);
                builder.setOngoing(false);
        }
}