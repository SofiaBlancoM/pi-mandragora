package es.cifpcarlos3.pimandragora.shared.paging;

public record PageRequest(int page, int size) {

    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("La página debe ser mayor o igual que 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño debe ser mayor que 0");
        }
        if (size > 200) {
            throw new IllegalArgumentException("El tamaño máximo permitido es 200");
        }
    }

    public static PageRequest firstPage(int size) {
        return new PageRequest(0, size);
    }

    public int offset() {
        return page * size;
    }
}