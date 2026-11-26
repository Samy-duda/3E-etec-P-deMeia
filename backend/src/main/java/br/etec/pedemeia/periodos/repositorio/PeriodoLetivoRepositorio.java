package br.etec.pedemeia.periodos.repositorio;

import br.etec.pedemeia.periodos.entidade.PeriodoLetivo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PeriodoLetivoRepositorio implements PanacheRepository<PeriodoLetivo> {
}
