package com.neology.loyaltycard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cesar on 21/01/16.
 */
public class CardInfo implements Parcelable {

    public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
        @Override
        public CardInfo createFromParcel(Parcel in) {
            return new CardInfo(in);
        }

        @Override
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };
    private String cardId;
    private String clientId;
    private String availableBalance;
    private int points;
    private String creationDate;
    private String expirationDate;
    private String fuelType;

    public CardInfo(String cardId,
                    String clientId,
                    String availableBalance,
                    int points,
                    String creationDate,
                    String expirationDate,
                    String fuelType) {

        this.cardId = cardId;
        this.clientId = clientId;
        this.availableBalance = availableBalance;
        this.points = points;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.fuelType = fuelType;
    }

    protected CardInfo(Parcel in) {
        cardId = in.readString();
        clientId = in.readString();
        availableBalance = in.readString();
        points = in.readInt();
        creationDate = in.readString();
        expirationDate = in.readString();
        fuelType = in.readString();
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardId);
        dest.writeString(clientId);
        dest.writeString(availableBalance);
        dest.writeInt(points);
        dest.writeString(creationDate);
        dest.writeString(expirationDate);
        dest.writeString(fuelType);
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}
