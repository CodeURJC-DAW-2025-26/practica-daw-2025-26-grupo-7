export enum DishCategory {
  STARTER = 'STARTER',
  MEAT = 'MEAT',
  DESSERT = 'DESSERT',
  DRINK = 'DRINK',
}

export interface DishImage {
  id: number;
}

export interface Dish {
  id: number;
  name: string;
  description: string;
  price: number;
  category: DishCategory;
  available: boolean;
  allergens: string[];
  image: DishImage | null;
}
