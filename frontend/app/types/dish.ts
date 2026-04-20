export enum DishCategory {
  STARTERS = 'STARTERS',
  MEAT = 'MEAT',
  FISH = 'FISH',
  DESSERTS = 'DESSERTS',
  DRINKS = 'DRINKS',
}

export interface Dish {
  id: number;
  name: string;
  description: string;
  price: number;
  category: DishCategory;
  available: boolean;
  imageId: number | null;
}
