package com.oneliferp.cwu.misc.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Pageable<T> {
    @JsonIgnore
    protected PaginationContext<T> pagination;

    /* State methods */
    public abstract void start();

    public abstract void end();

    public abstract void reset();

    public abstract boolean verify();

    /* Page methods */
    public T getCurrentPage() {
        return this.pagination.getCurrent();
    }

    public void goNextPage() {
        this.pagination.setNext();
    }

    public void goPrevPage() {
        this.pagination.setPrev();
    }

    public void goPreviewPage() {
        this.pagination.setPreview();
    }

    public void goFirstPage() {
        this.pagination.setFirst();
    }

    public boolean isFirstPage() {
        return this.pagination.isFirst();
    }

    public boolean isLastPage() {
        return this.pagination.isLast();
    }

    public int getCurrentStep() {
        return this.pagination.getCurrentStep();
    }

    public int getMaxSteps() {
        return this.pagination.getMaxSteps();
    }

    public boolean hasPage(final T type) {
        return this.pagination.contains(type);
    }
}
