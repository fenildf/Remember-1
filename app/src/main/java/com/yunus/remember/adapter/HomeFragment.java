package com.yunus.remember.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yunus.utils.LogUtil;
import com.yunus.remember.R;
import com.yunus.remember.activity.home.TestActivity;
import com.yunus.remember.activity.mine.BooksActivity;
import com.yunus.remember.activity.mine.ProgressActivity;
import com.yunus.remember.entity.Book;
import com.yunus.remember.entity.TodayWord;
import com.yunus.remember.entity.Word;
import com.yunus.remember.utils.StorageUtil;

import org.litepal.crud.DataSupport;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by yun on 2018/3/29.
 */

public class HomeFragment extends Fragment {

    TextView doneDay;
    TextView newNum;
    TextView todayNum;
    TextView remainNum;
    TextView mineNum;
    TextView btMineNum;
    Button startStudy;
    LinearLayout numLayout;
    LinearLayout welcomeLayout;
    LinearLayout commonLayout;
    TextView addBook;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    String today = df.format(new Date(System.currentTimeMillis()));


    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_pager_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        doneDay = getActivity().findViewById(R.id.done_day);
        newNum = getActivity().findViewById(R.id.home_num_new);
        todayNum = getActivity().findViewById(R.id.home_num_today);
        remainNum = getActivity().findViewById(R.id.home_num_remain);
        mineNum = getActivity().findViewById(R.id.home_num_mine);
        btMineNum = getActivity().findViewById(R.id.home_bt_mine);
        startStudy = getActivity().findViewById(R.id.start_study);
        numLayout = getActivity().findViewById(R.id.home_num_layout);
        welcomeLayout = getActivity().findViewById(R.id.home_welcome_layout);
        commonLayout = getActivity().findViewById(R.id.home_common_layout);
        addBook = getActivity().findViewById(R.id.home_add_book);
        pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        initView();

        startStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
            }
        });

        btMineNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BooksActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initText();
        if (!pref.getString(StorageUtil.TODAY_DATE, " ").equals(today)) {
            //联网，生成今日单词
            DataSupport.deleteAll(TodayWord.class);
            List<Word> words = DataSupport.findAll(Word.class);
            LogUtil.d("BBBBBBBBB", words.size() + "");
            TodayWord todayWord;
            for (Word word : words) {
                todayWord = new TodayWord(word.getSpell(), word.getMean(), word.getPhonogram(), word.getSentence(), 1);
                todayWord.save();
            }
            editor = pref.edit();
            editor.putString(StorageUtil.TODAY_DATE, today);
            editor.putInt(StorageUtil.STUDY_TIME, 0);
            editor.apply();
        }
    }

    private void initView() {
        if (!pref.getString(StorageUtil.TODAY_DATE, " ").equals(today)) {
            List<Book> books = DataSupport.findAll(Book.class);
            if (books.isEmpty()) {
                commonLayout.setVisibility(View.GONE);
                startStudy.setVisibility(View.GONE);
                numLayout.setVisibility(View.INVISIBLE);
                welcomeLayout.setVisibility(View.VISIBLE);
            } else {
                List<Word> words = DataSupport.select("spell").where("importance > ?", "3").find(Word.class);
                editor = pref.edit();
                if (words.size() > pref.getInt(StorageUtil.TODAY_NEW_NUM, 0)) {
                    editor.putInt(StorageUtil.TODAY_REAL_NEW_NUM, 0);
                } else {
                    editor.putInt(StorageUtil.TODAY_REAL_NEW_NUM, pref.getInt(StorageUtil.TODAY_NEW_NUM, 0) - words.size());
                }
                editor.apply();
            }
        }
    }


    private void initText() {
        doneDay.setText(String.valueOf(pref.getInt(StorageUtil.REGISTER_DAY, 0)));
        newNum.setText(String.valueOf(pref.getInt(StorageUtil.TODAY_REAL_NEW_NUM, 0)));
        todayNum.setText(String.valueOf(pref.getInt(StorageUtil.TODAY_NUM, 0)));
        remainNum.setText(String.valueOf(pref.getInt(StorageUtil.TODAY_REMAIN_NUM, 0)));
        mineNum.setText(String.valueOf(pref.getInt(StorageUtil.WORDS_NUM, 0)));
    }
}