import { Component, inject, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Observable, Subscription } from 'rxjs';
import {
  NbActionsModule,
  NbDialogService,
  NbIconModule,
  NbLayoutModule,
  NbMenuItem,
  NbMenuModule,
  NbMenuService,
  NbSidebarModule,
  NbSidebarService,
  NbUserModule
} from '@nebular/theme';
import { AuthService, AuthUser } from '../auth/auth.service';
import { LinkFormDialogComponent } from '../pages/link-form-dialog/link-form-dialog.component';

@Component({
  selector: 'app-layout',
  imports: [
    RouterOutlet,
    AsyncPipe,
    NbActionsModule,
    NbIconModule,
    NbLayoutModule,
    NbMenuModule,
    NbSidebarModule,
    NbUserModule
  ],
  template: `
    <nb-layout>
      <nb-layout-header fixed>
        <nb-actions>
          <nb-action icon="menu-2-outline" class="menu-toggle" (click)="toggleSidebar()" title="Menu"></nb-action>
          <nb-action>
            <span class="brand">tinylink</span>
          </nb-action>
        </nb-actions>
        <nb-actions class="ms-auto" fullWidth>
          <nb-action class="user-action">
            <nb-user [name]="(user$ | async)?.username ?? ''" [title]="(user$ | async)?.role ?? ''"></nb-user>
          </nb-action>
          <nb-action icon="log-out-outline" (click)="logout()" title="Sair"></nb-action>
        </nb-actions>
      </nb-layout-header>

      <nb-sidebar state="collapsed">
        <nb-menu [items]="menuItems"></nb-menu>
      </nb-sidebar>

      <nb-layout-column>
        <router-outlet></router-outlet>
      </nb-layout-column>
    </nb-layout>
  `,
  styles: [`
    .brand {
      font-size: 1.25rem;
      font-weight: 700;
    }

    .user-action {
      pointer-events: none;
    }

    .menu-toggle,
    nb-action[title="Sair"] {
      cursor: pointer;
    }
  `]
})
export class LayoutComponent implements OnDestroy {
  private authService = inject(AuthService);
  private menuService = inject(NbMenuService);
  private sidebarService = inject(NbSidebarService);
  private dialog = inject(NbDialogService);
  private menuSubscription: Subscription;

  user$: Observable<AuthUser | null> = this.authService.currentUser$;

  menuItems: NbMenuItem[] = [
    { title: 'Dashboard', icon: 'home-outline', link: '/dashboard' },
    { title: 'Meus links', icon: 'link-2-outline', link: '/links' },
    { title: 'Encurtar URL', icon: 'plus-outline' }
  ];

  constructor() {
    this.menuSubscription = this.menuService.onItemClick().subscribe(({ item }) => {
      if (item.title === 'Encurtar URL') {
        this.dialog.open(LinkFormDialogComponent, { closeOnBackdropClick: false });
        this.sidebarService.collapse();
      }
    });
  }

  ngOnDestroy(): void {
    this.menuSubscription.unsubscribe();
  }

  toggleSidebar(): void {
    this.sidebarService.toggle();
  }

  logout(): void {
    this.authService.logout();
  }
}
