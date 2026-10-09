import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbDialogService,
  NbIconModule,
  NbInputModule,
  NbSpinnerModule,
  NbToastrService
} from '@nebular/theme';
import { LinkService } from '../../services/link.service';
import { LinkView, PageResponse } from '../../models/link.model';
import { LinkFormDialogComponent } from '../link-form-dialog/link-form-dialog.component';

@Component({
  selector: 'app-links',
  imports: [
    DatePipe,
    FormsModule,
    NbButtonModule,
    NbCardModule,
    NbCheckboxModule,
    NbIconModule,
    NbInputModule,
    NbSpinnerModule
  ],
  template: `
    <nb-card>
      <nb-card-header class="header">
        <h5 class="m-0">Meus links</h5>
        <button nbButton status="success" (click)="abrirFormulario()">Encurtar URL</button>
      </nb-card-header>
      <nb-card-body>
        <div class="busca">
          <input nbInput fullWidth type="text" placeholder="Buscar por URL..."
                 [(ngModel)]="busca" (keyup.enter)="pesquisar()">
          <button nbButton status="primary" (click)="pesquisar()">
            <nb-icon icon="search-outline"></nb-icon>
          </button>
        </div>

        @if (page) {
          <div class="table-responsive">
            <table class="link-table">
              <thead>
                <tr>
                  <th>Link curto</th>
                  <th>Destino</th>
                  <th>Hits</th>
                  <th>Splash</th>
                  <th>Criado em</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                @for (link of page.content; track link.id) {
                  <tr>
                    <td><a [href]="link.shortUrl" target="_blank">{{ link.code }}</a></td>
                    <td class="truncate" [title]="link.fullUrl">{{ link.fullUrl }}</td>
                    <td>{{ link.hitCount }}</td>
                    <td>
                      <nb-checkbox [checked]="link.showSplash"
                                   (checkedChange)="alternarSplash(link, $event)">
                      </nb-checkbox>
                    </td>
                    <td>{{ link.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
                    <td>
                      <button nbButton ghost status="danger" size="small" (click)="excluir(link)" title="Excluir">
                        <nb-icon icon="trash-2-outline"></nb-icon>
                      </button>
                    </td>
                  </tr>
                } @empty {
                  <tr><td colspan="6">Nenhum link encontrado.</td></tr>
                }
              </tbody>
            </table>
          </div>

          @if (page.totalPages > 1) {
            <div class="paginacao">
              <button nbButton status="basic" size="small" [disabled]="page.page === 0"
                      (click)="irPara(page.page - 1)">Anterior</button>
              <span>Página {{ page.page + 1 }} de {{ page.totalPages }}</span>
              <button nbButton status="basic" size="small" [disabled]="page.page >= page.totalPages - 1"
                      (click)="irPara(page.page + 1)">Próxima</button>
            </div>
          }
        } @else {
          <div class="loading">
            <nb-spinner size="large"></nb-spinner>
          </div>
        }
      </nb-card-body>
    </nb-card>
  `,
  styles: [`
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .busca {
      display: flex;
      gap: 0.5rem;
      margin-bottom: 1rem;
      max-width: 32rem;
    }

    .truncate {
      max-width: 24rem;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .paginacao {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 1rem;
      margin-top: 1rem;
    }

    .loading {
      display: flex;
      justify-content: center;
      padding: 3rem;
    }
  `]
})
export class LinksComponent implements OnInit {
  private linkService = inject(LinkService);
  private toastr = inject(NbToastrService);
  private dialog = inject(NbDialogService);
  private destroyRef = inject(DestroyRef);

  page: PageResponse<LinkView> | null = null;
  busca = '';
  pageAtual = 0;

  ngOnInit(): void {
    this.carregar();
    this.linkService.changed$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.carregar());
  }

  carregar(): void {
    this.linkService.list(this.busca, this.pageAtual)
      .subscribe(page => this.page = page);
  }

  pesquisar(): void {
    this.pageAtual = 0;
    this.carregar();
  }

  irPara(pagina: number): void {
    this.pageAtual = pagina;
    this.carregar();
  }

  abrirFormulario(): void {
    this.dialog.open(LinkFormDialogComponent, { closeOnBackdropClick: false });
  }

  alternarSplash(link: LinkView, showSplash: boolean): void {
    this.linkService.atualizarSplash(link.id, showSplash).subscribe({
      next: () => {
        link.showSplash = showSplash;
        this.toastr.success('Link atualizado.', 'Sucesso');
      },
      error: () => this.toastr.danger('Falha ao atualizar link.', 'Erro')
    });
  }

  excluir(link: LinkView): void {
    if (!confirm(`Deseja mesmo remover o link ${link.code}?`)) {
      return;
    }
    this.linkService.excluir(link.id).subscribe({
      next: () => {
        this.toastr.success('Link removido.', 'Sucesso');
        this.carregar();
      },
      error: () => this.toastr.danger('Falha ao remover link.', 'Erro')
    });
  }
}
