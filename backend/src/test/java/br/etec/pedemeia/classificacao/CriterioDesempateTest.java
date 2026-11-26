package br.etec.pedemeia.classificacao;

import br.etec.pedemeia.classificacao.CriterioDesempate.Candidato;
import br.etec.pedemeia.classificacao.CriterioDesempate.Posicionado;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testes unitários do desempate P01 (ADR-003). Sem banco. */
class CriterioDesempateTest {

    private static Candidato c(long id, int ano, String nota, String nascimento, String nome) {
        return new Candidato(id, ano, new BigDecimal(nota), LocalDate.parse(nascimento), nome);
    }

    private static Posicionado pos(List<Posicionado> lista, long matriculaId) {
        return lista.stream().filter(p -> p.matriculaId() == matriculaId).findFirst().orElseThrow();
    }

    @Test
    void mvpCa18_posicoesSequenciaisEMaiorNotaPrimeiro() {
        // Ana 9,0 (15/03/2008), Bruno 9,0 (10/05/2008), Carla 7,0
        List<Candidato> lista = List.of(
                c(1, 2, "7.0", "2008-01-01", "Carla Mendes"),
                c(2, 2, "9.0", "2008-03-15", "Ana Souza"),
                c(3, 2, "9.0", "2008-05-10", "Bruno Lima"));
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(1, pos(r, 2).posicao());
        assertEquals(2, pos(r, 3).posicao());
        assertEquals(3, pos(r, 1).posicao());
        assertEquals(new BigDecimal("9.0"), pos(r, 2).notaFinal());
    }

    @Test
    void mvpCa19_empateDeNotaVenceQuemNasceuAntes() {
        List<Candidato> lista = List.of(
                c(1, 2, "9.0", "2008-03-15", "Ana Souza"),
                c(2, 2, "9.0", "2007-05-10", "Bruno Lima"));
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(1, pos(r, 2).posicao(), "Bruno (nascido em 2007) fica em 1º");
        assertEquals(2, pos(r, 1).posicao(), "Ana fica em 2º");
    }

    @Test
    void mvpCa20_empateDeNascimentoVenceOrdemAlfabetica() {
        List<Candidato> lista = List.of(
                c(1, 1, "8.0", "2008-01-01", "Diego Rocha"),
                c(2, 1, "8.0", "2008-01-01", "Carla Mendes"));
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(1, pos(r, 2).posicao(), "Carla fica em 1º");
        assertEquals(2, pos(r, 1).posicao(), "Diego fica em 2º");
    }

    @Test
    void mvpCa20_complemento_nomeIgualVenceMenorId() {
        List<Candidato> lista = List.of(
                c(9, 1, "8.0", "2008-01-01", "Carla Mendes"),
                c(4, 1, "8.0", "2008-01-01", "Carla Mendes"));
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(1, pos(r, 4).posicao());
        assertEquals(2, pos(r, 9).posicao());
    }

    @Test
    void mvpCa20_resultadoEhIgualEmDuasExecucoes() {
        List<Candidato> lista = List.of(
                c(1, 1, "8.0", "2008-01-01", "Carla Mendes"),
                c(2, 1, "8.0", "2008-01-01", "Diego Rocha"),
                c(3, 1, "8.0", "2008-01-01", "Ana Souza"));
        List<Posicionado> primeira = CriterioDesempate.classificar(new ArrayList<>(lista), 10);
        List<Candidato> invertida = new ArrayList<>(lista);
        java.util.Collections.reverse(invertida);
        List<Posicionado> segunda = CriterioDesempate.classificar(invertida, 10);
        assertEquals(primeira, segunda);
    }

    @Test
    void mvpCa22_fronteiraDoTop10SelecionaExatamenteDez() {
        // Ano 1 com 11 alunos. 10º e 11º têm nota 7,0. Ana nasceu antes de Bruno.
        List<Candidato> lista = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            lista.add(c(100 + i, 1, String.valueOf(9.0 - i * 0.1).substring(0, 3), "2008-06-01", "Aluno " + i));
        }
        lista.add(c(1, 1, "7.0", "2008-03-15", "Ana Souza"));
        lista.add(c(2, 1, "7.0", "2008-05-10", "Bruno Lima"));
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(10, pos(r, 1).posicao(), "Ana fica em 10º");
        assertEquals(11, pos(r, 2).posicao(), "Bruno fica em 11º");
        assertTrue(pos(r, 1).selecionado());
        assertFalse(pos(r, 2).selecionado());
        assertEquals(10, r.stream().filter(Posicionado::selecionado).count());
    }

    @Test
    void mvpCa21_selecionaSomenteOsDezPrimeirosDoAno() {
        List<Candidato> lista = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            lista.add(c(i + 1, 2, String.valueOf(9.5 - i * 0.25), "2008-01-01", "Aluno " + i));
        }
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(10, r.stream().filter(Posicionado::selecionado).count());
        assertTrue(r.stream().filter(p -> p.posicao() <= 10).allMatch(Posicionado::selecionado));
        assertTrue(r.stream().filter(p -> p.posicao() > 10).noneMatch(Posicionado::selecionado));
    }

    @Test
    void mvpCa17_posicoesSaoIndependentesPorAno() {
        List<Candidato> lista = new ArrayList<>();
        for (int ano = 1; ano <= 3; ano++) {
            for (int i = 0; i < 6; i++) {
                lista.add(c(ano * 100L + i, ano, String.valueOf(9.0 - i * 0.5), "2008-01-01", "A" + ano + i));
            }
        }
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        for (int ano = 1; ano <= 3; ano++) {
            final int a = ano;
            List<Integer> posicoes = r.stream().filter(p -> p.anoEscolar() == a)
                    .map(Posicionado::posicao).sorted().toList();
            assertEquals(List.of(1, 2, 3, 4, 5, 6), posicoes);
        }
    }

    @Test
    void mvpCa23_anoComMenosDeDezAlunosSelecionaTodos() {
        List<Candidato> lista = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            lista.add(c(i + 1, 3, String.valueOf(9.0 - i * 0.5), "2008-01-01", "Aluno " + i));
        }
        List<Posicionado> r = CriterioDesempate.classificar(lista, 10);
        assertEquals(7, r.stream().filter(Posicionado::selecionado).count());
    }

    @Test
    void chaveDeNomeIgnoraAcentoECaixa() {
        assertEquals("JOSE", CriterioDesempate.chaveNome("José"));
        assertEquals("CONCEICAO", CriterioDesempate.chaveNome("Conceição"));
        assertEquals(CriterioDesempate.chaveNome("Ana Souza"), CriterioDesempate.chaveNome("ANA SOUZA"));
        assertNotEquals(CriterioDesempate.chaveNome("Ana"), CriterioDesempate.chaveNome("Bruno"));
    }
}
