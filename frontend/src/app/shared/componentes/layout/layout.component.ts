import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { PERFIS_ADMINISTRADOR, PERFIS_OPERADORES, PERFIS_TODOS, Perfil } from '../../../core/auth/perfil';

interface ItemMenu {
  rotulo: string;
  rota: string;
  perfis: readonly Perfil[];
}

/** Menu do topo. Mostra só os itens do perfil atual (a API continua sendo a autoridade). */
@Component({
  selector: 'app-layout',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LayoutComponent {
  private readonly auth = inject(AuthService);

  private readonly itens: readonly ItemMenu[] = [
    { rotulo: 'Processos seletivos', rota: '/processos-seletivos', perfis: PERFIS_TODOS },
    { rotulo: 'Alunos', rota: '/alunos', perfis: PERFIS_OPERADORES },
    { rotulo: 'Períodos letivos', rota: '/periodos-letivos', perfis: PERFIS_OPERADORES },
    { rotulo: 'Provas', rota: '/provas', perfis: PERFIS_OPERADORES },
    { rotulo: 'Usuários', rota: '/usuarios', perfis: PERFIS_ADMINISTRADOR },
  ];

  readonly usuario = this.auth.usuario;
  readonly itensVisiveis = computed(() => this.itens.filter((item) => this.auth.temPerfil(item.perfis)));

  sair(): void {
    this.auth.logout();
  }
}
