package com.logimarui.reposicoes.core.repository;

public interface StorageRepository {
    String salvarImagemAvaria(byte[] conteudo, String nomeArquivo);
}
