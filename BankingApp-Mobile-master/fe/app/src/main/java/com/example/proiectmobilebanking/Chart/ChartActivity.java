package com.example.proiectmobilebanking.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import com.example.proiectmobilebanking.SharedPreferencesUser;
import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.proiectmobilebanking.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {
private SharedPreferencesUser preferences;
Integer amountSend=0;
Integer amountAsk=0;
LinearLayout layout;
Integer values[]=new Integer[2];
float valuesFloat[]=new float[2];
private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences=new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadChartData();

    }

    private void loadChartData() {
        apiService.getCurrentTransitions(preferences.getAuthorizationHeader()).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for(TransitionResponse transition:response.body()){
                        amountSend += transition.getAmount().intValue();
                    }
                    values[0]=amountSend;
                    valuesFloat[0]=(float)amountSend;
                    values[1]=amountAsk;
                    valuesFloat[1]=(float)amountAsk;
                    valuesFloat=calculate(valuesFloat);
                    layout=findViewById(R.id.chartLayout);
                    if(valuesFloat[0]!=0 || valuesFloat[1]!=0)
                    {layout.addView(new Grafic(getApplicationContext(),valuesFloat));}
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
            }
        });
    }
    private float[] calculate(float[] vector){
        float sum=0;
        for(int i=0;i<vector.length;i++){
            sum=sum+vector[i];
        }
        if (sum == 0) {
            return vector;
        }
        for(int i=0;i<vector.length;i++){
            vector[i]=360*(vector[i]/sum);
        }
        return vector;
    }

    public class Grafic extends View{
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] degree;
        private int[] colors={Color.MAGENTA,Color.YELLOW};
        private Paint paint2=new Paint(Paint.ANTI_ALIAS_FLAG);
      RectF rectf=new RectF();
      RectF rectfRect=new RectF();
      RectF recffRect2=new RectF();
        int temp=0;
        public Grafic(Context context,float[] values){
            super(context);
            degree=new float[values.length];
            for(int i=0;i<values.length;i++){
                degree[i]=valuesFloat[i];
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int height=canvas.getHeight()/2;
            int width=canvas.getWidth()/2;
            int heightRect=300+(int)0.3*canvas.getHeight();
            int widthRect=300+(int)0.3*canvas.getWidth();
            int heightRect2=350+(int)0.3*canvas.getHeight();
            int widthRect2=350+(int)0.3*canvas.getWidth();
            rectfRect.set(widthRect-10,heightRect-10,widthRect+10,heightRect+10);
            recffRect2.set(widthRect-10,heightRect-10,widthRect+10,heightRect+10);
            rectf.set(width-100,height-100,width+100,height+100);
            rectf.inset(-200,-200);
            rectfRect.inset(-8,-8);
            recffRect2.offsetTo(widthRect-10,heightRect+40);
            recffRect2.inset(-8,-8);

            for(int i=0;i<degree.length;i++){
                if(i==0){
                    paint.setColor(colors[i]);
                    canvas.drawRect(rectfRect,paint);
                    canvas.drawArc(rectf,0,degree[i],true,paint);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(40);
                    canvas.drawText("Money sent",widthRect+40,widthRect+10,paint);
                    paint2.setColor(colors[i+1]);
                    canvas.drawRect(recffRect2,paint2);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(40);
                    canvas.drawText("Money asked",widthRect+40,widthRect+60,paint);
                }
                else{
                    temp+=(int)degree[i-1];
                    paint.setColor(colors[i]);
                    canvas.drawArc(rectf,temp,degree[i],true,paint);


                }
            }
        }
    }

}
