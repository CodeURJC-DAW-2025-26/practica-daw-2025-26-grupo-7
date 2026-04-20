export interface User {
  id: number;
  username: string;
  email: string;
  roles: string[];
  imageId: number | null;
}
