import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NbCardModule, NbLayoutModule, NbSpinnerModule } from '@nebular/theme';
import { LinkService } from '../../services/link.service';
import { ResolveView } from '../../models/link.model';

@Component({
  selector: 'app-redirect',
  imports: [NbCardModule, NbLayoutModule, NbSpinnerModule],
  template: `
    <nb-layout>
      <nb-layout-column class="redirect-column">
        <nb-card class="redirect-card">
          @if (erro) {
            <nb-card-header class="text-center">
              <h4 class="m-0">Link não encontrado</h4>
            </nb-card-header>
            <nb-card-body class="text-center">
              <p>O link <strong>/{{ code }}</strong> não existe ou foi removido.</p>
            </nb-card-body>
          } @else if (resolve) {
            <nb-card-header class="text-center">
              <h4 class="m-0">Redirecionando...</h4>
            </nb-card-header>
            <nb-card-body class="text-center">
              <p>Você será redirecionado em <strong>{{ segundos }}</strong> segundos.</p>
              <p>Destino: <a [href]="resolve.fullUrl">{{ resolve.fullUrl }}</a></p>
              @if (resolve.adHtml) {
                <div class="ad" [innerHTML]="resolve.adHtml"></div>
              }
            </nb-card-body>
          } @else {
            <nb-card-body class="loading">
              <nb-spinner size="large"></nb-spinner>
            </nb-card-body>
          }
        </nb-card>
      </nb-layout-column>
    </nb-layout>
  `,
  styles: [`
    .redirect-column {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 90vh;
    }

    .redirect-card {
      width: 100%;
      max-width: 40rem;
    }

    .text-center {
      text-align: center;
    }

    .ad {
      margin-top: 1.5rem;
    }

    .loading {
      display: flex;
      justify-content: center;
      padding: 2rem;
    }
  `]
})
export class RedirectComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private linkService = inject(LinkService);
  private timer: ReturnType<typeof setInterval> | null = null;

  code = '';
  resolve: ResolveView | null = null;
  segundos = 0;
  erro = false;

  ngOnInit(): void {
    this.code = this.route.snapshot.paramMap.get('code') ?? '';
    this.linkService.resolver(this.code).subscribe({
      next: resolve => {
        if (!resolve.showSplash) {
          window.location.href = resolve.fullUrl;
          return;
        }
        this.resolve = resolve;
        this.segundos = resolve.refreshRateSeconds;
        this.timer = setInterval(() => this.tick(), 1000);
      },
      error: () => this.erro = true
    });
  }

  ngOnDestroy(): void {
    this.pararTimer();
  }

  private tick(): void {
    this.segundos--;
    if (this.segundos <= 0) {
      this.pararTimer();
      window.location.href = this.resolve!.fullUrl;
    }
  }

  private pararTimer(): void {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }
}
