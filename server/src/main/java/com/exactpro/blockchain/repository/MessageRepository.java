package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MessageRepository extends ReactiveCrudRepository<Message, String> {
}
