package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<Message, String> {
    @Query("SELECT m.* FROM Message m INNER JOIN Transfer t ON m.transferId = t.transferId WHERE t.transferId = :transferId")
    Flux<Message> findByTransferId(@Param("transferId") int transferId);
}
