export interface LinkView {
  id: number;
  code: string;
  shortUrl: string;
  fullUrl: string;
  resolvedIp?: string;
  showSplash: boolean;
  hitCount: number;
  createdAt: string;
}

export interface LinkForm {
  url?: string;
  showSplash?: boolean;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface RecentHit {
  id: number;
  code: string;
  hitAt: string;
  visitorIp?: string;
}

export interface StatsView {
  totalLinks: number;
  totalHits: number;
  topLinks: LinkView[];
  recentHits: RecentHit[];
}

export interface AuthResponse {
  token: string;
  username: string;
  email?: string;
  role: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  message: string;
  path: string;
  violations: string[];
}
