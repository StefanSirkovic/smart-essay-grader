export type EssayStatus = 'SUBMITTED' | 'GRADING' | 'GRADED' | 'FAILED';
export type Role = 'STUDENT' | 'TEACHER' | 'ADMIN';

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  role: Role;
}

export interface EssayResponse {
  id: number;
  title: string;
  text: string;
  status: EssayStatus;
  submittedAt: string;
  userId: number;
  email: string;
  score: number | null;
  feedback: string | null;
}

export interface DashboardResponse {
  totalEssays: number;
  essaysByStatus: Record<string, number>;
  averageScore: number | null;
  highestScore: number | null;
  lowestScore: number | null;
  recentEssays: EssayResponse[];
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface EssayRequest {
  title: string;
  text: string;
}
