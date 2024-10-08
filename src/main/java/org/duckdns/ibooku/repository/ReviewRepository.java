package org.duckdns.ibooku.repository;

import org.duckdns.ibooku.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query(value = "SELECT * FROM review WHERE isbn = :isbn ORDER BY id DESC", nativeQuery = true)
    List<Review> findReviewsByIsbnOrderByIdDesc(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM review WHERE isbn = :isbn AND spoiler = false ORDER BY id DESC", nativeQuery = true)
    List<Review> findReviewsByIsbnAndIsSpoilerNoneOrderByIdDesc(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM review WHERE isbn = :isbn ORDER BY point DESC", nativeQuery = true)
    List<Review> findReviewsByIsbnOrderByPointDesc(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM review WHERE isbn = :isbn AND spoiler = false ORDER BY point DESC", nativeQuery = true)
    List<Review> findReviewsByIsbnAndIsSpoilerNoneOrderByPointDesc(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM review WHERE isbn = :isbn ORDER BY point ASC", nativeQuery = true)
    List<Review> findReviewsByIsbnOrderByPointAsc(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM review WHERE isbn = :isbn AND spoiler = false ORDER BY point ASC", nativeQuery = true)
    List<Review> findReviewsByIsbnAndIsSpoilerNoneOrderByPointAsc(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM review l WHERE l.lat BETWEEN :lat - 0.05 AND :lat + 0.05 AND l.lon BETWEEN :lon - 0.05 AND :lon + 0.05", nativeQuery = true)
    List<Review> findByLatLonRange(@Param("lat") double lat, @Param("lon") double lon);

    @Query(value = "SELECT ROUND(AVG(r.point), 1) FROM review r WHERE r.isbn = :isbn", nativeQuery = true)
    Double findAveragePointByIsbn(@Param("isbn") String isbn);
}
