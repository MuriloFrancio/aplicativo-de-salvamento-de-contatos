package vr.univali.adicionar_contatos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // Import necessário para usar o Log
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditContactActivity extends AppCompatActivity {
    private EditText edtName, edtNumber;
    private LinearLayout phoneContainer;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        edtName = findViewById(R.id.edtName);
        phoneContainer = findViewById(R.id.phoneContainer);

        // Verificar se há um contato enviado para edição
        contact = (Contact) getIntent().getSerializableExtra("contact");
        if (contact != null) {
            // Modo de edição: Carregar os dados do contato existente
            edtName.setText(contact.getName());
            for (Phone phone : contact.getPhones()) {
                addPhoneField(phone.getNumber(), phone.getType());
            }
        } else {
            // Modo de adição: Criar um novo contato vazio
            contact = new Contact(0, "", new ArrayList<>());
            addPhoneField("", "Pessoal");
        }

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                saveContact();
            }
        });

        Button btnAddPhone = findViewById(R.id.btnAddPhone);
        btnAddPhone.setOnClickListener(v -> addPhoneField("", "Pessoal"));
    }

    private void addPhoneField(String number, String type) {
        View phoneView = LayoutInflater.from(this).inflate(R.layout.phone_item, phoneContainer, false);
        EditText edtNumber = phoneView.findViewById(R.id.edtNumber);
        Spinner spinnerType = phoneView.findViewById(R.id.spinnerType);

        // Configura o número e tipo no campo
        edtNumber.setText(number);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phone_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        if (type != null) {
            int position = adapter.getPosition(type);
            spinnerType.setSelection(position);  // Selecionar o tipo correto
        }

        // Remover o telefone ao clicar no botão "Remover"
        Button btnRemovePhone = phoneView.findViewById(R.id.btnRemovePhone);
        btnRemovePhone.setOnClickListener(v -> phoneContainer.removeView(phoneView));  // Remove da interface

        // Adiciona o campo ao container de telefones
        phoneContainer.addView(phoneView);
    }

    private boolean validateFields() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Nome é obrigatório");
            return false;
        }

        // Verifica se pelo menos um número foi adicionado
        for (int i = 0; i < phoneContainer.getChildCount(); i++) {
            View phoneView = phoneContainer.getChildAt(i);
            EditText edtNumber = phoneView.findViewById(R.id.edtNumber);

            if (edtNumber.getText().toString().trim().isEmpty()) {
                edtNumber.setError("Telefone é obrigatório");
                return false;
            }
        }

        return true;
    }



    private void saveContact() {
        String name = edtName.getText().toString();  // Coleta o nome
        List<Phone> phones = new ArrayList<>();

        // Coleta todos os números de telefone adicionados
        for (int i = 0; i < phoneContainer.getChildCount(); i++) {
            View phoneView = phoneContainer.getChildAt(i);
            EditText edtNumber = phoneView.findViewById(R.id.edtNumber);
            Spinner spinnerType = phoneView.findViewById(R.id.spinnerType);

            String number = edtNumber.getText().toString();
            String type = spinnerType.getSelectedItem() != null ? spinnerType.getSelectedItem().toString() : "Celular";

            if (!number.isEmpty()) {
                phones.add(new Phone(number, type));  // Adiciona o número à lista
            }
        }

        // Atualiza o nome e os telefones no contato
        contact.setName(name);
        contact.setPhones(phones);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        if (contact.getId() == 0) {
            // Se o ID do contato é 0, é um novo contato, então insere no banco de dados
            dbHelper.addContact(contact);
        } else {
            // Senão, é uma edição, então atualiza o contato existente
            dbHelper.updateContact(contact);
        }

        // Envia o contato atualizado para a MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("contact", contact);
        setResult(RESULT_OK, resultIntent);  // Retorna o resultado
        finish();
    }
}