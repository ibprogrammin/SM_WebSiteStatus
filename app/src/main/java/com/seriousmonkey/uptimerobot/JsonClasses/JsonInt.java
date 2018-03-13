package com.seriousmonkey.uptimerobot.JsonClasses;

/**
 * Created by Daniel Sevitti on 2018-03-07.
 */

public class JsonInt
{
    private int value;

    public JsonInt(int jSonInteger)
    {
        this.value = jSonInteger;
    }

    public int getValue()
    {
        return value;
    }
}