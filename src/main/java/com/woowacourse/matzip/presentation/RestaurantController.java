package com.woowacourse.matzip.presentation;

import static com.woowacourse.matzip.domain.restaurant.SortCondition.RATING;

import com.woowacourse.matzip.application.RestaurantService;
import com.woowacourse.matzip.application.response.RestaurantResponse;
import com.woowacourse.matzip.application.response.RestaurantTitleResponse;
import com.woowacourse.matzip.application.response.RestaurantTitlesResponse;
import com.woowacourse.matzip.domain.restaurant.SortCondition;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/campuses/{campusId}/restaurants")
    public ResponseEntity<RestaurantTitlesResponse> showPage(@PathVariable final Long campusId,
                                                             @RequestParam(required = false) final Long categoryId,
                                                             @RequestParam(value = "filter", defaultValue = "DEFAULT") final String filterName,
                                                             final Pageable pageable) {
        SortCondition sortCondition = SortCondition.from(filterName);
        if (sortCondition == RATING) {
            return ResponseEntity.ok(restaurantService.findByCampusIdOrderByRatingDesc(campusId, categoryId, pageable));
        }
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                sortCondition.getValue());
        return ResponseEntity.ok(restaurantService.findByCampusId(campusId, categoryId, pageableWithSort));
    }

    @GetMapping("/campuses/{campusId}/restaurants/random")
    public ResponseEntity<List<RestaurantTitleResponse>> showRandoms(@PathVariable final Long campusId,
                                                                     @RequestParam final int size) {
        return ResponseEntity.ok(restaurantService.findRandomsByCampusId(campusId, size));
    }

    @GetMapping("/campuses/{campusId}/restaurants/search")
    public ResponseEntity<RestaurantTitlesResponse> searchRestaurantsPage(@PathVariable final Long campusId,
                                                                          @RequestParam final String name,
                                                                          final Pageable pageable) {
        return ResponseEntity.ok(
                restaurantService.findTitlesByCampusIdAndNameContainingIgnoreCaseIdDescSort(campusId, name, pageable));
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponse> showRestaurant(@PathVariable final Long restaurantId) {
        return ResponseEntity.ok(restaurantService.findById(restaurantId));
    }
}
