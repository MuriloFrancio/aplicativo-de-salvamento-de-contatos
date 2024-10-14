package vr.univali.adicionar_contatos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactList, this);
        recyclerView.setAdapter(contactAdapter);

        loadContacts(); // Carrega os contatos inicialmente

        // Configurando o OnClickListener para o botão "Adicionar Contato"
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            openEditContact(null); // Chama o método para abrir a tela de adicionar um contato
        });
    }


    private void openEditContact(Contact contact) {
        Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
        if (contact != null) {
            intent.putExtra("contact", contact);  // Enviar o contato para edição
        }
        startActivityForResult(intent, 1);
    }

    // Método onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            loadContacts();
        }
    }

    private void loadContacts() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        contactList.clear(); // Limpa a lista existente

        List<Contact> contacts = dbHelper.getAllContacts(); // Carrega os contatos do banco de dados
        contactList.addAll(contacts); // Adiciona os novos contatos à lista
        contactAdapter.notifyDataSetChanged(); // Notifica o adaptador
    }

}