package com.restAPI.Messages.Repo;

import com.restAPI.Messages.Models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long > {
}
