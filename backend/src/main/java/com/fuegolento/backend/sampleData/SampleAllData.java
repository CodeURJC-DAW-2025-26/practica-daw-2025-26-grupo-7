package com.fuegolento.backend.sampleData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.model.Image;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.OrderItem;
import com.fuegolento.backend.model.User;
import com.fuegolento.backend.repository.DishRepository;
import com.fuegolento.backend.repository.OrderRepository;
import com.fuegolento.backend.repository.UserRepository;

/**
 * Loads sample data into the database when the application starts.
 * Inserts users + dishes (with images as BLOB) + a few kitchen orders if tables are empty.
 */
@Service
public class SampleAllData {

    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public SampleAllData(
            UserRepository userRepository,
            DishRepository dishRepository,
            OrderRepository orderRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        // ---------------------------
        // USERS
        // ---------------------------
        if (userRepository.count() == 0) {

            userRepository.save(new User(
                    "user",
                    "user@fuegolento.com",
                    LocalDate.of(2000, 5, 10),
                    passwordEncoder.encode("user123"),
                    "USER"
            ));

            userRepository.save(new User(
                    "admin",
                    "admin@fuegolento.com",
                    LocalDate.of(1995, 1, 1),
                    passwordEncoder.encode("admin123"),
                    "USER", "ADMIN"
            ));
        }

        User sampleUser = userRepository.findByUsername("user")
                .orElseThrow(() -> new RuntimeException("Sample user not found"));

        // ---------------------------
        // DISHES + IMAGES (BLOB)
        // ---------------------------
        if (dishRepository.count() == 0) {

            Dish chuleton = new Dish(
                    DishCategory.MEAT,
                    "Chuletón a la piedra",
                    "Corte premium servido con piedra caliente y guarnición.",
                    List.of(),
                    new BigDecimal("24.90"),
                    true
            );
            chuleton.setImage(imageFromStatic("static/img/dishes/chuleton-piedra.png"));

            Dish costillar = new Dish(
                    DishCategory.MEAT,
                    "Costillar a la brasa",
                    "Cocción lenta, glaseado suave y sabor ahumado.",
                    List.of(),
                    new BigDecimal("19.50"),
                    true
            );
            costillar.setImage(imageFromStatic("static/img/dishes/costillar-brasa.png"));

            Dish croquetas = new Dish(
                    DishCategory.STARTER,
                    "Croquetas caseras",
                    "Cremosas, crujientes y recién hechas.",
                    List.of(),
                    new BigDecimal("7.50"),
                    true
            );
            croquetas.setImage(imageFromStatic("static/img/dishes/croquetas.png"));

            Dish tartaQueso = new Dish(
                    DishCategory.DESSERT,
                    "Tarta de queso",
                    "Horneada, cremosa y con base de galleta.",
                    List.of(),
                    new BigDecimal("6.50"),
                    false
            );
            tartaQueso.setImage(imageFromStatic("static/img/dishes/tarta-queso.png"));

            Dish entrecot = new Dish(
                    DishCategory.MEAT,
                    "Entrecot a la piedra",
                    "Carne tierna para terminar al punto en mesa.",
                    List.of(),
                    new BigDecimal("18.90"),
                    true
            );
            entrecot.setImage(imageFromStatic("static/img/dishes/entrecot-piedra.png"));

            Dish secreto = new Dish(
                    DishCategory.MEAT,
                    "Secreto ibérico a la brasa",
                    "Jugoso y dorado al carbón, con toque de sal en escamas.",
                    List.of(),
                    new BigDecimal("16.90"),
                    true
            );
            secreto.setImage(imageFromStatic("static/img/dishes/secreto-iberico.png"));

            Dish ensalada = new Dish(
                    DishCategory.STARTER,
                    "Ensalada de temporada",
                    "Fresca, ligera y perfecta para compartir.",
                    List.of(),
                    new BigDecimal("6.90"),
                    true
            );
            ensalada.setImage(imageFromStatic("static/img/dishes/ensalada-temporada.png"));

            Dish agua = new Dish(
                    DishCategory.DRINK,
                    "Agua mineral",
                    "Agua fría (con o sin gas).",
                    List.of(),
                    new BigDecimal("2.00"),
                    true
            );
            agua.setImage(imageFromStatic("static/img/dishes/agua-mineral.png"));

            dishRepository.saveAll(List.of(
                    chuleton, costillar, croquetas, tartaQueso,
                    entrecot, secreto, ensalada, agua
            ));
        }

        // ---------------------------
        // ORDERS for kitchen board (SENT_TO_KITCHEN / IN_PROGRESS / READY)
        // ---------------------------
        if (orderRepository.count() == 0) {

            Dish chuleton = findDish("Chuletón a la piedra");
            Dish costillar = findDish("Costillar a la brasa");
            Dish croquetas = findDish("Croquetas caseras");
            Dish entrecot = findDish("Entrecot a la piedra");
            Dish ensalada = findDish("Ensalada de temporada");
            Dish agua = findDish("Agua mineral");

            // 1) RECEIVED (SENT_TO_KITCHEN)
            Order o1 = new Order(sampleUser);
            o1.setStatus(OrderStatus.SENT_TO_KITCHEN);
            o1.setTableNumber(7);
            o1.setCustomerNote("Sin sal en la ensalada, por favor.");
            addItem(o1, chuleton, 2);
            addItem(o1, agua, 2);
            orderRepository.save(o1);

            // 2) IN PROGRESS
            Order o2 = new Order(sampleUser);
            o2.setStatus(OrderStatus.IN_PROGRESS);
            o2.setTableNumber(3);
            addItem(o2, costillar, 1);
            addItem(o2, ensalada, 1);
            orderRepository.save(o2);

            // 3) READY
            Order o3 = new Order(sampleUser);
            o3.setStatus(OrderStatus.READY);
            o3.setTableNumber(12);
            addItem(o3, entrecot, 1);
            addItem(o3, croquetas, 2);
            orderRepository.save(o3);
        }
    }

    private Dish findDish(String exactName) {
        return dishRepository.findByNameContainingIgnoreCase(exactName).stream()
                .filter(d -> d.getName().equalsIgnoreCase(exactName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dish not found: " + exactName));
    }

    private void addItem(Order order, Dish dish, int quantity) {
        OrderItem item = new OrderItem(dish, quantity, dish.getPrice());
        order.addItem(item);
    }

    /**
     * Loads an image from classpath (resources/static/...) and stores it in an Image entity as BLOB.
     */
    private Image imageFromStatic(String classpathLocation) {
        try {
            byte[] bytes = new ClassPathResource(classpathLocation).getInputStream().readAllBytes();
            Image img = new Image();
            img.setImageFile(new SerialBlob(bytes));
            return img;
        } catch (Exception e) {
            throw new RuntimeException("Failed loading image: " + classpathLocation, e);
        }
    }
}