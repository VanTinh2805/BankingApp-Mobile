package com.example.proiectmobilebanking.Chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proiectmobilebanking.LoginActivity;
import com.example.proiectmobilebanking.R;
import com.example.proiectmobilebanking.SharedPreferencesUser;
import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.TransitionResponse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {
    private SharedPreferencesUser preferences;
    private LinearLayout layout;
    private ApiService apiService;
    private double amountSent = 0;
    private double amountReceived = 0;
    private final DecimalFormat amountFormat = new DecimalFormat("#,##0.##", new DecimalFormatSymbols(Locale.US));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = new SharedPreferencesUser(getApplicationContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        layout = findViewById(R.id.chartLayout);
        loadChartData();
    }

    private void loadChartData() {
        String authorization = preferences.getAuthorizationHeader();
        if (authorization.isEmpty()) {
            goToLogin();
            return;
        }

        apiService.getCurrentTransitions(authorization).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    amountSent = sumAmount(response.body());
                    loadReceivedChartData(authorization);
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc thong ke giao dich gui", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void loadReceivedChartData(String authorization) {
        apiService.getReceivedTransitions(authorization).enqueue(new Callback<List<TransitionResponse>>() {
            @Override
            public void onResponse(Call<List<TransitionResponse>> call, Response<List<TransitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    amountReceived = sumAmount(response.body());
                    renderChart();
                } else if (response.code() == 401 || response.code() == 403) {
                    preferences.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Khong lay duoc thong ke giao dich nhan", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransitionResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private double sumAmount(List<TransitionResponse> transitions) {
        double total = 0;
        for (TransitionResponse transition : transitions) {
            if (transition.getAmount() != null) {
                total += transition.getAmount();
            }
        }
        return total;
    }

    private void renderChart() {
        layout.removeAllViews();
        if (amountSent <= 0 && amountReceived <= 0) {
            Toast.makeText(getApplicationContext(), "Chua co du lieu giao dich", Toast.LENGTH_LONG).show();
            return;
        }

        layout.addView(new Grafic(getApplicationContext(), calculateDegrees(amountSent, amountReceived), amountSent, amountReceived));
    }

    private float[] calculateDegrees(double sent, double received) {
        double sum = sent + received;
        if (sum <= 0) {
            return new float[]{0, 0};
        }
        return new float[]{
                (float) (360 * (sent / sum)),
                (float) (360 * (received / sum))
        };
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public class Grafic extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float[] degree;
        private final double sent;
        private final double received;
        private final int[] colors = {Color.MAGENTA, Color.YELLOW};
        private final RectF chartBounds = new RectF();
        private final RectF sentLegendBounds = new RectF();
        private final RectF receivedLegendBounds = new RectF();

        public Grafic(Context context, float[] values, double sent, double received) {
            super(context);
            this.degree = values;
            this.sent = sent;
            this.received = received;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int centerX = canvas.getWidth() / 2;
            int centerY = canvas.getHeight() / 2;
            int radius = Math.min(canvas.getWidth(), canvas.getHeight()) / 4;

            chartBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

            float startAngle = 0;
            for (int i = 0; i < degree.length; i++) {
                paint.setColor(colors[i]);
                canvas.drawArc(chartBounds, startAngle, degree[i], true, paint);
                startAngle += degree[i];
            }

            drawLegend(canvas, centerX - radius, centerY + radius + 60);
        }

        private void drawLegend(Canvas canvas, int left, int top) {
            paint.setTextSize(36);

            sentLegendBounds.set(left, top, left + 28, top + 28);
            paint.setColor(colors[0]);
            canvas.drawRect(sentLegendBounds, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("Money sent: " + amountFormat.format(sent), left + 45, top + 28, paint);

            receivedLegendBounds.set(left, top + 55, left + 28, top + 83);
            paint.setColor(colors[1]);
            canvas.drawRect(receivedLegendBounds, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("Money received: " + amountFormat.format(received), left + 45, top + 83, paint);
        }
    }
}
