package com.example.digitalcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
    private ValueEventListener listener;

    Toolbar toolbar;

    // שומרים אותו כדי להסיר בסוף

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);
        setTitle("Saved cards");

        lvCards = findViewById(R.id.lvCards);
        tvCount = findViewById(R.id.tvCount);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ➕ כרטיס חדש: סוגרים את הרשימה וחוזרים לטופס (הוא מתחת לרשימה, והוא כבר ריק)
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Card c = cards.get(position);

                Intent intent = new Intent(CardsListActivity.this, ThirdActivity.class);
                intent.putExtra("KEY", c.getKey());
                intent.putExtra("NAME", c.getName());
                intent.putExtra("PROF", c.getJob());
                intent.putExtra("PHONE", c.getPhone());
                intent.putExtra("WEBSITE", c.getSite());
                startActivity(intent);
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cards);
        lvCards.setAdapter(adapter);

        if (!FirebaseConfig.isConfigured()) {
            tvCount.setText("Missing values in Firebase.config");
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
                tvCount.setText("Total cards: " + cards.size());
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

        lvCards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                askDelete(cards.get(position));
                return true; // Consumes the event so onItemClick does not also fire
            }
        });
    }


    private void askDelete(final Card c) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Card")
                .setMessage("Delete " + c.getName() + "'s card?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ref != null && c.getKey() != null) {
                            ref.child(c.getKey()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(CardsListActivity.this, "Deleted card", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_return).setVisible(true);
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.action_list).setVisible(false);

        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_call).setVisible(false);
        menu.findItem(R.id.action_site).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_return) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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