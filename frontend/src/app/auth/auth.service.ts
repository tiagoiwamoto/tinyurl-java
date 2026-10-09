import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse } from '../models/link.model';

export interface AuthUser {
  username?: string;
  email?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private baseUrl = `${environment.apiUrl}/auth`;

  private readonly loggedIn = new BehaviorSubject<boolean>(this.hasSession());
  private readonly currentUser = new BehaviorSubject<AuthUser | null>(this.readUser());

  readonly isLoggedIn$ = this.loggedIn.asObservable();
  readonly currentUser$ = this.currentUser.asObservable();

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { username, password })
      .pipe(tap(response => this.persist(response)));
  }

  register(username: string, email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, { username, email, password })
      .pipe(tap(response => this.persist(response)));
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  get accessToken(): string {
    return this.getToken() || '';
  }

  isAuthenticated(): boolean {
    return this.hasSession();
  }

  getUserProfile(): AuthUser | null {
    return this.currentUser.value;
  }

  private persist(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    this.loggedIn.next(true);
    this.currentUser.next({ username: response.username, email: response.email, role: response.role });
  }

  private clearSession(): void {
    this.loggedIn.next(false);
    this.currentUser.next(null);
    localStorage.removeItem('token');
  }

  private hasSession(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return !payload.exp || payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  private readUser(): AuthUser | null {
    const token = this.getToken();
    if (!token || !this.hasSession()) {
      return null;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return { username: payload['sub'], role: payload['role'] };
    } catch {
      return null;
    }
  }
}
