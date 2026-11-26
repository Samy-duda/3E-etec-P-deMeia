package br.etec.pedemeia.classificacao;

import br.etec.pedemeia.classificacao.dto.ClassificacaoItemResposta;
import br.etec.pedemeia.classificacao.entidade.Classificacao;

public final class Mapeador {

    private Mapeador() {
    }

    /** dataNascimento nunca sai em classificação (contrato). matriculaEscolar só com dados pessoais. */
    public static ClassificacaoItemResposta paraItem(Classificacao c, boolean verDadosPessoais) {
        return new ClassificacaoItemResposta(
                c.getMatricula().getId(),
                c.getMatricula().getAnoEscolar(),
                c.getPosicao(),
                c.getNotaFinal(),
                c.isSelecionado(),
                c.getMatricula().getAluno().getNome(),
                verDadosPessoais ? c.getMatricula().getAluno().getMatriculaEscolar() : null);
    }
}
