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
        if (page < 0) throw new IllegalArgumentException("La página debe ser mayor o igual que 0");
        if (size <= 0) throw new IllegalArgumentException("El tamaño debe ser mayor que 0");
        if (totalItems < 0) throw new IllegalArgumentException("El total de items debe ser mayor o igual que 0");
    }

    public boolean hasNext() {
        return page + 1 < totalPages();
    }

    public long totalPages() {
        if (totalItems == 0) return 0;
        return (totalItems + size - 1) / size;
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}
