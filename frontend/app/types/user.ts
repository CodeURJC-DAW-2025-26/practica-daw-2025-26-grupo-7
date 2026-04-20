export interface User {
  id: number;
  username: string;
  email: string;
  birthDate: string | null;
  roles: string[];
  banned: boolean;
  createdAt: string;
}
