import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import {
  NbThemeModule,
  NbSidebarModule,
  NbMenuModule,
  NbToastrModule,
  NbDialogModule
} from '@nebular/theme';
import { NbEvaIconsModule } from '@nebular/eva-icons';
import { routes } from './app.routes';
import { authInterceptor } from './auth/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),
    importProvidersFrom(
      NbThemeModule.forRoot({ name: 'default' }),
      NbSidebarModule.forRoot(),
      NbMenuModule.forRoot(),
      NbToastrModule.forRoot(),
      NbDialogModule.forRoot(),
      NbEvaIconsModule
    )
  ]
};
