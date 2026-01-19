package com.logimarui.replenishment.core.repository;

import java.io.InputStream;

public interface ReplenishmentImageStorageRepository {
    String save(Long lineReplenishmentId, InputStream imagemStream, String contentType);
    InputStream load(String imagePath);
    void delete(String imagePath);
}
