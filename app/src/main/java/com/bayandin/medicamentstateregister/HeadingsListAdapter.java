package com.bayandin.medicamentstateregister;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HeadingsListAdapter extends RecyclerView.Adapter<HeadingsListAdapter.HeadingsViewHolder> {
    private ArrayList<ColumnHeadingItem> columnHeadingItems = new ArrayList<>();
    private OnHeadingsClickListener onHeadingsClickListener;

    public void setOnHeadingsClickListener(OnHeadingsClickListener onHeadingsClickListener) {
        this.onHeadingsClickListener = onHeadingsClickListener;
    }

    public void setHeadingTables(ArrayList<ColumnHeadingItem> columnHeadingItems) {
        this.columnHeadingItems = columnHeadingItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HeadingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_heading_item,
                parent,
                false
        );
        return new HeadingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HeadingsViewHolder viewHolder, int position) {
        ColumnHeadingItem columnHeadingItem = columnHeadingItems.get(position);
        viewHolder.textViewHeading.setText(columnHeadingItem.getTextHeading());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            //Слушатель клика для каждого созданного view
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (onHeadingsClickListener != null) {
                    onHeadingsClickListener.onHeadingClick(columnHeadingItem);
                    //Удаляем все заголовки из коллекции, кроме того, по которому клик
                    //Важно: здесь сравнивается не содержимое объектов, а ссылки на них.
                    // Если нужно сравнивать по содержимому, то в классе объекта нужно переопередить
                    //методы equals() и hashCode(). Это позволит использовать такие методы, как
                    columnHeadingItems.removeIf(item -> item != columnHeadingItem);
                    //Toast.makeText(v.getContext(), sqlId, Toast.LENGTH_LONG).show();
                    // Обновляем RecyclerView
                     notifyDataSetChanged();
                     //далее нужен метод, который заполяент коллекцию SearchResultItem найденными результатами поиска в зависимости от строки в поиске
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return columnHeadingItems.size();
    }

    class HeadingsViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewHeading;

        public HeadingsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeading = itemView.findViewById(R.id.textViewHeadingItem);
        }
    }

    interface OnHeadingsClickListener {
        void onHeadingClick(ColumnHeadingItem columnHeadingItem);
    }
}
