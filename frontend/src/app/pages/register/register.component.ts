import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NbButtonModule, NbCardModule, NbInputModule, NbLayoutModule } from '@nebular/theme';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, NbButtonModule, NbCardModule, NbInputModule, NbLayoutModule],
  template: `
    <nb-layout>
      <nb-layout-column class="register-column">
        <nb-card class="register-card">
          <nb-card-header class="text-center">
            <h4 class="m-0">Criar conta</h4>
          </nb-card-header>
          <nb-card-body>
            @if (erro) {
              <p class="erro">{{ erro }}</p>
            }
            <form (ngSubmit)="registrar()">
              <div class="campo">
                <label class="label" for="username">Usuário</label>
                <input nbInput fullWidth type="text" id="username" name="username"
                       [(ngModel)]="username" required minlength="3">
              </div>
              <div class="campo">
                <label class="label" for="email">Email</label>
                <input nbInput fullWidth type="email" id="email" name="email"
                       [(ngModel)]="email">
              </div>
              <div class="campo">
                <label class="label" for="password">Senha</label>
                <input nbInput fullWidth type="password" id="password" name="password"
                       [(ngModel)]="password" required minlength="8">
              </div>
              <button nbButton status="primary" size="large" fullWidth [disabled]="registrando">
                {{ registrando ? 'Cadastrando...' : 'Cadastrar' }}
              </button>
            </form>
          </nb-card-body>
          <nb-card-footer class="text-center">
            Já tem conta? <a routerLink="/login">Entrar</a>
          </nb-card-footer>
        </nb-card>
      </nb-layout-column>
    </nb-layout>
  `,
  styles: [`
    .register-column {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 90vh;
    }

    .register-card {
      width: 100%;
      max-width: 30rem;
    }

    .campo {
      margin-bottom: 1rem;
    }

    .erro {
      color: #ff3d71;
      text-align: center;
    }
  `]
})
export class RegisterComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  email = '';
  password = '';
  registrando = false;
  erro = '';

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  registrar(): void {
    this.registrando = true;
    this.erro = '';
    this.authService.register(this.username, this.email, this.password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        this.registrando = false;
        this.erro = err.status === 409
          ? 'Nome de usuário já está em uso.'
          : 'Falha ao cadastrar. Verifique os dados.';
      }
    });
  }
}
