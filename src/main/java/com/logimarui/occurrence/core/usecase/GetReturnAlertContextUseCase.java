package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.model.ReturnAlertContext;
import com.logimarui.occurrence.core.exception.ReturnContextNotFoundException;
import com.logimarui.occurrence.core.port.repository.OrderReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetReturnAlertContextUseCase {
    private final OrderReadRepository orderReadRepository;

    @Transactional(transactionManager = "readTransactionManager", readOnly = true)
    public ReturnAlertContext execute(Long customerId, Long invoiceNumber) {
        return orderReadRepository.findReturnContext(customerId, invoiceNumber)
                .orElseThrow(() -> new ReturnContextNotFoundException(customerId, invoiceNumber));
    }
}
