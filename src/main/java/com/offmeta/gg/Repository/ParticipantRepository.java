package com.offmeta.gg.Repository;

import com.offmeta.gg.Entity.ParticipantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {
}

