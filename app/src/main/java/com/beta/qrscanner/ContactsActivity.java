package com.beta.qrscanner;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private ArrayList<String> contactsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        ListView contactsListView = findViewById(R.id.contacts_list);
        EditText searchEditText = findViewById(R.id.search_edit_text);

        // Initialize contacts list
        contactsList = getContacts();

        // Initialize adapter with all contacts
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
        contactsListView.setAdapter(adapter);

        // Set click listener for ListView items
        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedContact = (String) parent.getItemAtPosition(position);
            String[] contactParts = selectedContact.split(": ");
            String selectedNumber = contactParts[1];

            getIntent().putExtra("selectedNumber", selectedNumber);
            setResult(RESULT_OK, getIntent());
            finish();
        });

        // Add text change listener to the search bar
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterContacts(s.toString());
            }
        });
    }

    private ArrayList<String> getContacts() {
        ArrayList<String> contactsList = new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String contactName = (nameIndex >= 0) ? cursor.getString(nameIndex) : "Unknown";
                String contactNumber = (numberIndex >= 0) ? cursor.getString(numberIndex) : "Unknown";
                contactsList.add(contactName + ": " + contactNumber);
            }
            cursor.close();
        }

        return contactsList;
    }

    private void filterContacts(String query) {
        ArrayList<String> filteredContacts = new ArrayList<>();

        for (String contact : contactsList) {
            if (contact.toLowerCase().contains(query.toLowerCase())) {
                filteredContacts.add(contact);
            }
        }

        adapter.clear();
        adapter.addAll(filteredContacts);
        adapter.notifyDataSetChanged();
    }
}
