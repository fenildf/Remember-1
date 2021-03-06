package com.yunus.remember.activity.chief;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.yunus.activity.BaseActivity;
import com.example.yunus.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunus.remember.R;
import com.yunus.remember.adapter.SearchWordAdapter;
import com.yunus.remember.entity.Word;
import com.yunus.remember.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends BaseActivity {

    private ListView listView;
    private List<Word> wordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        final SearchView searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.search_list_view);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView.onActionViewExpanded();
        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                wordList.clear();
                getWords(newText);
                LogUtil.d("before", wordList.toString());
                return true;
            }
        });
    }

    private void setAdapter(List<Word> list) {
        SearchWordAdapter adapter = new SearchWordAdapter(SearchActivity.this,
                R.layout.item_search_word, list);
        listView.setAdapter(adapter);
    }

    private void getWords(String data) {
        if (!data.isEmpty()) {
            HttpUtil.searchWord(data, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Gson gson = new Gson();
                    wordList = gson.fromJson(result, new
                            TypeToken<List<Word>>() {
                            }.getType());
                    LogUtil.d("after", result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (wordList.isEmpty()) {
                            } else {
                                setAdapter(wordList);
                            }
                        }
                    });
                }
            });
        }
    }
}
