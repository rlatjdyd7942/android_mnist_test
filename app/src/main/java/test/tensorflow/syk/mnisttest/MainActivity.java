package test.tensorflow.syk.mnisttest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {
    private MonoPaintView view = null;

    static {
        System.loadLibrary("tensorflow_inference");
    }

    private static final String MODEL_FILE = "file:///android_asset/frozen_tfdroid.pb";
    private static final String INPUT_IMAGE = "INPUT_IMAGE";
    private static final String INPUT_TRAINING = "INPUT_TRAINING";
    private static final String OUTPUT_ONEHOT = "OUTPUT_ONEHOT";

    private static final long[] INPUT_SIZE = {1,1};

    private TensorFlowInferenceInterface inferenceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout)findViewById(R.id.main_layout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = displayMetrics.widthPixels;

        view = new MonoPaintView(this, size, size);
        layout.addView(view);

        Button b = new Button(this);
        b.setText("Clear");
        b.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final MonoPaintView v = view;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.clearBitmap();
            }
        });
        layout.addView(b);

        TextView textView = new TextView(this);
        textView.setText("Prediction : " + -1 + ", Secondary : " + -1);
        final TextView tv = textView;

        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);
        float[] inputFloats = {1.0f};
        //inferenceInterface.feed(INPUT_TRAINING, inputFloats, INPUT_SIZE);

        b = new Button(this);
        b.setText("Run");
        b.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float[] inputFloats = {1.0f};
                inferenceInterface.feed(INPUT_TRAINING, inputFloats, INPUT_SIZE);
                inferenceInterface.feed(INPUT_IMAGE, v.getPixelArray(), new long[] {1, MonoPaintView.BITMAP_LENGTH} );
                inferenceInterface.run(new String[] {OUTPUT_ONEHOT});
                //float[] result = new float[10];
                float[] result = new float[10];
                inferenceInterface.fetch(OUTPUT_ONEHOT, result);
                int maxIndex = 0;
                int secIndex = 0;
                for (int i = 0; i < 10; ++i) {
                    if (result[i] > result[maxIndex]) {
                        secIndex = maxIndex;
                        maxIndex = i;
                    }
                }
                if (result[maxIndex] < 0.f) {
                    maxIndex = -1;
                    secIndex = -1;
                }
                tv.setText("Prediction : " + maxIndex + ", Secondary : " + secIndex);
            }
        });
        layout.addView(b);
        layout.addView(textView);

    }

}
