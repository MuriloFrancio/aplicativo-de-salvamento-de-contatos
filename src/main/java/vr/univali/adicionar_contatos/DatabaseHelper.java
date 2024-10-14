package vr.univali.adicionar_contatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Versão do banco de dados
    private static final int DATABASE_VERSION = 1;

    // Nome do banco de dados
    private static final String DATABASE_NAME = "contacts.db";

    // Nome da tabela
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_PHONES = "phones";

    // Colunas da tabela de contatos
    private static final String COLUMN_CONTACT_ID = "id";
    private static final String COLUMN_CONTACT_NAME = "name";

    // Colunas da tabela de telefones
    private static final String COLUMN_PHONE_ID = "id";
    private static final String COLUMN_PHONE_NUMBER = "number";
    private static final String COLUMN_PHONE_TYPE = "type";
    private static final String COLUMN_PHONE_CONTACT_ID = "contact_id"; // Chave estrangeira

    // Criação da tabela de contatos
    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
            + COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CONTACT_NAME + " TEXT NOT NULL" + ")";

    // Criação da tabela de telefones
    private static final String CREATE_PHONES_TABLE = "CREATE TABLE " + TABLE_PHONES + "("
            + COLUMN_PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PHONE_NUMBER + " TEXT, "
            + COLUMN_PHONE_TYPE + " TEXT, "
            + COLUMN_PHONE_CONTACT_ID + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_PHONE_CONTACT_ID + ") REFERENCES "
            + TABLE_CONTACTS + "(" + COLUMN_CONTACT_ID + ") ON DELETE CASCADE" + ")";

    // Construtor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação das tabelas
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_PHONES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualiza o banco de dados, se necessário
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // CRUD (Create, Read, Update, Delete) para contatos e telefones

    // Adicionar um novo contato
    public long addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, contact.getName());

        long contactId = db.insert(TABLE_CONTACTS, null, values);

        // Adicionar telefones do contato
        for (Phone phone : contact.getPhones()) {
            addPhone(phone, contactId);
        }

        db.close();
        return contactId;
    }

    // Adicionar um telefone
    private void addPhone(Phone phone, long contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phone.getNumber());
        values.put(COLUMN_PHONE_TYPE, phone.getType());
        values.put(COLUMN_PHONE_CONTACT_ID, contactId);

        db.insert(TABLE_PHONES, null, values);
    }


    // Obter todos os contatos
    // Obter todos os contatos
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME));

                // Cria o objeto Contact e adiciona à lista
                Contact contact = new Contact(id, name, getPhonesForContact(id));  // Chama o método que você já tem
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }


    // Obter todos os telefones de um contato
    private List<Phone> getPhonesForContact(long contactId) {
        List<Phone> phoneList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_CONTACT_ID + " = " + contactId;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_TYPE));

                Phone phone = new Phone(number, type);
                phoneList.add(phone);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return phoneList;
    }

    // Atualizar um contato
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, contact.getName());

        // Atualizar o nome do contato
        int result = db.update(TABLE_CONTACTS, values, COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});

        // Deletar os telefones antigos e adicionar os novos
        deletePhones(contact.getId());
        for (Phone phone : contact.getPhones()) {
            addPhone(phone, contact.getId());
        }

        db.close();
        return result;
    }

    // Deletar os telefones de um contato
    private void deletePhones(long contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHONES, COLUMN_PHONE_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)});
    }

    // Deletar um contato
    // Deletar um contato
    public void deleteContact(long contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Primeiro, remove todos os telefones associados ao contato
        deletePhones(contactId);
        // Agora, remove o contato
        db.delete(TABLE_CONTACTS, COLUMN_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)});
        db.close();
    }

}
