package com.example.dashnotes;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Random;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView, minus, noteSize, plus;
    String title, content, docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;
    private float mTextSize = 18;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        minus = findViewById(R.id.minus);
        noteSize = findViewById(R.id.note_size);
        plus = findViewById(R.id.plus);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if (isEditMode) {
            pageTitleTextView.setText(getResources().getString(R.string.edit));
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }
        saveNoteBtn.setOnClickListener((v)-> saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());

        minus.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mTextSize > 13) {
                    mTextSize -= 2;
                    contentEditText.setTextSize(mTextSize);
                    noteSize.setText(String.format("%.0f", mTextSize));
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mTextSize < 53) {
                    mTextSize += 2;
                    contentEditText.setTextSize(mTextSize);
                    noteSize.setText(String.format("%.0f", mTextSize));
                }
            }
        });
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if (noteTitle.isEmpty()) {
            titleEditText.setError(getResources().getString(R.string.title_error));
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        Random random = new Random();
        note.setStylecode(random.nextInt(5));
        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else {
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetailsActivity.this,
                            getResources().getString(R.string.create_success));
                    finish();
                }
                else {
                    Utility.showToast(NoteDetailsActivity.this,
                            getResources().getString(R.string.create_error));
                }
            }
        });
    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetailsActivity.this,
                            getResources().getString(R.string.del_success));
                    finish();
                }
                else {
                    Utility.showToast(NoteDetailsActivity.this,
                            getResources().getString(R.string.del_error));
                }
            }
        });
    }
}