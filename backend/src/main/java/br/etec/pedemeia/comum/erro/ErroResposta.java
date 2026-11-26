package br.etec.pedemeia.comum.erro;

import java.time.Instant;
import java.util.List;

/** Formato único de erro (ADR-002, schema ErroResposta do contrato). */
public record ErroResposta(
        Instant timestamp,
        int status,
        CodigoErro codigo,
        String mensagem,
        String caminho,
        List<CampoErro> campos) {

    public static ErroResposta de(int status, CodigoErro codigo, String mensagem, String caminho,
                                  List<CampoErro> campos) {
        return new ErroResposta(Instant.now(), status, codigo, mensagem, caminho, campos == null ? List.of() : campos);
    }
}
