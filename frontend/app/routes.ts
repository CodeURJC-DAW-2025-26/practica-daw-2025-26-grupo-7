import { type RouteConfig, index, route, layout } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("login", "routes/login.tsx"),
  route("*", "routes/notFound.tsx"),
  route("menu", "routes/menu.tsx"),
  route("menu/:id", "routes/menuDetail.tsx"),

  layout("routes/_auth.tsx", [
    route("cart", "routes/cart.tsx"),
    route("orders", "routes/orders.tsx"),
    route("order-sent", "routes/orderSent.tsx"),
    route("profile", "routes/profile.tsx"),
    route("profile/edit", "routes/profileEdit.tsx"),
  ]),

  layout("routes/_admin.tsx", [
    route("admin/products", "routes/adminProducts.tsx"),
    route("admin/products/new", "routes/adminProductNew.tsx"),
    route("admin/products/:id/edit", "routes/adminProductEdit.tsx"),
    route("admin/orders", "routes/adminOrders.tsx"),
    route("admin/orders/:id", "routes/adminOrderDetail.tsx"),
    route("admin/kitchen", "routes/adminKitchen.tsx"),
    route("admin/users", "routes/adminUsers.tsx"),
    route("admin/dashboard", "routes/adminDashboard.tsx"),
  ]),
] satisfies RouteConfig;
