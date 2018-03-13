package com.seriousmonkey.uptimerobot.JsonClasses;

/**
 * Created by Daniel on 2018-03-07.
 */

public class Pagination {
    public JsonInt offset;
    public JsonInt limit;
    public JsonInt total;

    public Pagination(JsonInt offset, JsonInt limit, JsonInt total) {
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }
}
