package sumdu.edu.ua.core.domain;

public class PageRequest {
    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = size <= 0 ? 20 : size;
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getOffset() { return page * size; }
}