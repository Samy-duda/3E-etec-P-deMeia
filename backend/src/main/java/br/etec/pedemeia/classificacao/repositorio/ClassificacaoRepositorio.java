package br.etec.pedemeia.classificacao.repositorio;

import br.etec.pedemeia.classificacao.entidade.Classificacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClassificacaoRepositorio implements PanacheRepository<Classificacao> {

    public boolean existeParaProcesso(long processoId) {
        return count("processo.id = ?1", processoId) > 0;
    }

    public Optional<Classificacao> buscarPorProcessoEMatricula(long processoId, long matriculaId) {
        return find("processo.id = ?1 and matricula.id = ?2", processoId, matriculaId).firstResultOptional();
    }

    /** Ordem do contrato: ano escolar crescente, depois posição crescente. */
    public List<Classificacao> listarPorProcesso(long processoId, Integer anoEscolar) {
        if (anoEscolar == null) {
            return find("select c from Classificacao c join fetch c.matricula m join fetch m.aluno "
                    + "where c.processo.id = ?1 order by m.anoEscolar, c.posicao", processoId).list();
        }
        return find("select c from Classificacao c join fetch c.matricula m join fetch m.aluno "
                + "where c.processo.id = ?1 and m.anoEscolar = ?2 order by m.anoEscolar, c.posicao",
                processoId, anoEscolar).list();
    }
}
