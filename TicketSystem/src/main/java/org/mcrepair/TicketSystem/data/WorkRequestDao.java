package org.mcrepair.TicketSystem.data;

import org.mcrepair.TicketSystem.models.WorkRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface WorkRequestDao extends CrudRepository<WorkRequest, Integer> {
}
