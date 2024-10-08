package org.duckdns.ibooku.service.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.ibooku.dto.response.book.BookResponseDTO;
import org.duckdns.ibooku.repository.ReviewRepository;
import org.duckdns.ibooku.util.JSONUtils;
import org.duckdns.ibooku.util.StringUtils;
import org.duckdns.ibooku.util.URLUtils;
import org.duckdns.ibooku.util.network.Get;
import org.duckdns.ibooku.util.network.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
    @Value("${api-key.library-bigdata}")
    private String KEY_LIBRARY_BIGDATA;

    @Value("${api-key.nl}")
    private String KEY_NL;

    private final ReviewRepository reviewRepository;

    public List<BookResponseDTO> search(String keyword) {
        List<BookResponseDTO> books = new ArrayList<>();

        String url = String.format("https://www.nl.go.kr/NL/search/openApi/search.do?key=%s&systemType=%s&pageNum=1&pageSize=30&apiType=json&kwd=%s", KEY_NL, "오프라인자료", URLUtils.urlEncode(keyword));

        Header header = new Header();

        try {
            Get get = new Get(url)
                    .setHeader(header)
                    .execute();

            int responseCode = get.getResponseCode();
            if (responseCode != org.apache.http.HttpStatus.SC_OK) {
                log.debug("responseCode: {}", responseCode);
                throw new RuntimeException("통신 오류: " + get.getUrl());
            }

            JsonObject jsonObject = JSONUtils.parse(get.getResult());
            JsonArray result = jsonObject.getAsJsonArray("result");

            for (Object o: result) {
                JsonObject book = (JsonObject) o;

                String name = book.get("titleInfo").getAsString();

                String[] isbnArray = book.get("isbn").getAsString().split(" ");
                String isbn = isbnArray.length == 0 ? "" : isbnArray[0];
                String image = book.get("imageUrl").getAsString();
                String subject = book.get("kdcName1s").getAsString();
                String author = book.get("authorInfo").getAsString();
                String publisher = book.get("pubInfo").getAsString();
                String content = "";
                Double point = reviewRepository.findAveragePointByIsbn(isbn);

                if (StringUtils.isNotNullOrEmpty(isbn)) {
                    url = String.format("https://data4library.kr/api/srchDtlList?authKey=%s&isbn13=%s&format=json", KEY_LIBRARY_BIGDATA, isbn);

                    get = new Get(url)
                            .setHeader(header)
                            .execute();

                    responseCode = get.getResponseCode();
                    if (responseCode != org.apache.http.HttpStatus.SC_OK) {
                        log.debug("responseCode: {}", responseCode);
                        throw new RuntimeException("통신 오류: " + get.getUrl());
                    }

                    JsonObject jobj = JSONUtils.parse(get.getResult());
                    JsonObject response = jobj.getAsJsonObject("response");
                    JsonArray details = response.getAsJsonArray("detail");

                    if (details != null) {
                        for (Object obj: details) {
                            JsonObject detail = (JsonObject) obj;
                            JsonObject bookDetail = detail.getAsJsonObject("book");

                            image = bookDetail.get("bookImageURL").getAsString();
                            content = bookDetail.get("description").getAsString();

                            break;
                        }
                    }
                }

                books.add(BookResponseDTO.builder()
                                .name(name)
                                .isbn(isbn)
                                .image(image)
                                .subject(subject)
                                .author(author)
                                .publisher(publisher)
                                .content(content)
                                .point(point)
                        .build());
            }

            return books;
        } catch(Exception e) {
            e.printStackTrace();
            log.error("통신 오류가 발생했습니다.");
            throw new RuntimeException();
        }
    }

    public BookResponseDTO info(String searchIsbn) {
        String url = String.format("https://www.nl.go.kr/NL/search/openApi/search.do?key=%s&systemType=%s&pageNum=1&pageSize=30&apiType=json&detailSearch=true&isbnOp=isbn&isbnCode=%s", KEY_NL, "오프라인자료", searchIsbn);

        Header header = new Header();

        try {
            Get get = new Get(url)
                    .setHeader(header)
                    .execute();

            int responseCode = get.getResponseCode();
            if (responseCode != org.apache.http.HttpStatus.SC_OK) {
                log.debug("responseCode: {}", responseCode);
                throw new RuntimeException("통신 오류: " + get.getUrl());
            }

            JsonObject jsonObject = JSONUtils.parse(get.getResult());
            JsonArray result = jsonObject.getAsJsonArray("result");

            for (Object o: result) {
                JsonObject book = (JsonObject) o;

                String name = book.get("titleInfo").getAsString();

                String[] isbnArray = book.get("isbn").getAsString().split(" ");
                String isbn = isbnArray.length == 0 ? "" : isbnArray[0];
                String image = book.get("imageUrl").getAsString();
                String subject = book.get("kdcName1s").getAsString();
                String author = book.get("authorInfo").getAsString();
                String publisher = book.get("pubInfo").getAsString();
                String content = "";
                Double point = reviewRepository.findAveragePointByIsbn(isbn);

                if (StringUtils.isNotNullOrEmpty(isbn)) {
                    url = String.format("https://data4library.kr/api/srchDtlList?authKey=%s&isbn13=%s&format=json", KEY_LIBRARY_BIGDATA, isbn);

                    get = new Get(url)
                            .setHeader(header)
                            .execute();

                    responseCode = get.getResponseCode();
                    if (responseCode != org.apache.http.HttpStatus.SC_OK) {
                        log.debug("responseCode: {}", responseCode);
                        throw new RuntimeException("통신 오류: " + get.getUrl());
                    }

                    JsonObject jobj = JSONUtils.parse(get.getResult());
                    JsonObject response = jobj.getAsJsonObject("response");
                    JsonArray details = response.getAsJsonArray("detail");

                    for (Object obj: details) {
                        JsonObject detail = (JsonObject) obj;
                        JsonObject bookDetail = detail.getAsJsonObject("book");

                        image = bookDetail.get("bookImageURL").getAsString();
                        content = bookDetail.get("description").getAsString();

                        break;
                    }
                }

                return BookResponseDTO.builder()
                        .name(name)
                        .isbn(isbn)
                        .image(image)
                        .subject(subject)
                        .author(author)
                        .publisher(publisher)
                        .content(content)
                        .point(point)
                        .build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            log.error("통신 오류가 발생했습니다.");
            throw new RuntimeException();
        }

        return null;
    }
}
