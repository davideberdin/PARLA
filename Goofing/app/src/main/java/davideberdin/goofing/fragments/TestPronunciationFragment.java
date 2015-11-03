package davideberdin.goofing.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import davideberdin.goofing.FeedbacksActivity;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.Recorder;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.UserLocalStore;


public class TestPronunciationFragment extends Fragment implements View.OnClickListener
{
    //region VARIABLES
    private View testPronunciationView = null;
    private FloatingActionButton startButton = null;
    private ImageButton listenSentence = null;

    // variables related to the view
    private TextView tvSentence = null;
    private TextView tvPhonemes = null;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;
    //endregion

    public TestPronunciationFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.testPronunciationView = inflater.inflate(R.layout.test_pronunciation_layout, container, false);

        this.startButton = (FloatingActionButton) testPronunciationView.findViewById(R.id.fabStartRecording);
        this.listenSentence = (ImageButton) testPronunciationView.findViewById(R.id.listenSentence);

        // register listener
        this.startButton.setOnClickListener(this);
        this.listenSentence.setOnClickListener(this);

        this.userLocalStore = new UserLocalStore(this.getActivity());
        this.loggedUser = this.userLocalStore.getLoggedUser();

        this.tvSentence = (TextView) testPronunciationView.findViewById(R.id.tpSentence);
        String tmp = this.loggedUser.GetCurrentSentence();
        this.tvSentence.setText(tmp);

        // create phoneme sentence
        this.tvPhonemes = (TextView) testPronunciationView.findViewById(R.id.tpPhoneticSentence);
        tmp = this.loggedUser.GetCurrentPhonetic();
        this.tvPhonemes.setText("/ " + tmp + " /");

        return this.testPronunciationView;
    }

    @Override
    public void onClick(View v)
    {
        ServerRequest recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");

        switch (v.getId())
        {
            case R.id.fabStartRecording:
                Recorder recorder = new Recorder(this.getView());
                recordingRequest.recordingAudioInBackground(recorder, new GetCallback()
                {
                    @Override
                    public void done(Object... params)
                    {
                        try
                        {
                            // dismiss
                            // and send it to server
                            // ...

                            // obtained as result
                            Object fileAudio = null; // params[0];      // TODO: put the audiofile here
                            String currentSentence = loggedUser.GetCurrentSentence();

                            InputStream inStream = testPronunciationView.getContext().getResources().openRawResource(R.raw.test_audio); // TODO: edit this line
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            BufferedInputStream in = new BufferedInputStream(inStream);

                            int read;
                            byte[] buff = new byte[1024];
                            while ((read = in.read(buff)) > 0) {
                                out.write(buff, 0, read);
                            }
                            out.flush();
                            byte[] fileAudioByte = out.toByteArray();

                            ServerRequest request = new ServerRequest(getActivity() , "Analyzing audio", "Please wait...");
                            request.sendRecordedAudioToServer(loggedUser, fileAudioByte, currentSentence, new GetCallback()
                            {
                                @Override
                                public void done(Object... params) {

                                    byte[] resultImage = (byte[]) params[0];
                                    Intent intent = new Intent(getActivity(), FeedbacksActivity.class);
                                    intent.putExtra("vowelchart", resultImage);

                                    startActivity(intent);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.listenSentence:
                // play audio
                try {
                    // TODO: mapping between R.raw.xxx to sentence
                    int resID = getActivity().getResources().getIdentifier("test_audio", "raw", getActivity().getPackageName());

                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), resID);
                    mediaPlayer.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
