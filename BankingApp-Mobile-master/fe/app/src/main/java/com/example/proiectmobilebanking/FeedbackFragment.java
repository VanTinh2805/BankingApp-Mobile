package com.example.proiectmobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proiectmobilebanking.network.RetrofitClient;
import com.example.proiectmobilebanking.network.api.ApiService;
import com.example.proiectmobilebanking.network.model.FeedbackRequest;
import com.example.proiectmobilebanking.network.model.FeedbackResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends Fragment {
    Button btnFeedback;
    EditText textFb;
    RatingBar rating;
    ListView lvSuggestions;
    SharedPreferencesUser preferencesUser;
    private ApiService apiService;
    private ArrayAdapter<FeedbackResponse> adapter;
    private List<FeedbackResponse> suggestions = new ArrayList<>();

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        preferencesUser = new SharedPreferencesUser(requireContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        initComponents(view);
        loadFeedback();
        return view;
    }

    private void initComponents(View view) {
        btnFeedback = view.findViewById(R.id.btnSendFeedback);
        textFb = view.findViewById(R.id.etNumeFeedback);
        rating = view.findViewById(R.id.ratingBar);
        lvSuggestions = view.findViewById(R.id.lvSuggestion);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, suggestions);
        lvSuggestions.setAdapter(adapter);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid()) {
                    createFeedback();
                }
            }
        });

        lvSuggestions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFeedback(suggestions.get(position));
                return true;
            }
        });
    }

    private void loadFeedback() {
        String authorization = preferencesUser.getAuthorizationHeader();
        if (TextUtils.isEmpty(authorization)) {
            goToLogin();
            return;
        }

        apiService.getCurrentFeedback(authorization).enqueue(new Callback<List<FeedbackResponse>>() {
            @Override
            public void onResponse(Call<List<FeedbackResponse>> call, Response<List<FeedbackResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.clear();
                    suggestions.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401 || response.code() == 403) {
                    preferencesUser.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getContext(), "Khong lay duoc feedback", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<FeedbackResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void createFeedback() {
        String authorization = preferencesUser.getAuthorizationHeader();
        if (TextUtils.isEmpty(authorization)) {
            goToLogin();
            return;
        }

        btnFeedback.setEnabled(false);
        FeedbackRequest request = new FeedbackRequest(textFb.getText().toString().trim(), Math.round(rating.getRating()));
        apiService.createFeedback(authorization, request).enqueue(new Callback<FeedbackResponse>() {
            @Override
            public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {
                btnFeedback.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    textFb.setText("");
                    suggestions.add(0, response.body());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), R.string.feedback_send, Toast.LENGTH_LONG).show();
                } else if (response.code() == 401 || response.code() == 403) {
                    preferencesUser.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getContext(), "Khong gui duoc feedback", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FeedbackResponse> call, Throwable t) {
                btnFeedback.setEnabled(true);
                Toast.makeText(getContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void deleteFeedback(final FeedbackResponse feedback) {
        if (feedback == null || feedback.getId() == null) {
            return;
        }

        String authorization = preferencesUser.getAuthorizationHeader();
        if (TextUtils.isEmpty(authorization)) {
            goToLogin();
            return;
        }

        apiService.deleteFeedback(authorization, feedback.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    suggestions.remove(feedback);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), getString(R.string.suggestion_deleted), Toast.LENGTH_LONG).show();
                } else if (response.code() == 401 || response.code() == 403) {
                    preferencesUser.clearSession();
                    goToLogin();
                } else {
                    Toast.makeText(getContext(), "Khong xoa duoc feedback", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Khong ket noi duoc toi Server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public boolean valid() {
        if (textFb.getText() == null || TextUtils.isEmpty(textFb.getText().toString().trim())) {
            textFb.setError(getString(R.string.invalid_email));
            textFb.requestFocus();
            return false;
        }
        return true;
    }

    private void goToLogin() {
        if (getContext() == null) {
            return;
        }
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
