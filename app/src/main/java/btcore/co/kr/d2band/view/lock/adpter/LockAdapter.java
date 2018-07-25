package btcore.co.kr.d2band.view.lock.adpter;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import btcore.co.kr.d2band.R;

public class LockAdapter extends PagerAdapter {

    private int[] images = {R.drawable.tutorial_01, R.drawable.tutorial_02, R.drawable.tutorial_03,
            R.drawable.tutorial_04, R.drawable.tutorial_05, R.drawable.tutorial_06};
    private Context context;

    private final int REQUEST_WIDTH = 1024;
    private final int REQUEST_HEIGHT = 1024;


    public LockAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.slider_lock, container, false);
        ImageView imageView = v.findViewById(R.id.image_lock);
        TextView textView = v.findViewById(R.id.text_num);
        TextView textLock = v.findViewById(R.id.text_use);

        Glide.with(context).load(images[position]).into(imageView);

        String text = (position + 1) + " / 6";

        if(position == 5) textLock.setVisibility(View.VISIBLE);
        textLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                context.getApplicationContext().startActivity(intent);
                }
        });
        textView.setText(text);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }




}

