package com.yunus.remember.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.yunus.utils.LogUtil;
import com.yunus.remember.R;
import com.yunus.remember.entity.Book;

import org.litepal.crud.DataSupport;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private int resourceId;

    public BookAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Book>
            objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Book book = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bookName = view.findViewById(R.id.book_name);
            viewHolder.wordNum = view.findViewById(R.id.book_word_num);
            viewHolder.addBook = view.findViewById(R.id.book_add_book);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.bookName.setText(book.getName());
        viewHolder.wordNum.setText("包含单词 " + book.getWordNum() + " 个");
        int num = DataSupport.where("id = ?", "" + book.getId()).count(Book.class);
        LogUtil.d("aaa", num + "");
        LogUtil.d("bbb", book.getId() + "");
        if (num != 0) {
            viewHolder.addBook.setVisibility(View.GONE);
        }
        viewHolder.addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem(position).save();
                viewHolder.addBook.setVisibility(View.GONE);
            }
        });
        return view;
    }

    class ViewHolder {
        TextView bookName;
        TextView wordNum;
        Button addBook;
    }
}
