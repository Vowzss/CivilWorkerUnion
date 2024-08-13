package com.oneliferp.cwu.misc.pagination;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class PaginationContext<A> {
    private final HashMap<Integer, A> pages;
    private final A previewPage;
    private int currentPageIndex;

    public PaginationContext(final List<A> pages, final A previewPage) {
        this.pages = new HashMap<>();
        this.previewPage = previewPage;
        this.currentPageIndex = 0;

        IntStream.range(0, pages.size()).forEach(index -> this.pages.put(index, pages.get(index)));
    }

    /* Getters & Setters */
    public A getCurrent() {
        if (this.currentPageIndex == -1) return this.previewPage;
        return this.pages.get(this.currentPageIndex);
    }

    public void setFirst() {
        this.currentPageIndex = 0;
    }
    public void setLast() {
        this.currentPageIndex = this.pages.size() - 1;
    }

    public void setNext() {
        this.currentPageIndex += 1;
    }
    public void setPrev() {
        this.currentPageIndex -= 1;
    }

    public void setPreview() {
        this.currentPageIndex = -1;
    }

    public boolean contains(final A type) {
        return this.pages.containsValue(type);
    }

    /* Utils */
    public boolean isFirst() {
        return this.currentPageIndex == 0 && !this.isLast();
    }
    public boolean isLast() {
        return this.currentPageIndex == this.pages.size() - 1;
    }

    public int getCurrentStep() {
        return this.currentPageIndex + 1;
    }
    public int getMaxSteps() {
        return this.pages.size();
    }
}
