package ru.sviridov.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"id", "title", "number", "fk_cards_users"})
public class Card {

    private long id;

    private String title;

    private String number;

    private long userId;


    public Card() {
    }

    public Card(long id, String title, String number, long userId) {
        this.id = id;
        this.title = title;
        this.number = number;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && userId == card.userId && Objects.equals(title, card.title) && Objects.equals(number, card.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, number, userId);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", number='" + number + '\'' +
                ", userId=" + userId +
                '}';
    }
}
