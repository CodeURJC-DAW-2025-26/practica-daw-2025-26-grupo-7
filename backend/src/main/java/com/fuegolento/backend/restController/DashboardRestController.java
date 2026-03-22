package com.fuegolento.backend.restController;

import com.fuegolento.backend.repository.OrderRepository;
import com.fuegolento.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class DashboardRestController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DashboardRestController(OrderRepository orderRepository,
                                   UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public record PointDto(String label, BigDecimal value) {}
    public record CountDto(String label, Long value) {}

    /**
     * Revenue per day (DELIVERED orders) for the last N days.
     * Example: GET /api/v1/admin/dashboard/revenue-daily?days=14
     */
    @GetMapping("/revenue-daily")
    public ResponseEntity<List<PointDto>> revenueDaily(@RequestParam(defaultValue = "14") int days) {

        LocalDateTime from = LocalDateTime.now().minusDays(Math.max(days, 1));
        List<OrderRepository.DailyRevenueRow> rows = orderRepository.revenueByDaySince(from);

        Map<LocalDate, BigDecimal> revenueByDay = new HashMap<>();
        for (OrderRepository.DailyRevenueRow row : rows) {
            LocalDate day = row.getDay().toLocalDate();
            revenueByDay.put(day, row.getRevenue() == null ? BigDecimal.ZERO : row.getRevenue());
        }

        List<PointDto> result = new ArrayList<>();
        LocalDate start = from.toLocalDate();
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            BigDecimal value = revenueByDay.getOrDefault(date, BigDecimal.ZERO);
            result.add(new PointDto(date.toString(), value));
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Delivered orders grouped by hour (0..23) for the last N days.
     * Example: GET /api/v1/admin/dashboard/orders-by-hour?days=14
     */
    @GetMapping("/orders-by-hour")
    public ResponseEntity<List<CountDto>> ordersByHour(@RequestParam(defaultValue = "14") int days) {

        LocalDateTime from = LocalDateTime.now().minusDays(Math.max(days, 1));
        List<OrderRepository.HourCountRow> rows = orderRepository.deliveredCountByHourSince(from);

        long[] counts = new long[24];
        for (OrderRepository.HourCountRow row : rows) {
            Integer hour = row.getHour();
            if (hour != null && hour >= 0 && hour <= 23) {
                counts[hour] = row.getCount() == null ? 0L : row.getCount();
            }
        }

        List<CountDto> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            result.add(new CountDto(String.format("%02d:00", hour), counts[hour]));
        }

        return ResponseEntity.ok(result);
    }

    /**
     * User registrations per day for the last N days.
     * Example: GET /api/v1/admin/dashboard/users-registrations?days=40
     */
    @GetMapping("/users-registrations")
    public ResponseEntity<List<CountDto>> userRegistrations(@RequestParam(defaultValue = "40") int days) {

        LocalDateTime from = LocalDateTime.now().minusDays(Math.max(days, 1));
        List<UserRepository.DailyUserCountRow> rows = userRepository.registrationsByDaySince(from);

        Map<LocalDate, Long> usersByDay = new HashMap<>();
        for (UserRepository.DailyUserCountRow row : rows) {
            LocalDate day = row.getDay().toLocalDate();
            usersByDay.put(day, row.getCount() == null ? 0L : row.getCount());
        }

        List<CountDto> result = new ArrayList<>();
        LocalDate start = from.toLocalDate();
        LocalDate end = LocalDate.now();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            result.add(new CountDto(date.toString(), usersByDay.getOrDefault(date, 0L)));
        }

        return ResponseEntity.ok(result);
    }
}