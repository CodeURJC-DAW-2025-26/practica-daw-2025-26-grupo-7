package com.fuegolento.backend.sampleData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;

import com.fuegolento.backend.enums.Allergen;
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

            userRepository.save(new User(
                    "maria",
                    "maria@fuegolento.com",
                    LocalDate.of(2001, 2, 18),
                    passwordEncoder.encode("maria123"),
                    "USER"
            ));

            userRepository.save(new User(
                    "juan",
                    "juan@fuegolento.com",
                    LocalDate.of(1999, 9, 7),
                    passwordEncoder.encode("juan123"),
                    "USER"
            ));

            User lucia = new User(
                    "lucia",
                    "lucia@fuegolento.com",
                    LocalDate.of(2002, 11, 25),
                    passwordEncoder.encode("lucia123"),
                    "USER"
            );
            lucia.setBanned(true); // to test banned flow
            userRepository.save(lucia);
        }

        User user = userRepository.findByUsername("user")
                .orElseThrow(() -> new RuntimeException("Sample user not found: user"));

        User maria = userRepository.findByUsername("maria")
                .orElseThrow(() -> new RuntimeException("Sample user not found: maria"));

        User juan = userRepository.findByUsername("juan")
                .orElseThrow(() -> new RuntimeException("Sample user not found: juan"));

        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Sample user not found: admin"));

        // ---------------------------
        // DISHES + IMAGES (BLOB)
        // ---------------------------
        if (dishRepository.count() == 0) {

            // ===== Existing dishes =====

            Dish chuleton = new Dish(
                    DishCategory.MEAT,
                    "Chuletón a la piedra",
                    "Corte premium servido con piedra caliente y guarnición.",
                    List.of(),
                    new BigDecimal("24.90"),
                    true
            );
            setImageIfExists(chuleton, "static/img/dishes/chuleton-piedra.png");

            Dish costillar = new Dish(
                    DishCategory.MEAT,
                    "Costillar a la brasa",
                    "Cocción lenta, glaseado suave y sabor ahumado.",
                    List.of(),
                    new BigDecimal("19.50"),
                    true
            );
            setImageIfExists(costillar, "static/img/dishes/costillar-brasa.png");

            Dish croquetas = new Dish(
                    DishCategory.STARTER,
                    "Croquetas caseras",
                    "Cremosas, crujientes y recién hechas.",
                    List.of(Allergen.GLUTEN, Allergen.MILK, Allergen.EGG),
                    new BigDecimal("7.50"),
                    true
            );
            setImageIfExists(croquetas, "static/img/dishes/croquetas.png");

            Dish tartaQueso = new Dish(
                    DishCategory.DESSERT,
                    "Tarta de queso",
                    "Horneada, cremosa y con base de galleta.",
                    List.of(Allergen.MILK, Allergen.EGG, Allergen.GLUTEN),
                    new BigDecimal("6.50"),
                    true
            );
            setImageIfExists(tartaQueso, "static/img/dishes/tarta-queso.png");

            Dish entrecot = new Dish(
                    DishCategory.MEAT,
                    "Entrecot a la piedra",
                    "Carne tierna para terminar al punto en mesa.",
                    List.of(),
                    new BigDecimal("18.90"),
                    true
            );
            setImageIfExists(entrecot, "static/img/dishes/entrecot-piedra.png");

            Dish secreto = new Dish(
                    DishCategory.MEAT,
                    "Secreto ibérico a la brasa",
                    "Jugoso y dorado al carbón, con toque de sal en escamas.",
                    List.of(),
                    new BigDecimal("16.90"),
                    true
            );
            setImageIfExists(secreto, "static/img/dishes/secreto-iberico.png");

            Dish ensalada = new Dish(
                    DishCategory.STARTER,
                    "Ensalada de temporada",
                    "Fresca, ligera y perfecta para compartir.",
                    List.of(),
                    new BigDecimal("6.90"),
                    true
            );
            setImageIfExists(ensalada, "static/img/dishes/ensalada-temporada.png");

            Dish agua = new Dish(
                    DishCategory.DRINK,
                    "Agua mineral",
                    "Agua fría (con o sin gas).",
                    List.of(),
                    new BigDecimal("2.00"),
                    true
            );
            setImageIfExists(agua, "static/img/dishes/agua-mineral.png");

            // ===== NEW MEATS (sin picaña) =====

            Dish tomahawk = new Dish(
                    DishCategory.MEAT,
                    "Tomahawk a la brasa",
                    "Tomahawk a la brasa (aprox. 1 kg), ideal para compartir.",
                    List.of(),
                    new BigDecimal("44.90"),
                    true
            );
            setImageIfExists(tomahawk, "static/img/dishes/tomahawk.png");

            Dish presaIberica = new Dish(
                    DishCategory.MEAT,
                    "Presa ibérica al carbón",
                    "Presa ibérica marcada a la brasa, muy jugosa.",
                    List.of(),
                    new BigDecimal("19.90"),
                    true
            );
            setImageIfExists(presaIberica, "static/img/dishes/presa-iberica.png");

            Dish parrillada = new Dish(
                    DishCategory.MEAT,
                    "Parrillada Fuego Lento",
                    "Selección de carnes a la brasa para 2 personas.",
                    List.of(),
                    new BigDecimal("39.90"),
                    true
            );
            setImageIfExists(parrillada, "static/img/dishes/parrillada.png");

            Dish burgerAngus = new Dish(
                    DishCategory.MEAT,
                    "Hamburguesa Black Angus",
                    "200g Black Angus, cheddar, bacon y salsa de la casa.",
                    List.of(Allergen.GLUTEN, Allergen.MILK, Allergen.EGG),
                    new BigDecimal("13.90"),
                    true
            );
            setImageIfExists(burgerAngus, "static/img/dishes/hamburguesa-angus.png");

            // ===== NEW STARTERS (sin morcilla) =====

            Dish provolone = new Dish(
                    DishCategory.STARTER,
                    "Provolone al horno",
                    "Queso provolone fundido con orégano y toque de brasa.",
                    List.of(Allergen.MILK),
                    new BigDecimal("8.90"),
                    true
            );
            setImageIfExists(provolone, "static/img/dishes/provolone.png");

            Dish chorizoCriollo = new Dish(
                    DishCategory.STARTER,
                    "Chorizo criollo a la brasa",
                    "Chorizo criollo marcado a la brasa, servido caliente.",
                    List.of(),
                    new BigDecimal("7.90"),
                    true
            );
            setImageIfExists(chorizoCriollo, "static/img/dishes/chorizo-criollo.png");

            Dish torreznos = new Dish(
                    DishCategory.STARTER,
                    "Torreznos crujientes",
                    "Torreznos tradicionales, crujientes y dorados.",
                    List.of(),
                    new BigDecimal("8.50"),
                    true
            );
            setImageIfExists(torreznos, "static/img/dishes/torreznos.png");

            Dish patatasCheddarBacon = new Dish(
                    DishCategory.STARTER,
                    "Patatas cheddar y bacon",
                    "Patatas con cheddar fundido y bacon crujiente.",
                    List.of(Allergen.MILK),
                    new BigDecimal("9.50"),
                    true
            );
            setImageIfExists(patatasCheddarBacon, "static/img/dishes/patatas-cheddar-bacon.png");

            // ===== NEW DESSERTS =====

            Dish brownie = new Dish(
                    DishCategory.DESSERT,
                    "Brownie con nueces y helado",
                    "Brownie templado con nueces y bola de helado.",
                    List.of(Allergen.GLUTEN, Allergen.EGG, Allergen.MILK, Allergen.NUTS),
                    new BigDecimal("6.90"),
                    true
            );
            setImageIfExists(brownie, "static/img/dishes/brownie.png");

            Dish coulant = new Dish(
                    DishCategory.DESSERT,
                    "Coulant de chocolate",
                    "Bizcocho de chocolate con corazón fundido.",
                    List.of(Allergen.GLUTEN, Allergen.EGG, Allergen.MILK),
                    new BigDecimal("6.50"),
                    true
            );
            setImageIfExists(coulant, "static/img/dishes/coulant-chocolate.png");

            Dish flan = new Dish(
                    DishCategory.DESSERT,
                    "Flan de huevo",
                    "Flan tradicional de huevo con caramelo.",
                    List.of(Allergen.EGG, Allergen.MILK),
                    new BigDecimal("4.90"),
                    true
            );
            setImageIfExists(flan, "static/img/dishes/flan-huevo.png");

            Dish cookie = new Dish(
                    DishCategory.DESSERT,
                    "Cookie caliente",
                    "Cookie templada con chips de chocolate.",
                    List.of(Allergen.GLUTEN, Allergen.EGG, Allergen.MILK),
                    new BigDecimal("5.90"),
                    true
            );
            setImageIfExists(cookie, "static/img/dishes/cookie-caliente.png");

            // ===== NEW DRINKS (sin sangría) =====

            Dish ribera = new Dish(
                    DishCategory.DRINK,
                    "Tinto Ribera del Duero",
                    "Copa de Ribera del Duero (tinto).",
                    List.of(),
                    new BigDecimal("3.80"),
                    true
            );
            setImageIfExists(ribera, "static/img/dishes/vino-ribera.png");

            Dish rioja = new Dish(
                    DishCategory.DRINK,
                    "Rioja crianza",
                    "Copa de Rioja crianza (tinto).",
                    List.of(),
                    new BigDecimal("3.60"),
                    true
            );
            setImageIfExists(rioja, "static/img/dishes/vino-crianza.png");

            Dish vinoCasa = new Dish(
                    DishCategory.DRINK,
                    "Vino tinto de la casa",
                    "Copa de vino tinto de la casa.",
                    List.of(),
                    new BigDecimal("2.90"),
                    true
            );
            setImageIfExists(vinoCasa, "static/img/dishes/vino-de-la-casa.png");

            Dish vinoBlanco = new Dish(
                    DishCategory.DRINK,
                    "Vino blanco Pescadito (semidulce)",
                    "Vino blanco semidulce, fresco y suave.",
                    List.of(),
                    new BigDecimal("3.40"),
                    true
            );
            setImageIfExists(vinoBlanco, "static/img/dishes/vino-blanco.png");

            Dish cervezaCasa = new Dish(
                    DishCategory.DRINK,
                    "Cerveza de la casa",
                    "Cerveza rubia bien fría.",
                    List.of(),
                    new BigDecimal("2.70"),
                    true
            );
            setImageIfExists(cervezaCasa, "static/img/dishes/cerveza-de-la-casa.png");

            Dish cervezaLimon = new Dish(
                    DishCategory.DRINK,
                    "Cerveza con limón",
                    "Cerveza con limón, refrescante y muy fría.",
                    List.of(),
                    new BigDecimal("2.90"),
                    true
            );
            setImageIfExists(cervezaLimon, "static/img/dishes/cerveza-limon.png");

            dishRepository.saveAll(List.of(
                    // old
                    chuleton, costillar, croquetas, tartaQueso,
                    entrecot, secreto, ensalada, agua,
                    // meats
                    tomahawk, presaIberica, parrillada, burgerAngus,
                    // starters
                    provolone, chorizoCriollo, torreznos, patatasCheddarBacon,
                    // desserts
                    brownie, coulant, flan, cookie,
                    // drinks
                    ribera, rioja, vinoCasa, vinoBlanco, cervezaCasa, cervezaLimon
            ));
        }

        // ---------------------------
        // ORDERS for kitchen board
        // ---------------------------
        if (orderRepository.count() == 0) {

            Dish tomahawk = findDishExact("Tomahawk a la brasa");
            Dish presa = findDishExact("Presa ibérica al carbón");
            Dish parrillada = findDishExact("Parrillada Fuego Lento");
            Dish burger = findDishExact("Hamburguesa Black Angus");

            Dish provolone = findDishExact("Provolone al horno");
            Dish chorizo = findDishExact("Chorizo criollo a la brasa");
            Dish torreznos = findDishExact("Torreznos crujientes");
            Dish patatas = findDishExact("Patatas cheddar y bacon");

            Dish brownie = findDishExact("Brownie con nueces y helado");
            Dish flan = findDishExact("Flan de huevo");

            Dish cerveza = findDishExact("Cerveza de la casa");
            Dish cervezaLimon = findDishExact("Cerveza con limón");
            Dish vinoBlanco = findDishExact("Vino blanco Pescadito (semidulce)");
            Dish rioja = findDishExact("Rioja crianza");

            // 1) SENT_TO_KITCHEN
            Order o1 = new Order(user);
            o1.setStatus(OrderStatus.SENT_TO_KITCHEN);
            o1.setTableNumber(7);
            o1.setCustomerNote("Presa al punto, por favor.");
            addItem(o1, presa, 1);
            addItem(o1, provolone, 1);
            addItem(o1, cerveza, 2);
            orderRepository.save(o1);

            // 2) IN_PROGRESS
            Order o2 = new Order(maria);
            o2.setStatus(OrderStatus.IN_PROGRESS);
            o2.setTableNumber(3);
            o2.setCustomerNote("Traer torreznos para compartir.");
            addItem(o2, parrillada, 1);
            addItem(o2, torreznos, 1);
            addItem(o2, cervezaLimon, 2);
            orderRepository.save(o2);

            // 3) READY
            Order o3 = new Order(juan);
            o3.setStatus(OrderStatus.READY);
            o3.setTableNumber(12);
            o3.setCustomerNote("Con patatas extra.");
            addItem(o3, burger, 2);
            addItem(o3, patatas, 1);
            addItem(o3, rioja, 2);
            orderRepository.save(o3);

            // 4) SENT_TO_KITCHEN (starter heavy)
            Order o4 = new Order(maria);
            o4.setStatus(OrderStatus.SENT_TO_KITCHEN);
            o4.setTableNumber(9);
            o4.setCustomerNote("Chorizo bien hecho.");
            addItem(o4, chorizo, 1);
            addItem(o4, provolone, 1);
            addItem(o4, vinoBlanco, 2);
            orderRepository.save(o4);

            // 5) IN_PROGRESS (premium cut)
            Order o5 = new Order(juan);
            o5.setStatus(OrderStatus.IN_PROGRESS);
            o5.setTableNumber(5);
            o5.setCustomerNote("Tomahawk muy hecho por fuera, jugoso por dentro.");
            addItem(o5, tomahawk, 1);
            addItem(o5, patatas, 1);
            addItem(o5, cerveza, 1);
            orderRepository.save(o5);

            // 6) READY (dessert test)
            Order o6 = new Order(admin);
            o6.setStatus(OrderStatus.READY);
            o6.setTableNumber(1);
            o6.setCustomerNote("Pedido de prueba (admin) con postre.");
            addItem(o6, burger, 1);
            addItem(o6, brownie, 1);
            addItem(o6, flan, 1);
            orderRepository.save(o6);
        }
    }

    private Dish findDishExact(String exactName) {
        return dishRepository.findByNameContainingIgnoreCase(exactName).stream()
                .filter(d -> d.getName().equalsIgnoreCase(exactName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dish not found: " + exactName));
    }

    private void addItem(Order order, Dish dish, int quantity) {
        OrderItem item = new OrderItem(dish, quantity, dish.getPrice());
        order.addItem(item);
    }

    private void setImageIfExists(Dish dish, String classpathLocation) {
        Image img = imageFromStaticSafe(classpathLocation);
        if (img != null) {
            dish.setImage(img);
        }
    }

    /**
     * Loads an image from classpath (resources/static/...) and stores it in an Image entity as BLOB.
     * If the file does not exist, returns null (so the app does not crash).
     */
    private Image imageFromStaticSafe(String classpathLocation) {
        try {
            ClassPathResource res = new ClassPathResource(classpathLocation);
            if (!res.exists()) return null;

            byte[] bytes = res.getInputStream().readAllBytes();
            Image img = new Image();
            img.setImageFile(new SerialBlob(bytes));
            return img;
        } catch (Exception e) {
            return null;
        }
    }
}