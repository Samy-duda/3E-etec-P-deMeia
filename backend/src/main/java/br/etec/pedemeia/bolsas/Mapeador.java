package br.etec.pedemeia.bolsas;

import br.etec.pedemeia.bolsas.dto.BolsaResposta;
import br.etec.pedemeia.bolsas.entidade.Bolsa;

public final class Mapeador {

    private Mapeador() {
    }

    public static BolsaResposta paraResposta(Bolsa b, int posicao, boolean verDadosPessoais) {
        return new BolsaResposta(
                b.getId(),
                b.getProcesso().getId(),
                b.getMatricula().getId(),
                b.getMatricula().getAnoEscolar(),
                posicao,
                b.getMatricula().getAluno().getNome(),
                verDadosPessoais ? b.getMatricula().getAluno().getMatriculaEscolar() : null,
                b.getValorMensal(),
                b.getSituacao(),
                b.getDataInicio(),
                null,
                verDadosPessoais ? b.getCriadaPor().getId() : null,
                verDadosPessoais ? b.getCriadaEm() : null);
    }
}
