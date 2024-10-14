package vr.univali.adicionar_contatos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> contacts; // Lista de contatos
    private Context context; // Contexto da atividade

    // Construtor do adapter
    public ContactAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts; // Inicializa a lista de contatos
        this.context = context; // Inicializa o contexto
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item de contato
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        // Inicializa o display com o nome do contato
        String displayText = contact.getName();

        // Verifica se o contato tem pelo menos um número de telefone e adiciona ao texto
        if (!contact.getPhones().isEmpty()) {
            String firstPhoneNumber = contact.getPhones().get(0).getNumber();  // Obtém o primeiro número
            displayText += " - " + firstPhoneNumber;  // Concatena o nome com o primeiro número de telefone
        }

        // **Atualiza o TextView com o texto concatenado**
        holder.contactNameTextView.setText(displayText);  // Usa o displayText para atualizar a UI

        // Configuração do clique para editar o contato
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditContactActivity.class);
            intent.putExtra("contact", contact);  // Enviar o contato existente para a edição
            ((Activity) context).startActivityForResult(intent, 1);  // Iniciar a atividade de edição
        });

        // Configuração do clique para remover o contato
        holder.btnRemoveContact.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.deleteContact(contact.getId()); // Chama o método de exclusão
            contacts.remove(position); // Remove o contato da lista
            notifyItemRemoved(position); // Notifica o adaptador sobre a remoção
        });
    }


    @Override
    public int getItemCount() {
        return contacts.size(); // Retorna o número total de contatos
    }

    // ViewHolder para o RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactNameTextView; // TextView para exibir o nome do contato
        Button btnRemoveContact;  // Botão de remover o contato

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNameTextView = itemView.findViewById(R.id.txtContactName); // Inicializa o TextView
            btnRemoveContact = itemView.findViewById(R.id.btnRemoveContact); // Inicializa o botão de remover
        }
    }

}
