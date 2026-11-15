package sumdu.edu.ua.web.controller.dto;

public record CommentRequest(long bookId, String author, String text) {}