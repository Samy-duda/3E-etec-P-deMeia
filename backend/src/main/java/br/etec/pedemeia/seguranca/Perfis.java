package br.etec.pedemeia.seguranca;

/**
 * Perfis de acesso (D02). Os nomes batem com o CHECK de USUARIOS.perfil e com o contrato.
 * As anotações usam estas constantes: ADMIN e SECRETARIA para gestão (x-perfis),
 * e os três perfis para leitura de classificações e bolsas.
 */
public final class Perfis {

    public static final String ADMIN = "ADMIN";
    public static final String SECRETARIA = "SECRETARIA";
    public static final String CONSULTA = "CONSULTA";

    private Perfis() {
    }
}
