package com.scz.odczytgazomierza.RecyclerView2;

public class Item2 {
    private Long id;
    private String bankAccountNumber;
    private String name;

    public Item2() {
    }

    public Item2(Long id, String bankAccountNumber, String name) {
        this.id = id;
        this.bankAccountNumber = bankAccountNumber;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
