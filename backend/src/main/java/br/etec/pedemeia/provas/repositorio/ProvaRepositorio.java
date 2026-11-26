package br.etec.pedemeia.provas.repositorio;

import br.etec.pedemeia.provas.entidade.Prova;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

import java.util.Optional;

@ApplicationScoped
public class ProvaRepositorio implements PanacheRepository<Prova> {

    /** SELECT ... FOR UPDATE na prova (ADR-004): serializa notas e classificação da prova. */
    public Optional<Prova> bloquear(long id) {
        return Optional.ofNullable(getEntityManager().find(Prova.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    public io.quarkus.hibernate.orm.panache.PanacheQuery<Prova> listar(Long periodoLetivoId) {
        if (periodoLetivoId == null) {
            return find("order by dataAplicacao desc, id desc");
        }
        return find("periodo.id = ?1 order by dataAplicacao desc, id desc", periodoLetivoId);
    }
}
