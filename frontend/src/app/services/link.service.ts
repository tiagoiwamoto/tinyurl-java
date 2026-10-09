import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { LinkForm, LinkView, PageResponse, StatsView } from '../models/link.model';

@Injectable({
  providedIn: 'root'
})
export class LinkService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/me`;

  private readonly changed = new Subject<void>();
  readonly changed$ = this.changed.asObservable();

  notifyChanged(): void {
    this.changed.next();
  }

  list(search?: string, page = 0, size = 10): Observable<PageResponse<LinkView>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'createdAt,desc');
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<PageResponse<LinkView>>(`${this.baseUrl}/links`, { params });
  }

  criar(form: LinkForm): Observable<LinkView> {
    return this.http.post<LinkView>(`${this.baseUrl}/links`, form);
  }

  atualizarSplash(id: number, showSplash: boolean): Observable<LinkView> {
    return this.http.patch<LinkView>(`${this.baseUrl}/links/${id}`, { showSplash });
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/links/${id}`);
  }

  stats(): Observable<StatsView> {
    return this.http.get<StatsView>(`${this.baseUrl}/stats`);
  }
}
