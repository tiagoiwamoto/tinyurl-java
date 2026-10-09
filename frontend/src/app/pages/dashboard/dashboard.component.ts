import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NbCardModule, NbIconModule, NbSpinnerModule } from '@nebular/theme';
import { LinkService } from '../../services/link.service';
import { StatsView } from '../../models/link.model';

interface CardResumo {
  titulo: string;
  valor: string;
  cor: string;
  icon: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [
    DatePipe,
    NbCardModule,
    NbIconModule,
    NbSpinnerModule
  ],
  template: `
    @if (stats) {
      <div class="cards">
        @for (card of cards; track card.titulo) {
          <nb-card class="stat">
            <nb-card-body class="stat-body">
              <div class="stat-icon" [style.background-color]="card.cor">
                <nb-icon [icon]="card.icon"></nb-icon>
              </div>
              <div class="stat-info">
                <span class="stat-label">{{ card.titulo }}</span>
                <span class="stat-value" [style.color]="card.cor">{{ card.valor }}</span>
              </div>
            </nb-card-body>
          </nb-card>
        }
      </div>

      <div class="grid">
        <nb-card>
          <nb-card-header>
            <h5 class="m-0">Links mais acessados</h5>
          </nb-card-header>
          <nb-card-body class="table-responsive">
            <table class="link-table">
              <thead>
                <tr>
                  <th>Link</th>
                  <th>Destino</th>
                  <th>Hits</th>
                </tr>
              </thead>
              <tbody>
                @for (link of stats.topLinks; track link.id) {
                  <tr>
                    <td><a [href]="link.shortUrl" target="_blank">{{ link.shortUrl }}</a></td>
                    <td class="truncate">{{ link.fullUrl }}</td>
                    <td>{{ link.hitCount }}</td>
                  </tr>
                } @empty {
                  <tr><td colspan="3">Nenhum link ainda.</td></tr>
                }
              </tbody>
            </table>
          </nb-card-body>
        </nb-card>

        <nb-card>
          <nb-card-header>
            <h5 class="m-0">Acessos recentes</h5>
          </nb-card-header>
          <nb-card-body class="table-responsive">
            <table class="link-table">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Quando</th>
                  <th>IP</th>
                </tr>
              </thead>
              <tbody>
                @for (hit of stats.recentHits; track hit.id) {
                  <tr>
                    <td>{{ hit.code }}</td>
                    <td>{{ hit.hitAt | date:'dd/MM/yyyy HH:mm' }}</td>
                    <td>{{ hit.visitorIp || '-' }}</td>
                  </tr>
                } @empty {
                  <tr><td colspan="3">Nenhum acesso ainda.</td></tr>
                }
              </tbody>
            </table>
          </nb-card-body>
        </nb-card>
      </div>
    } @else {
      <div class="loading">
        <nb-spinner size="giant"></nb-spinner>
      </div>
    }
  `,
  styles: [`
    .cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
      gap: 1rem;
    }

    .stat-body {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .stat-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 3rem;
      height: 3rem;
      border-radius: 0.5rem;
      color: #fff;
      font-size: 1.5rem;
      flex-shrink: 0;
    }

    .stat-info {
      display: flex;
      flex-direction: column;
      min-width: 0;
    }

    .stat-label {
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: #8f9bb3;
    }

    .stat-value {
      font-size: 1.25rem;
      font-weight: 700;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .truncate {
      max-width: 20rem;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .loading {
      display: flex;
      justify-content: center;
      padding: 4rem;
    }
  `]
})
export class DashboardComponent implements OnInit {
  private linkService = inject(LinkService);
  private destroyRef = inject(DestroyRef);

  stats: StatsView | null = null;

  ngOnInit(): void {
    this.carregar();
    this.linkService.changed$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.carregar());
  }

  get cards(): CardResumo[] {
    const s = this.stats;
    if (!s) {
      return [];
    }
    return [
      { titulo: 'Meus links', valor: String(s.totalLinks), cor: '#0095ff', icon: 'link-2-outline' },
      { titulo: 'Total de acessos', valor: String(s.totalHits), cor: '#00d68f', icon: 'trending-up-outline' }
    ];
  }

  private carregar(): void {
    this.linkService.stats().subscribe(stats => this.stats = stats);
  }
}
