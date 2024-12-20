package com.example.shakyaapi;

import android.app.AlertDialog;
import android.os.Bundle;

import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.shakyaapi.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSearch.setOnClickListener(view -> {
            HttpUrl url = HttpUrl.parse("https://tools-api.italkutalk.com/java/lab12");
            Request request = new Request(url, "GET", Headers.of(), null, Map.of());
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200 || !response.isSuccessful() || response.body() == null) {
                        Log.e("fetch", response.code() + response.message());
                        return;
                    }

                    Data data = new Gson().fromJson(response.body().string(), Data.class);

                    String[] items = Arrays.stream(data.result.results)
                            .map(results -> "\n列車即將進入：" + results.Station + "\n列車行駛目的地：" + results.Destination)
                            .toArray(String[]::new);

                    runOnUiThread(() -> {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("臺北捷運列車到站站名")
                                .setItems(items, null)
                                .show();
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("fetch", e.getMessage());
                }
            });
        });
    }
}
