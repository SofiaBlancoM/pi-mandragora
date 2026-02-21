package es.cifpcarlos3.pimandragora.shared.paging;

import java.util.List;

public record Page<T>(
        List<T> items,
        int page,
        int size,
        long totalItems
) {
    public Page {
        items = (items == null) ? List.of() : List.copyOf(items);
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        if (totalItems < 0) throw new IllegalArgumentException("totalItems must be >= 0");
    }

    public long totalPages() {
        if (totalItems == 0) return 0;
        return (totalItems + size - 1) / size;
    }

    public <U> Page<U> map(java.util.function.Function<? super T, U> converter) {
        List<U> mappedItems = this.items.stream()
                .map(converter)
                .toList();
        return new Page<>(mappedItems, this.page, this.size, this.totalItems);
    }

    public boolean hasNext() {
        return page + 1 < totalPages();
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}
