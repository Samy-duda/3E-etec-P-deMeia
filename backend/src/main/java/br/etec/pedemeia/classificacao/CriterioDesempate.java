package br.etec.pedemeia.classificacao;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Ordenação da classificação e desempate P01 (ADR-003, 02-arquitetura.md, seção 6).
 * Java puro, sem banco e sem framework. Ordem da chave:
 *   1. nota final decrescente (BigDecimal pelo valor, sem considerar a escala);
 *   2. data de nascimento crescente (mais antiga primeiro);
 *   3. nome crescente, pela chave sem acento e em caixa alta, comparada por código Unicode;
 *   4. id da matrícula crescente.
 * Posições são sequenciais por ano escolar, sem ex-aequo (MVP-CA18).
 * Premissa P01: validar com a instituição (Q01). Mudar a regra é mudar só ORDEM.
 */
public final class CriterioDesempate {

    private CriterioDesempate() {
    }

    /** Dados de um candidato: uma nota de prova de uma matrícula. */
    public record Candidato(long matriculaId, int anoEscolar, BigDecimal notaFinal,
                            LocalDate dataNascimento, String nome) {
    }

    /** Resultado: posição sequencial no ano escolar e seleção (posição até as vagas). */
    public record Posicionado(long matriculaId, int anoEscolar, int posicao, BigDecimal notaFinal,
                              boolean selecionado) {
    }

    public static final Comparator<Candidato> ORDEM = Comparator
            .comparing(Candidato::notaFinal, Comparator.reverseOrder())
            .thenComparing(Candidato::dataNascimento)
            .thenComparing(c -> chaveNome(c.nome()))
            .thenComparingLong(Candidato::matriculaId);

    /** Nome sem acentos, em caixa alta. "Ana" e "Âna" têm a mesma chave (ADR-003). */
    public static String chaveNome(String nome) {
        String semAcento = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toUpperCase(Locale.ROOT);
    }

    /** Classifica por ano escolar e marca como selecionados os que estão até {@code vagasPorAno}. */
    public static List<Posicionado> classificar(List<Candidato> candidatos, int vagasPorAno) {
        Map<Integer, List<Candidato>> porAno = candidatos.stream()
                .collect(Collectors.groupingBy(Candidato::anoEscolar, TreeMap::new, Collectors.toList()));
        List<Posicionado> resultado = new ArrayList<>();
        for (Map.Entry<Integer, List<Candidato>> ano : porAno.entrySet()) {
            List<Candidato> ordenados = new ArrayList<>(ano.getValue());
            ordenados.sort(ORDEM);
            for (int i = 0; i < ordenados.size(); i++) {
                Candidato c = ordenados.get(i);
                int posicao = i + 1;
                resultado.add(new Posicionado(c.matriculaId(), c.anoEscolar(), posicao, c.notaFinal(),
                        posicao <= vagasPorAno));
            }
        }
        return resultado;
    }
}
