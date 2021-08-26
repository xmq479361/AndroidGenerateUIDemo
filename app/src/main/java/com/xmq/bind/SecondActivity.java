package com.xmq.bind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xmq.xbind.annotation.EventType;
import com.xmq.xbind.annotation.XBindEvent;
import com.xmq.xbind.annotation.XBindLayout;
import com.xmq.xbind.annotation.XBindView;
import com.xmq.xbind.core.XBinder;

/**
 * @author xmqyeah
 */
@XBindLayout(R.layout.activity_second)
public class SecondActivity extends AppCompatActivity {
    @XBindView(R.id.second_text)
    TextView second_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XBinder.bind(this);
        new _SecondActivity$$IXBinder().xbind(this);
//        XBindSecondActivity.bindView(this);
//        XBindSecondActivity.bindEvent(this);
        second_text.setText("text from bind View");
    }
    @XBindEvent(value = R.id.second_text, type = EventType.Click)
    public void onTextClick(View view) {
        Toast.makeText(this, "onTextClick", Toast.LENGTH_SHORT).show();
    }
}