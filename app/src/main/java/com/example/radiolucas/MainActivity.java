package com.example.radiolucas;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private boolean isImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                if (i == 0) {
                    imageView.setImageResource(R.drawable.imgtest1);
                    i = i + 1;
                } else if (i == 1) {
                    imageView.setImageResource(R.drawable.imgtest2);
                    i = i + 1;
                } else if (i == 2) {
                    imageView.setImageResource(R.drawable.imgtest3);
                    i = i + 1;
                } else if (i == 3) {
                    imageView.setImageResource(R.drawable.imgtest4);
                    i = i + 1;
                }
            }
        });
    }
    @Override
    public void onStart()  {super.onStart();}

    @Override
    public void onRestart(){super.onRestart();}

    @Override
    public void onPause()  {super.onPause();}

    @Override
    public void onResume() {super.onResume();}

    @Override
    public void onStop() {super.onStop();}

    @Override
    public void onDestroy() {super.onDestroy();}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}