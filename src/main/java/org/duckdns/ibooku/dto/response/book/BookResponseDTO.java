package org.duckdns.ibooku.dto.response.book;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookResponseDTO {
    private String name;
    private String isbn;
    private String image;
    private String subject;
    private String author;
    private String publisher;
    private String content;
    private Double point;

    @Builder
    public BookResponseDTO(String name, String isbn, String image, String subject, String author, String publisher, String content, Double point) {
        this.name = name;
        this.isbn = isbn;
        this.image = image != null && image.contains("kolis_on") ? "http://cover.nl.go.kr/" + image : image;
        this.subject = subject;
        this.author = author;
        this.publisher = publisher;
        this.content = content;
        this.point = point;
    }
}
