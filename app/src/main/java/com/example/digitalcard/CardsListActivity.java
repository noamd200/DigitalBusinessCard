package com.example.digitalcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CardsListActivity extends AppCompatActivity {

    private ListView lvCards;
    private TextView tvCount;

    private ArrayList<Card> cards = new ArrayList<>();   // הכרטיסים בזיכרון
    private ArrayAdapter<Card> adapter;                  // מחבר בין הרשימה ל-ListView

    private DatabaseReference ref;                       // הצומת cards
    private ValueEventListener listener;                 // שומרים אותו כדי להסיר בסוף

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);
        setTitle("הכרטיסים שלי");

        lvCards = findViewById(R.id.lvCards);
        tvCount = findViewById(R.id.tvCount);

        // ➕ כרטיס חדש: סוגרים את הרשימה וחוזרים לטופס (הוא מתחת לרשימה, והוא כבר ריק)
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cards);
        lvCards.setAdapter(adapter);

        if (!FirebaseConfig.isConfigured()) {
            tvCount.setText("חסרים ערכים ב-FirebaseConfig.java");
            return;
        }
        ref = CardsDb.cards(this);

        // ===== קריאה (Read) =====
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // TODO 4: רוקנו את הרשימה cards (למה? ראו את הרמזים)
                cards.clear();

                // TODO 5: עברו בלולאה על כל הילדים של snapshot. לכל ילד:
                //         הפכו אותו לעצם Card, שמרו בעצם את המפתח של הילד,
                //         והוסיפו את העצם לרשימה cards
                for (DataSnapshot child : snapshot.getChildren()) {
                    Card card = child.getValue(Card.class);
                    if (card != null) {
                        card.setKey(child.getKey());
                        cards.add(card);
                    }
                }

                // TODO 6: אמרו למתאם שהנתונים השתנו,
                //         וכתבו ב-tvCount כמה כרטיסים יש
                adapter.notifyDataSetChanged();
                tvCount.setText("סה\"כ כרטיסים: " + cards.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardsListActivity.this,
                        "הקריאה נכשלה: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // TODO 7: הירשמו עם listener לשינויים בצומת ref
        ref.addValueEventListener(listener);

        // שלב 5: לחיצה רגילה (פתיחת כרטיס) — נוסיף כאן

        // שלב 7: לחיצה ארוכה (מחיקה) — נוסיף כאן
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO 8: אם ref ו-listener לא null, בטלו את ההרשמה של listener
        if (ref != null && listener != null) {
            ref.removeEventListener(listener);
        }
    }
}