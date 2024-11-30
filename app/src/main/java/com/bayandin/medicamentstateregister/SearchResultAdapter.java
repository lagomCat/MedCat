package com.bayandin.medicamentstateregister;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private List<SearchResultItem> searchResultItems = new ArrayList<>();
    private OnSearchResultClickListener onSearchResultClickListener;


    public void setOnSearchResultClickListener(OnSearchResultClickListener onSearchResultClickListener) {
        this.onSearchResultClickListener = onSearchResultClickListener;
    }


    public void setSearchResults(List<SearchResultItem> searchResultItems) {
        this.searchResultItems = searchResultItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_search_result_item,
                parent,
                false
        );
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder viewHolder, int position) {
        SearchResultItem searchResultItem = searchResultItems.get(position);
        viewHolder.textViewSearchResult.setText(searchResultItem.getText());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSearchResultClickListener != null) {
                    onSearchResultClickListener.onSearchResultClick(searchResultItem);
                }
            }
        });

        viewHolder.itemView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { // Обрабатываем только момент касания
                hideKeyboard(v);
            }
            return false; // Возвращаем true, чтобы сигнализировать, что событие обработано
        });

    }

    @Override
    public int getItemCount() {
        return searchResultItems.size();
    }


    class SearchResultViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewSearchResult;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSearchResult = itemView.findViewById(R.id.textViewSearchResult);
        }
    }

    interface OnSearchResultClickListener {
        void onSearchResultClick(SearchResultItem searchResultItem);
    }

    //Метод для скрытия клавиатуры
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
