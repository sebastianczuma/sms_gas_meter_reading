package com.scz.odczytgazomierza.RecyclerView;

public class Item {
    private Long id;
    private String date;
    private String meterReading;
    private String bankAccountNumber;
    private String phoneNumber;

    public Item() {
    }

    public Item(Long id, String date, String meterReading, String bankAccountNumber, String phoneNumber) {
        this.id = id;
        this.date = date;
        this.meterReading = meterReading;
        this.bankAccountNumber = bankAccountNumber;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMeterReading() {
        return meterReading;
    }

    public void setMeterReading(String meterReading) {
        this.meterReading = meterReading;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
}
