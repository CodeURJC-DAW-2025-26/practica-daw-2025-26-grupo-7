export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message: string;
  accessToken: string | null;
}
