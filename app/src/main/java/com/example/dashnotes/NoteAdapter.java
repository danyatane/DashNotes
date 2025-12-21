package com.example.dashnotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class NoteAdapter extends
        FirestoreRecyclerAdapter<com.example.dashnotes.Note, NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<com.example.dashnotes.Note> options,
                       Context context) {
        super(options);
        this.context = context;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView,contentTextView,timestampTextView;
        LinearLayout lr;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            lr = itemView.findViewById(R.id.note_item_layout);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_note_item,parent,false
        );
        return new NoteViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder,
                                    int position,
                                    @NonNull com.example.dashnotes.Note note) {
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);
        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));
        switch (note.stylecode) {
            case 0:
                holder.lr.setBackgroundResource(R.drawable.pink_gradient);
                break;
            case 1:
                holder.lr.setBackgroundResource(R.drawable.blue_gradient);
                break;
            case 2:
                holder.lr.setBackgroundResource(R.drawable.orange_gradient);
                break;
            case 3:
                holder.lr.setBackgroundResource(R.drawable.green_gradient);
                break;
            case 4:
                holder.lr.setBackgroundResource(R.drawable.red_gradient);
                break;
            default:
                holder.lr.setBackgroundResource(R.drawable.pink_gradient);
                break;
        }

        holder.itemView.setOnClickListener((v)-> {
            Intent intent = new Intent(context, NoteDetailsActivity.class);
            intent.putExtra("title",note.title);
            intent.putExtra("content",note.content);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener((v)-> {
            PopupMenu popupMenu = new PopupMenu(context, holder.contentTextView);
            popupMenu.getMenu().add(context.getResources().getString(R.string.share));
            popupMenu.getMenu().add(context.getResources().getString(R.string.del));
            popupMenu.show();
            String docId = this.getSnapshots().getSnapshot(position).getId();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle() == context.getResources().getString(R.string.share)) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        String noteText = holder.titleTextView.getText() + "\n" +
                                holder.contentTextView.getText();
                        sendIntent.putExtra(Intent.EXTRA_TEXT, noteText);
                        context.startActivity(sendIntent);
                    }
                    else if (item.getTitle() == context.getResources().getString(R.string.del)) {
                        DocumentReference documentReference;
                        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
                        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Utility.showToast(context,
                                            context.getResources().getString(R.string.del_success));
                                }
                                else {
                                    Utility.showToast(context,
                                            context.getResources().getString(R.string.del_error));
                                }
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
            return false;
        });
    }
}