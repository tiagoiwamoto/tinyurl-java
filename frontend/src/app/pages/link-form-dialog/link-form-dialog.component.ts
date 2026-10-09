import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbDialogRef,
  NbIconModule,
  NbInputModule,
  NbToastrService
} from '@nebular/theme';
import { LinkService } from '../../services/link.service';
import { LinkForm } from '../../models/link.model';

@Component({
  selector: 'app-link-form-dialog',
  imports: [
    FormsModule,
    NbButtonModule,
    NbCardModule,
    NbCheckboxModule,
    NbIconModule,
    NbInputModule
  ],
  template: `
    <nb-card class="dialog-card">
      <nb-card-header class="dialog-header">
        <h5 class="m-0">Encurtar URL</h5>
        <button nbButton ghost status="basic" class="fechar" (click)="ref.close()">
          <nb-icon icon="close-outline"></nb-icon>
        </button>
      </nb-card-header>
      <nb-card-body>
        <form (ngSubmit)="salvar()" #f="ngForm">
          <div class="campo">
            <label class="label" for="url">URL de destino</label>
            <input nbInput fullWidth type="text" id="url" name="url" required
                   placeholder="https://exemplo.com/pagina" [(ngModel)]="form.url">
          </div>
          <div class="campo">
            <nb-checkbox name="showSplash" [(ngModel)]="form.showSplash">
              Exibir página de splash antes de redirecionar
            </nb-checkbox>
          </div>
        </form>
      </nb-card-body>
      <nb-card-footer class="dialog-footer">
        <button nbButton status="primary" (click)="salvar()" [disabled]="salvando || !form.url">
          {{ salvando ? 'Salvando...' : 'Encurtar' }}
        </button>
        <button nbButton status="basic" (click)="ref.close()">Cancelar</button>
      </nb-card-footer>
    </nb-card>
  `,
  styles: [`
    :host {
      display: block;
      width: min(32rem, 95vw);
    }

    .dialog-card {
      max-height: 90vh;
      display: flex;
      flex-direction: column;
    }

    .dialog-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .fechar {
      margin-right: -0.5rem;
    }

    .campo {
      margin-bottom: 1rem;
    }

    .dialog-footer {
      display: flex;
      gap: 0.5rem;
      justify-content: flex-end;
    }
  `]
})
export class LinkFormDialogComponent {
  protected ref = inject(NbDialogRef<LinkFormDialogComponent>);
  private linkService = inject(LinkService);
  private toastr = inject(NbToastrService);

  form: LinkForm = { showSplash: true };
  salvando = false;

  salvar(): void {
    if (!this.form.url) {
      return;
    }
    this.salvando = true;
    this.linkService.criar(this.form).subscribe({
      next: (link) => {
        this.salvando = false;
        this.toastr.success(`Link criado: ${link.shortUrl}`, 'Sucesso');
        this.linkService.notifyChanged();
        this.ref.close(true);
      },
      error: () => {
        this.salvando = false;
        this.toastr.danger('Falha ao encurtar URL.', 'Erro');
      }
    });
  }
}
