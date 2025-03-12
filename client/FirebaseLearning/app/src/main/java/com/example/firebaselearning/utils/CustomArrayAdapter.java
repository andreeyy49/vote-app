package com.example.firebaselearning.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.firebaselearning.dto.CityDto;
import com.example.firebaselearning.dto.CountryDto;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {

    private List<T> originalList;
    private List<T> filteredList;

    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.originalList = new ArrayList<>(objects);
        this.filteredList = new ArrayList<>(objects);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public T getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        T item = getItem(position);

        // Отображаем только тайтл
        if (item instanceof CountryDto) {
            textView.setText(((CountryDto) item).getTitle());
        } else if (item instanceof CityDto) {
            textView.setText(((CityDto) item).getTitle());
        }

        return textView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof CountryDto) {
                    return ((CountryDto) resultValue).getTitle();
                } else if (resultValue instanceof CityDto) {
                    return ((CityDto) resultValue).getTitle();
                }
                return super.convertResultToString(resultValue);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                // Если текст не введён или пуст
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalList;
                    results.count = originalList.size();
                } else {
                    List<T> filteredItems = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase().trim(); // Приводим текст к нижнему регистру для фильтрации

                    // Фильтрация: ищем совпадения, которые начинаются с введённого текста
                    for (T item : originalList) {
                        String title = "";
                        if (item instanceof CountryDto) {
                            title = ((CountryDto) item).getTitle().toLowerCase(); // Приводим к нижнему регистру только для фильтрации
                        } else if (item instanceof CityDto) {
                            title = ((CityDto) item).getTitle().toLowerCase(); // Приводим к нижнему регистру только для фильтрации
                        }

                        if (title.startsWith(filterPattern)) { // Проверка на начало строки
                            filteredItems.add(item);
                        }
                    }

                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    filteredList.clear();
                    filteredList.addAll((List<T>) results.values);
                    notifyDataSetChanged();  // Оповещаем адаптер о том, что данные обновились
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
