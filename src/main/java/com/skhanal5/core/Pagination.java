package com.skhanal5.core;

class Pagination {
    int start;

    int end;

    Pagination(int start, int end) {
        this.start = start;
        this.end = end;
    }

    String serialize() {
        return this.start + "-" + this.end;
    }
}
