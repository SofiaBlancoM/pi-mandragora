package es.cifpcarlos3.pimandragora.shared.paging;

public record PageRequest(int page, int size) {

    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        if (size > 200) throw new IllegalArgumentException("size must be <= 200");
    }

    public static PageRequest firstPage(int size) {
        return new PageRequest(0, size);
    }

    public int offset() {
        return page * size;
    }
}
