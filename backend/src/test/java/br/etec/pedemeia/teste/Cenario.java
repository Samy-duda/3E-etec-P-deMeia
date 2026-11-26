package br.etec.pedemeia.teste;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Monta turmas com notas distintas e decrescentes: o primeiro colocado tem a maior nota. */
public final class Cenario {

    private Cenario() {
    }

    public static List<Api.Matriculado> turma(long periodoId, long provaId, int ano, int quantidade,
                                              String nascimento) {
        List<Api.Matriculado> lista = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Api.Matriculado m = Api.matriculado(periodoId, "Aluno " + ano + "-" + i, nascimento, ano);
            Api.lancarNota(provaId, m.matriculaId(), String.format(Locale.ROOT, "%.2f", 9.50 - i * 0.25))
                    .then().statusCode(201);
            lista.add(m);
        }
        return lista;
    }
}
