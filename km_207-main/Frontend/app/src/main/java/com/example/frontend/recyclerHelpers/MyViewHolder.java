package com.example.frontend.recyclerHelpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.AllChatsActivity;
import com.example.frontend.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * This class holds references to the UI elements for each item in the list.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView user_name_text, user_email_text, major_text;
    Context context;

    /**
     * Constructor for MyViewHolder.
     *
     * @param itemView
     */
    public MyViewHolder(@NonNull View itemView, List<Item> items, Context context) {
        super(itemView);
        this.context = context;

//        user_name_text = itemView.findViewById(R.id.user_name_text);
        user_email_text = itemView.findViewById(R.id.user_email_text);
//        major_text = itemView.findViewById(R.id.major_text);

        user_email_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Item clickedItem = items.get(position);
                    // Handle the click event for the item (e.g., open a chat with the selected friend)
                    // You can call a method from your activity or perform any action you need.

                    // Create and show a chat popup dialog
                    showChatPopupDialog(clickedItem.getEmail());
                }
            }
        });
    }


    // Create a method to show the chat popup dialog
    private void showChatPopupDialog(String userEmail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chat with " + userEmail);
        builder.setMessage("Add your chat UI components here."); // Customize your chat UI
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



}
