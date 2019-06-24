package org.mcrepair.TicketSystem.data;

import org.mcrepair.TicketSystem.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserDao extends CrudRepository<User, Integer> {

    List<User> findByEmail(String email);
}
