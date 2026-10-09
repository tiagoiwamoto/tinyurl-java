import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NbButtonModule, NbCardModule, NbInputModule, NbLayoutModule } from '@nebular/theme';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, NbButtonModule, NbCardModule, NbInputModule, NbLayoutModule],
  template: `
    <nb-layout>
      <nb-layout-column class="login-column">
        <nb-card class="login-card">
          <nb-card-header class="text-center">
            <h4 class="m-0">tinylink</h4>
          </nb-card-header>
          <nb-card-body>
            <p class="subtitle">Encurtador de URLs</p>
            @if (erro) {
              <p class="erro">{{ erro }}</p>
            }
            <form (ngSubmit)="entrar()">
              <div class="campo">
                <label class="label" for="username">Usuário</label>
                <input nbInput fullWidth type="text" id="username" name="username"
                       [(ngModel)]="username" required>
              </div>
              <div class="campo">
                <label class="label" for="password">Senha</label>
                <input nbInput fullWidth type="password" id="password" name="password"
                       [(ngModel)]="password" required>
              </div>
              <button nbButton status="primary" size="large" fullWidth [disabled]="entrando">
                {{ entrando ? 'Entrando...' : 'Entrar' }}
              </button>
            </form>
          </nb-card-body>
          <nb-card-footer class="text-center">
            Não tem conta? <a routerLink="/register">Cadastre-se</a>
          </nb-card-footer>
        </nb-card>
      </nb-layout-column>
    </nb-layout>
  `,
  styles: [`
    .login-column {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 90vh;
    }

    .login-card {
      width: 100%;
      max-width: 30rem;
    }

    .subtitle {
      text-align: center;
      margin-bottom: 1.5rem;
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
export class LoginComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  entrando = false;
  erro = '';

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  entrar(): void {
    this.entrando = true;
    this.erro = '';
    this.authService.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.entrando = false;
        this.erro = 'Usuário ou senha inválidos.';
      }
    });
  }
}
