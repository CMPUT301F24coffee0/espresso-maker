package com.example.espresso.db;

import com.google.firebase.firestore.DocumentSnapshot;

public interface DocumentSupplier {
    void run(DocumentSnapshot doc);
}
