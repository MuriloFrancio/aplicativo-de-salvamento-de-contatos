package vr.univali.adicionar_contatos;

import java.io.Serializable;
import java.util.List;

public class Contact implements Serializable {
    private long id;  // ID do contato
    private String name;
    private List<Phone> phones;

    // Construtor
    public Contact(long id, String name, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.phones = phones;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }
}
