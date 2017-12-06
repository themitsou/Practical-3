package gr.academic.city.sdmd.projectissues.ui.activity.fragments;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import gr.academic.city.sdmd.projectissues.R;
import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.service.WorkLogService;
import gr.academic.city.sdmd.projectissues.ui.activity.ClubActivityDetailsActivity;
import gr.academic.city.sdmd.projectissues.util.Constants;
import pl.droidsonroids.gif.GifImageView;


public class ClubActivityDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public interface OnFragmentInteractionListener {
        void onClubActivityDeleted(Long activity);
    }

    private static final String ARG_PARAM1 = "param1";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PROJECT_ISSUES_DATE_FORMAT);
    private static final int CLUB_ACTIVITY_LOADER = 20;
    private static final String EXTRA_ACTIVITY_ID = "Activity_ID";

    private static final int WORK_CYCLE_DURATION = 10 * 1000;
    private static final int SMALL_BREAK_DURATION = 10 * 1000;
    private static final int LONG_BREAK_DURATION = 10 * 1000;


    private long clubActivityId;

    private TextView tvTitle;
    private TextView tvShortNote;
    private TextView tvLongNote;
    private TextView tvDate;
    private TextView tvProgress;
    private TextView tvProgressMessage;
    private Long serverIssueID;
    private String comment;
    private Double workHours;

    private TextView textView;
    private GifImageView gifView;
    private ProgressBar progressBar;
    private ObjectAnimator animation;
    private int pomodori = 0;
    private int greatPomodori = 0;
    private FloatingActionButton myFab;
    private SharedPreferences sharedPref;

    CountDownTimer mCountDownWorkTimer = new CountDownTimer(WORK_CYCLE_DURATION, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvProgressMessage.setText("Let's focus for the next minutes on the issue... ");
            tvProgress.setText(String.format("%d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) + " minutes");
        }

        @Override
        public void onFinish() {
            playSound();
            vibrate();
            showEndWorkNotification(clubActivityId);
            insertIssueComment();
        }
    };

    CountDownTimer mCountDownLongBrakeTimer = new CountDownTimer(LONG_BREAK_DURATION, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvProgressMessage.setText("Let's have a long brake now... ");
            tvProgress.setText(String.format("%d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) + " minutes");

        }

        @Override
        public void onFinish() {
            playSound();
            vibrate();
            showEndBreakNotification(clubActivityId);
            proceedToWork();
        }
    };

    CountDownTimer mCountDownShortBrakeTimer = new CountDownTimer(SMALL_BREAK_DURATION, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvProgressMessage.setText("Let's have a short brake now... ");
            tvProgress.setText(String.format("%d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) + " minutes");
        }

        @Override
        public void onFinish() {
            playSound();
            vibrate();
            showEndBreakNotification(clubActivityId);
            proceedToWork();
        }
    };

    private void playSound() {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this.getContext(), defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vibrate() {
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        v.vibrate(2000);
    }


    private View view;
    private Long mParam1;

    private OnFragmentInteractionListener mListener;

    public ClubActivityDetailsFragment() {
        // Required empty public constructor
    }


    public static ClubActivityDetailsFragment newInstance(Long param1) {
        ClubActivityDetailsFragment fragment = new ClubActivityDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }
        clubActivityId = mParam1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_club_activity_details, container, false);

        greatPomodori = 0;

        tvTitle = (TextView) view.findViewById(R.id.tv_club_activity_title);
        tvShortNote = (TextView) view.findViewById(R.id.tv_club_activity_short_note);
        tvLongNote = (TextView) view.findViewById(R.id.tv_club_activity_long_note);
        tvDate = (TextView) view.findViewById(R.id.tv_club_activity_date);

        tvProgress = (TextView) view.findViewById(R.id.tv_progressText);
        tvProgressMessage = (TextView) view.findViewById(R.id.tv_progressMessage);

        textView = (TextView) view.findViewById(R.id.tv_trophyText);
        gifView = (GifImageView) view.findViewById(R.id.trophyGifView);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setRotation(-90f);
        animation = ObjectAnimator.ofInt(progressBar, "progress", 1000, 0);

        myFab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (animation.isRunning()) {
                    cancelWork();
                } else {
                    continueToWork();
                }

            }

        });

        getLoaderManager().initLoader(CLUB_ACTIVITY_LOADER, null, this);

        return view;
    }

    private void cancelWork() {
        myFab.setImageResource(android.R.drawable.ic_media_play);
        animation.end();
        progressBar.setVisibility(View.INVISIBLE);
        mCountDownWorkTimer.cancel();
        mCountDownShortBrakeTimer.cancel();
        mCountDownLongBrakeTimer.cancel();
        tvProgress.setText(R.string.progress_message);
        tvProgressMessage.setText("");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLUB_ACTIVITY_LOADER:
                return new CursorLoader(view.getContext(),
                        ContentUris.withAppendedId(ProjectManagementContract.ProjectIssue.CONTENT_URI, clubActivityId),
                        null, // NULL because we want every column
                        null,
                        null,
                        null
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateView(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateView(null);
    }


    private void updateView(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_TITLE)));
            tvShortNote.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SHORT_NOTE)));
            tvLongNote.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_LONG_NOTE)));
            tvDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_START_DATE)));
            serverIssueID = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectManagementContract.ProjectIssue.COLUMN_NAME_SERVER_ID));
        }

        if (cursor != null) {
            cursor.close();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mListener.onClubActivityDeleted(clubActivityId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startAnimation(long duration) {
        progressBar.setVisibility(View.VISIBLE);
        animation.end();
        animation.setDuration(duration); //in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void continueToWork() {
        myFab.setImageResource(android.R.drawable.checkbox_off_background);
        mCountDownWorkTimer.start();
        startAnimation(WORK_CYCLE_DURATION); //in milliseconds
        NotificationManager notificationManager = (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(16);
    }

    public void continueToBreak() {

        NotificationManager notificationManager = (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(15);

        pomodori++;

        if (pomodori < 4) {
            mCountDownShortBrakeTimer.start();
            startAnimation(SMALL_BREAK_DURATION);
        } else {
            greatPomodori++;
            saveData(greatPomodori);

            textView.setText("Great Job! You have completed "+ String.valueOf(greatPomodori*4) +" sessions in a row!");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(
                            view.getContext(), R.anim.fade_in_view);

                    final Animation fadeOutAnimation = AnimationUtils.loadAnimation(
                            view.getContext(), R.anim.fade_out_view);

                    textView.startAnimation(fadeInAnimation);
                    gifView.startAnimation(fadeInAnimation);

                    fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            textView.setVisibility(View.VISIBLE);
                            gifView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    textView.startAnimation(fadeOutAnimation);
                                    gifView.startAnimation(fadeOutAnimation);
                                }
                            }, 10000);


                        }
                    });

                    fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            textView.setVisibility(View.INVISIBLE);
                            gifView.setVisibility(View.INVISIBLE);

                            pomodori = 0;
                            mCountDownLongBrakeTimer.start();
                            startAnimation(LONG_BREAK_DURATION);


                        }
                    });
                }

            }, 500);


        }
    }

    private void insertIssueComment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Great job my friend!");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_issue_comment, (ViewGroup) getView(), false);
        // Set up the input
        final EditText inputComment = (EditText) viewInflated.findViewById(R.id.input_comment);
//        final EditText inputWorkHours = (EditText) viewInflated.findViewById(R.id.input_work_hours);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                comment = inputComment.getText().toString();
                workHours = 0.42;
                saveNewWorkLog();
                dialog.dismiss();
                continueToBreak();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                continueToBreak();
            }
        });

        builder.show();
    }

    private void proceedToWork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("That was a nice break! Do you want to continue on your issue?");

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                continueToWork();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                cancelWork();
            }
        });

        builder.show();
    }

    private void saveNewWorkLog() {
        //for test purposes, added as a default place
        WorkLogService.startCreateWorkLog(this.getContext(),
                serverIssueID,
                comment,
                workHours);

    }

    private void showEndWorkNotification(long clubActivityId) {
        PendingIntent contentIntent = PendingIntent.getActivity(this.getContext(), 0, ClubActivityDetailsActivity.getStartIntent(this.getContext(), clubActivityId).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this.getContext())
                .setSmallIcon(android.R.drawable.btn_star_big_on)
                .setTicker("Great job my friend! Comment your work!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Great job my friend!")
                .setContentText("Comment your work!")
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(15, notification);
    }

    private void showEndBreakNotification(long clubActivityId) {
        PendingIntent contentIntent = PendingIntent.getActivity(this.getContext(), 0, ClubActivityDetailsActivity.getStartIntent(this.getContext(), clubActivityId).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this.getContext())
                .setSmallIcon(android.R.drawable.btn_star_big_off)
                .setTicker("Time to get back to work!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("This was a good break!")
                .setContentText("Time to put some effort on your issues :)")
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(16, notification);
    }


    private void saveData(int newGreatPomodori){
        SharedPreferences sp = view.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sp.edit();
        mEditor.putInt("GreatPomodori"+String.valueOf(newGreatPomodori), 1).commit();
    }
}
