package com.beta.qrscanner;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        ListView contactsListView = findViewById(R.id.contacts_list);

        // Query contacts
        ArrayList<String> contactsList = getContacts();

        // Populate ListView with contacts
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
        contactsListView.setAdapter(adapter);

        // Set click listener for ListView items
        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected contact number
            String selectedContact = (String) parent.getItemAtPosition(position);
            String[] contactParts = selectedContact.split(": ");
            String selectedNumber = contactParts[1];

            // Pass the selected contact number back to MainActivity
            getIntent().putExtra("selectedNumber", selectedNumber);
            setResult(RESULT_OK, getIntent());
            finish();
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
}
