package br.etec.pedemeia.processos.repositorio;

import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

import java.util.Optional;

@ApplicationScoped
public class ProcessoSeletivoRepositorio implements PanacheRepository<ProcessoSeletivo> {

    /** SELECT ... FOR UPDATE no processo (ADR-004). */
    public Optional<ProcessoSeletivo> bloquear(long id) {
        return Optional.ofNullable(getEntityManager().find(ProcessoSeletivo.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    public Optional<ProcessoSeletivo> buscarPorProva(long provaId) {
        return find("prova.id", provaId).firstResultOptional();
    }

    /**
     * Existe classificação gerada para a prova? Consulta à tabela CLASSIFICACOES pela entidade
     * Classificacao (JPQL), sem depender do pacote classificacao, para evitar ciclo de módulos.
     */
    public boolean existeClassificacaoParaProva(long provaId) {
        Long total = getEntityManager()
                .createQuery("select count(c) from Classificacao c where c.processo.prova.id = :provaId", Long.class)
                .setParameter("provaId", provaId)
                .getSingleResult();
        return total > 0;
    }

    public PanacheQuery<ProcessoSeletivo> listar(Long periodoLetivoId) {
        if (periodoLetivoId == null) {
            return find("order by id desc");
        }
        return find("periodo.id = :periodo order by id desc", Parameters.with("periodo", periodoLetivoId));
    }
}
