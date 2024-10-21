package com.example.frontend.recyclerHelpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;

import java.util.List;

/**
 * Adapter class for RecyclerView.
 * This class binds data from a list of Items to the UI elements in MyViewHolder.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Item> items;

    /**
     * Constructor for MyAdapter.
     * @param context
     * @param items
     */
    public MyAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * Called by the RecyclerView to create a new ViewHolder for a list item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false), items, context);
    }

    /**
     * Called by the RecyclerView to update the ViewHolder to represent the contents of the
     * item at the given position in the data set.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.user_name_text.setText(items.get(position).getName());
        holder.user_email_text.setText(items.get(position).getEmail());
//        holder.major_text.setText(items.get(position).getMajor());
    }

    /**
     * Returns the number of Items in the RecyclerView.
     * @return number of Items in the RecyclerView
     */
    @Override
    public int getItemCount() {
        return items.size();
    }
}
