package chanyoung.GoodNightHackathon.review.service;

import chanyoung.GoodNightHackathon.restaurant.domain.Restaurant;
import chanyoung.GoodNightHackathon.restaurant.service.RestaurantService;
import chanyoung.GoodNightHackathon.review.domin.Review;
import chanyoung.GoodNightHackathon.review.domin.ReviewRepository;
import chanyoung.GoodNightHackathon.review.dto.ReviewRequest;
import chanyoung.GoodNightHackathon.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RestaurantService restaurantService;
    private final ReviewRepository repository;
    public void save (ReviewRequest request) {
        Optional<Restaurant> restaurant = restaurantService.findId(request.getRestaurant_id());
        Review review = Review.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .restaurant(restaurant.get())
                .build();
        repository.save(review);
    }

    public ReviewResponse findId(Long id) {
        Review review = repository.findById(id).get();
        Restaurant restaurant = review.getRestaurant();
        ReviewResponse dto = ReviewResponse.builder()
                .title(review.getTitle())
                .content(review.getContent())
                .restaurantName(restaurant.getName())
                .build();
        return dto;
    }

    public void updateReview(Long id, String title, String content) {
        Optional<Review> review = repository.findById(id);
        review.get().update(title, content);
        repository.save(review.get());
    }

    public void removeReview(Long id) {
        Optional<Review> review = repository.findById(id);
        repository.delete(review.get());
    }

    public List<ReviewResponse> sortReview(String title, String content, boolean orderType) { //orderType True인 경우 오름차순
        PageRequest request = PageRequest.of(0, 5);

        List<ReviewResponse> orderList;

        if (orderType) {
            orderList = repository.findByTitleContent(request, title, content).stream()
                    .sorted(Comparator.comparing(Review::getCreatedAt))
                    .map(this::mapToReviewResponse)
                    .collect(Collectors.toList());
        } else {
            orderList = repository.findByTitleContent(request, title, content).stream()
                    .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                    .map(this::mapToReviewResponse)
                    .collect(Collectors.toList());
        }

        return orderList;
    }

    public ReviewResponse mapToReviewResponse (Review review) {
        return ReviewResponse.builder()
                .title(review.getTitle())
                .content(review.getContent())
                .restaurantName(review.getRestaurant().getName())
                .build();
    }
 }