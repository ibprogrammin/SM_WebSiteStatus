package com.seriousmonkey.uptimerobot.assets;

/**
 * Created by Daniel Sevitti on 2017-09-03.
 */

public class DetailItem {
    private String mDescription;
    private String mAmount;
    private String mColor;
    private String mBackgroundColor;
    private String mDownTime;

    public DetailItem(String description, String amount) {
        this(description, amount, "#333333");
    }

    public DetailItem(String description, String amount, String color) {
        this(description, amount, "0.0", color, "#9fffffff");
    }

    public DetailItem(String description, String amount, String downTime, String color, String backgroundColor) {
        mDescription = description;
        mAmount = amount;
        mDownTime = downTime;
        mColor = color;
        mBackgroundColor = backgroundColor;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAmount() {
        return mAmount;
    }

    public String getDownTime() { return mDownTime; }

    public String getColor() {
        return mColor;
    }

    public String getBackgroundColor() {
        return mBackgroundColor;
    }

}
